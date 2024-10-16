package com.mindhealth.EmoMind

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddingNoteActivity : AppCompatActivity() {
    private var startX: Float = 0f
    private var endX: Float = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_note)

        val databaseHelper = DatabaseHelper(this)

        val spinnerTopics: Spinner = findViewById(R.id.spinnerTopics)
        val topics = resources.getStringArray(R.array.topics_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, topics)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTopics.adapter = adapter

        val editTextComment: EditText = findViewById(R.id.editTextComment)

        val saveButton: Button = findViewById(R.id.buttonSaveNote)
        saveButton.setOnClickListener {
            val comment = editTextComment.text.toString()
            val selectedTopic = spinnerTopics.selectedItem.toString()

            val currentTimestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            val isAdded = databaseHelper.addNote(selectedTopic, comment, currentTimestamp)

            if (isAdded) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.note_error), Toast.LENGTH_SHORT).show()
            }
        }

        window.decorView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    true
                }
                MotionEvent.ACTION_UP -> {
                    endX = event.x
                    if (endX > startX) {
                        swipeRight()
                    } else if (startX > endX) {
                        swipeLeft()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun swipeLeft() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.swipe_left)
        window.decorView.startAnimation(animation)

        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(mainIntent)
        finish()
    }

    private fun swipeRight() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.swipe_right)
        window.decorView.startAnimation(animation)

        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(mainIntent)
        finish()
    }
}