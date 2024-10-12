package com.mindhealth.EmoMind

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddingMoodActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private var startX: Float = 0f
    private var endX: Float = 0f
    private var selectedEmotions: MutableList<String> = mutableListOf()
    private var selectedMood: String? = null
    private var userId: Long? = null

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_mood)

        databaseHelper = DatabaseHelper(this)

        val saveMoodButton = findViewById<Button>(R.id.buttonSaveMood)
        val commentEditText = findViewById<EditText>(R.id.editTextComment)

        val login = intent.getStringExtra("LOGIN")

        if (login != null) {
            userId = databaseHelper.getUserId(login)
            if (userId == null) {
                Toast.makeText(this, getString(R.string.user_not_found_error), Toast.LENGTH_SHORT).show()
                finish()
                return
            }
        } else {
            Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val textViewHorrible = findViewById<TextView>(R.id.textViewHorrible)
        val imageView5 = findViewById<ImageView>(R.id.imageView5)

        textViewHorrible.setOnClickListener {
            selectedMood = "Ужасно"
            Toast.makeText(this, getString(R.string.selected_mood_horrible), Toast.LENGTH_SHORT).show()
        }

        imageView5.setOnClickListener {
            selectedMood = "Ужасно"
            Toast.makeText(this, getString(R.string.selected_mood_horrible), Toast.LENGTH_SHORT).show()
        }

        val textViewBad = findViewById<TextView>(R.id.textViewBad)
        val imageView4 = findViewById<ImageView>(R.id.imageView4)

        textViewBad.setOnClickListener {
            selectedMood = "Плохо"
            Toast.makeText(this, getString(R.string.selected_mood_bad), Toast.LENGTH_SHORT).show()
        }

        imageView4.setOnClickListener {
            selectedMood = "Плохо"
            Toast.makeText(this, getString(R.string.selected_mood_bad), Toast.LENGTH_SHORT).show()
        }

        val textViewOkay = findViewById<TextView>(R.id.textViewOkay)
        val imageView11 = findViewById<ImageView>(R.id.imageView11)

        textViewOkay.setOnClickListener {
            selectedMood = "Так себе"
            Toast.makeText(this, getString(R.string.selected_mood_okay), Toast.LENGTH_SHORT).show()
        }

        imageView11.setOnClickListener {
            selectedMood = "Так себе"
            Toast.makeText(this, getString(R.string.selected_mood_okay), Toast.LENGTH_SHORT).show()
        }

        val textViewGood = findViewById<TextView>(R.id.textViewGood)
        val imageView7 = findViewById<ImageView>(R.id.imageView7)

        textViewGood.setOnClickListener {
            selectedMood = "Хорошо"
            Toast.makeText(this, getString(R.string.selected_mood_good), Toast.LENGTH_SHORT).show()
        }

        imageView7.setOnClickListener {
            selectedMood = "Хорошо"
            Toast.makeText(this, getString(R.string.selected_mood_good), Toast.LENGTH_SHORT).show()
        }

        val textViewSuper = findViewById<TextView>(R.id.textViewSuper)
        val imageView8 = findViewById<ImageView>(R.id.imageView8)

        textViewSuper.setOnClickListener {
            selectedMood = "Супер"
            Toast.makeText(this, getString(R.string.selected_mood_super), Toast.LENGTH_SHORT).show()
        }

        imageView8.setOnClickListener {
            selectedMood = "Супер"
            Toast.makeText(this, getString(R.string.selected_mood_super), Toast.LENGTH_SHORT).show()
        }

        saveMoodButton.setOnClickListener {
            if (login != null) {
                val userId = databaseHelper.getUserId(login)

                when {
                    userId == null -> {
                        Toast.makeText(this, getString(R.string.user_not_found_error), Toast.LENGTH_SHORT).show()
                    }
                    selectedMood == null -> {
                        Toast.makeText(this, getString(R.string.no_mood_selected_error), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        val mood = selectedMood!!
                        val comment = commentEditText.text.toString()
                        val emotionsString = selectedEmotions.joinToString(", ")

                        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

                        val isSuccess = databaseHelper.addMood(userId, mood, currentDate, comment, emotionsString)

                        if (isSuccess) {
                            Toast.makeText(this, getString(R.string.mood_saved_success), Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, getString(R.string.mood_save_error), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_SHORT).show()
            }
        }

        val emotionsButton = findViewById<Button>(R.id.buttonSelectEmotions)
        emotionsButton.setOnClickListener {
            showMultiSelectDialog()
        }

        window.decorView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    true
                }
                MotionEvent.ACTION_UP -> {
                    endX = event.x
                    when {
                        endX > startX -> swipeRight()
                        startX > endX -> swipeLeft()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun showMultiSelectDialog() {
        val emotions = resources.getStringArray(R.array.emotion_array)
        val checkedItems = BooleanArray(emotions.size) { false }

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.select_emotions_title))
            .setMultiChoiceItems(emotions, checkedItems) { _, which, isChecked ->
                if (isChecked) {
                    selectedEmotions.add(emotions[which])
                } else {
                    selectedEmotions.remove(emotions[which])
                }
            }
            .setPositiveButton(getString(R.string.done_button)) { dialog, _ -> dialog.dismiss() }
            .setNegativeButton(getString(R.string.cancel_button)) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun swipeRight() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.swipe_right)
        window.decorView.startAnimation(animation)

        val registerIntent = Intent(this, MainActivity::class.java)
        startActivity(registerIntent)
        finish()
    }

    private fun swipeLeft() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.swipe_left)
        window.decorView.startAnimation(animation)

        val loginIntent = Intent(this, MainActivity::class.java)
        startActivity(loginIntent)
        finish()
    }
}