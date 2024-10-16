package com.mindhealth.EmoMind

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_goals)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
                Toast.makeText(this, getString(R.string.notification_permission_request), Toast.LENGTH_SHORT).show()
            }
        }

        val goalEditText = findViewById<EditText>(R.id.editTextSleepGoal)
        val deadlineEditText = findViewById<EditText>(R.id.editTextSleepDeadline)
        val notesEditText = findViewById<EditText>(R.id.editTextSleepNotes)

        val saveGoalsButton = findViewById<Button>(R.id.buttonSaveGoals)
        val dbHelper = DatabaseHelper(this)

        saveGoalsButton.setOnClickListener {
            val goalInput = goalEditText.text.toString()
            val deadlineInput = deadlineEditText.text.toString()
            val notesInput = notesEditText.text.toString()

            if (!isValidDateFormat(deadlineInput)) {
                Toast.makeText(this, getString(R.string.enter_valid_date), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = dbHelper.addGoal(goalInput, deadlineInput, notesInput)
            if (success) {
                Toast.makeText(this, getString(R.string.goal_saved), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.goal_save_error), Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        window.decorView.setOnTouchListener { _, event ->
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
}
