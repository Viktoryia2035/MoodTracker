package com.mindhealth.EmoMind

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddingEvent : AppCompatActivity() {
    private var startX: Float = 0f
    private var endX: Float = 0f
    private lateinit var databaseHelper: DatabaseHelper

    @SuppressLint("ClickableViewAccessibility", "StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_event)

        databaseHelper = DatabaseHelper(this)

        val activityNameEditText = findViewById<EditText>(R.id.editTextActivityName)
        val durationEditText = findViewById<EditText>(R.id.editTextDuration)
        val commentsEditText = findViewById<EditText>(R.id.editTextComments)
        val intensitySpinner = findViewById<Spinner>(R.id.spinnerIntensity)
        val categorySpinner = findViewById<Spinner>(R.id.spinnerTags)

        val intensityLevels = resources.getStringArray(R.array.intensity_levels_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, intensityLevels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        intensitySpinner.adapter = adapter

        val categories = resources.getStringArray(R.array.tags_array)
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        val saveAddButton = findViewById<Button>(R.id.buttonAddActivity)
        saveAddButton.setOnClickListener {
            val activityName = activityNameEditText.text.toString()
            val duration = durationEditText.text.toString()
            val comments = commentsEditText.text.toString()
            val selectedIntensity = intensitySpinner.selectedItem.toString()
            val selectedCategory = categorySpinner.selectedItem.toString()

            if (activityName.isEmpty() || duration.isEmpty() || comments.isEmpty()) {
                Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            } else if (!isValidTimeFormat(duration)) {
                Toast.makeText(this, getString(R.string.incorrect_time_format), Toast.LENGTH_SHORT).show()
            } else {
                val currentTimestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

                if (databaseHelper.addActivity(activityName, selectedCategory, selectedIntensity, duration, comments, currentTimestamp)) {
                    Toast.makeText(this, getString(R.string.activity_added, activityName, selectedIntensity), Toast.LENGTH_SHORT).show()

                    val mainIntent = Intent(this, MainActivity::class.java)
                    mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(mainIntent)
                    finish()
                } else {
                    Toast.makeText(this, getString(R.string.error_adding_activity), Toast.LENGTH_SHORT).show()
                }
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

    private fun isValidTimeFormat(time: String): Boolean {
        val regex = Regex("^(\\d{1,2}):(\\d{2})$")
        if (!regex.matches(time)) return false

        val (hours, minutes) = time.split(":").map { it.toInt() }
        return hours in 0..23 && minutes in 0..59
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