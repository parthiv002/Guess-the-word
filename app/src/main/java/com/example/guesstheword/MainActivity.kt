package com.example.guesstheword

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var wordTextView: TextView
    private lateinit var lettersGrid: GridLayout
    private lateinit var resetButton: Button
    private lateinit var eraseButton: Button
    private lateinit var levelTextView: TextView
    private lateinit var checkBox1: CheckBox
    private lateinit var checkBox2: CheckBox
    private lateinit var checkBox3: CheckBox
    private lateinit var checkBox4: CheckBox
    private lateinit var checkBox5: CheckBox

    private var currentWord = ""
    private var displayedWord = ""
    private var missingLetters = listOf<Char>()
    private var guessedWord = ""
    private var guessedLetters = mutableListOf<Char>()
    private var level = 1
    private var correctGuessCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wordTextView = findViewById(R.id.wordTextView)
        lettersGrid = findViewById(R.id.lettersGrid)
        resetButton = findViewById(R.id.resetButton)
        eraseButton = findViewById(R.id.eraseButton)
        levelTextView = findViewById(R.id.levelTextView)

        // Initialize CheckBoxes
        checkBox1 = findViewById(R.id.checkBox1)
        checkBox2 = findViewById(R.id.checkBox2)
        checkBox3 = findViewById(R.id.checkBox3)
        checkBox4 = findViewById(R.id.checkBox4)
        checkBox5 = findViewById(R.id.checkBox5)

        // Disable user interaction with checkboxes
        checkBox1.isEnabled = false
        checkBox2.isEnabled = false
        checkBox3.isEnabled = false
        checkBox4.isEnabled = false
        checkBox5.isEnabled = false

        resetButton.setOnClickListener {
            resetGame()
        }

        eraseButton.setOnClickListener {
            eraseLetters()
        }

        startNewLevel()
    }

    private fun startNewLevel() {
        currentWord = generateRandomWord(level)
        missingLetters = getMissingLetters(currentWord, level)
        displayedWord = hideLetters(currentWord, missingLetters)
        guessedWord = displayedWord.replace(" ", "")  // Initial guessed word with underscores
        guessedLetters.clear()  // Clear previous guessed letters
        wordTextView.text = "Word: $displayedWord"

        populateLetterGrid(missingLetters)
    }

    private fun generateRandomWord(level: Int): String {
        val wordLength = when (level) {
            1 -> Random.nextInt(4, 6)  // Easy level: 4-5 letter words
            2 -> Random.nextInt(6, 8)  // Medium level: 6-7 letter words
            else -> Random.nextInt(8, 10)  // Hard level: 8-9 letter words
        }

        val vowels = "aeiou"
        val consonants = "bcdfghjklmnpqrstvwxyz"

        var randomWord = ""
        var isVowelNext = Random.nextBoolean()

        for (i in 0 until wordLength) {
            randomWord += if (isVowelNext) {
                vowels[Random.nextInt(vowels.length)]
            } else {
                consonants[Random.nextInt(consonants.length)]
            }
            isVowelNext = !isVowelNext
        }

        return randomWord.capitalize()
    }

    private fun getMissingLetters(word: String, level: Int): List<Char> {
        val numberOfMissing = Random.nextInt(1, 4)  // 1 to 3 letters missing
        return word.toList().shuffled().take(numberOfMissing)
    }

    private fun hideLetters(word: String, missingLetters: List<Char>): String {
        return word.map { if (missingLetters.contains(it)) '_' else it }.joinToString(" ")
    }

    private fun populateLetterGrid(letters: List<Char>) {
        lettersGrid.removeAllViews() // Clear previous buttons

        val shuffledLetters = letters.shuffled()

        for (letter in shuffledLetters) {
            val letterButton = Button(this)
            letterButton.text = letter.toString()

            letterButton.setOnClickListener {
                handleLetterClick(letter)
            }

            lettersGrid.addView(letterButton)
        }
    }

    private fun handleLetterClick(letter: Char) {
        if (missingLetters.contains(letter)) {
            // Replace the first underscore with the guessed letter
            val underscoreIndex = guessedWord.indexOf('_')
            guessedWord = guessedWord.replaceFirst('_', letter)
            displayedWord = displayedWord.replaceFirst('_', letter)
            guessedLetters.add(letter)  // Track guessed letter
            wordTextView.text = "Word: $displayedWord"

            // Check if all missing letters are filled
            if (!guessedWord.contains('_')) {
                checkWordMatch()  // Now check if the guessed word matches the original word
            }
        } else {
            Toast.makeText(this, "Incorrect letter! Try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkWordMatch() {
        if (guessedWord == currentWord) {
            // Correct word guessed
            correctGuessCount++
            fillCheckbox(correctGuessCount) // Fill checkbox for each correct guess

            if (correctGuessCount % 5 == 0) {
                level++  // Increase level every 5 correct guesses
                levelTextView.text = "Level: $level"
                // Untick all checkboxes after 5 correct guesses
                resetCheckboxes()
            }

            startNewLevel() // Proceed to next level
        } else {
            // Incorrect word guessed
            Toast.makeText(this, "Incorrect word! Try again.", Toast.LENGTH_SHORT).show()
            guessedWord = displayedWord.replace(" ", "")
        }
    }

    private fun fillCheckbox(correctGuessCount: Int) {
        when (correctGuessCount) {
            1 -> checkBox1.isChecked = true
            2 -> checkBox2.isChecked = true
            3 -> checkBox3.isChecked = true
            4 -> checkBox4.isChecked = true
            5 -> checkBox5.isChecked = true
        }
    }

    private fun resetCheckboxes() {
        checkBox1.isChecked = false
        checkBox2.isChecked = false
        checkBox3.isChecked = false
        checkBox4.isChecked = false
        checkBox5.isChecked = false
        correctGuessCount = 0 // Reset the count after unticking
    }

    private fun eraseLetters() {
        // Only guessed letters are erased, not app-generated letters
        guessedLetters.forEach { guessedLetter ->
            displayedWord = displayedWord.replaceFirst(guessedLetter, '_')
            guessedWord = guessedWord.replaceFirst(guessedLetter, '_')
        }
        guessedLetters.clear()  // Clear the list of guessed letters
        wordTextView.text = "Word: $displayedWord"
    }

    private fun resetGame() {
        level = 1
        correctGuessCount = 0
        levelTextView.text = "Level: $level"
        resetCheckboxes() // Reset checkboxes on game reset
        startNewLevel()
    }
}
