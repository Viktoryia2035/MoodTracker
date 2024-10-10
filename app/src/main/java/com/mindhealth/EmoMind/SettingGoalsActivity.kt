package com.mindhealth.EmoMind

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class SettingGoalsActivity : AppCompatActivity() {
    private var startX: Float = 0f
    private var endX: Float = 0f

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_goals)

        val reminderEditText = findViewById<EditText>(R.id.editTextSleepReminder)
        val goalEditText = findViewById<EditText>(R.id.editTextSleepGoal)
        val deadlineEditText = findViewById<EditText>(R.id.editTextSleepDeadline)
        val notesEditText = findViewById<EditText>(R.id.editTextSleepNotes)

        val saveGoalsButton = findViewById<Button>(R.id.buttonSaveGoals)
        saveGoalsButton.setOnClickListener {
            val reminderInput = reminderEditText.text.toString()
            val goalInput = goalEditText.text.toString()
            val deadlineInput = deadlineEditText.text.toString()
            val notesInput = notesEditText.text.toString()

            if (!isValidTimeFormat(reminderInput)) {
                Toast.makeText(this, "Введите время напоминания в корректном формате чч:мм", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidDateFormat(deadlineInput)) {
                Toast.makeText(this, "Введите дату в корректном формате дд.мм.гг и не ранее следующего дня", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
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

    private fun swipeRight() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.swipe_right)
        window.decorView.startAnimation(animation)

        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(mainIntent)
        finish()
    }

    private fun swipeLeft() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.swipe_left)
        window.decorView.startAnimation(animation)

        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(mainIntent)
        finish()
    }

    private fun isValidTimeFormat(time: String): Boolean {
        val regex = Regex("^(\\d{1,2}):(\\d{2})$")
        if (!regex.matches(time)) return false

        val (hours, minutes) = time.split(":").map { it.toInt() }
        return hours in 0..23 && minutes in 0..59
    }

    private fun isValidDateFormat(date: String): Boolean {
        val regex = Regex("^(\\d{2})\\.(\\d{2})\\.(\\d{2})$")
        if (!regex.matches(date)) return false

        val (day, month, year) = date.split(".").map { it.toInt() }
        val currentDate = Calendar.getInstance()
        val inputDate = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.MONTH, month - 1)
            set(Calendar.YEAR, 2000 + year)
        }

        inputDate.add(Calendar.DAY_OF_MONTH, 1)
        return inputDate.after(currentDate)
    }
}
