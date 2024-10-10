package com.mindhealth.EmoMind

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class ProfileActivity : AppCompatActivity() {
    private lateinit var imageViewProfile: ImageView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var switchMoodReminder: Switch
    private lateinit var switchGoalReminder: Switch
    private var savedUsername: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        dbHelper = DatabaseHelper(this)
        imageViewProfile = findViewById(R.id.imageViewProfile)
        switchMoodReminder = findViewById(R.id.switchMoodReminder)
        switchGoalReminder = findViewById(R.id.switchGoalReminder)

        loadReminderState()
        loadProfileData()

        // Получение сохраненного имени
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        savedUsername = sharedPreferences.getString("username", null)

        findViewById<Button>(R.id.buttonEditProfile).setOnClickListener {
            val intent = Intent(this, RedactorProfileActivity::class.java)
            intent.putExtra("NAME", savedUsername)
            startActivity(intent)
        }

        switchMoodReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showTimePicker { hour, minute -> setMoodReminder(hour, minute) }
            } else {
                cancelAlarm(MoodReminderReceiver::class.java)
            }
            saveReminderState()
        }

        switchGoalReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showTimePicker { hour, minute -> setGoalReminder(hour, minute) }
            } else {
                cancelAlarm(GoalReminderReceiver::class.java)
            }
            saveReminderState()
        }

        findViewById<Button>(R.id.buttonGoToMain).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.buttonSingOut).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfileData()
    }

    private fun loadProfileData() {
        val username = intent.getStringExtra("NAME")
        if (username != null) {
            savedUsername = username
            // Сохранение имени
            val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putString("username", username)
                apply()
            }

            val textViewName = findViewById<TextView>(R.id.textViewName)
            textViewName.text = username
            loadProfileImage(username)
        } else {
            Log.e("ProfileActivity", "Username is null")
            Toast.makeText(this, "Username not provided", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProfileImage(username: String) {
        val cursor = dbHelper.readableDatabase.rawQuery(
            "SELECT profile_image FROM ${DatabaseHelper.TABLE_USERS} WHERE ${DatabaseHelper.COLUMN_LOGIN} = ?",
            arrayOf(username)
        )

        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex("profile_image")
            val imageBytes = cursor.getBlob(columnIndex)

            if (imageBytes != null) {
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                imageViewProfile.setImageBitmap(bitmap)
            } else {
                Log.e("ProfileActivity", "Image bytes are null")
            }
        } else {
            Log.e("ProfileActivity", "Cursor did not return any results")
        }
        cursor.close()
    }

    private fun showTimePicker(onTimeSet: (Int, Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            onTimeSet(selectedHour, selectedMinute)
            Toast.makeText(this, "Уведомление установлено на: ${String.format("%02d:%02d", selectedHour, selectedMinute)}:00", Toast.LENGTH_SHORT).show()
        }, hour, minute, true).show()
    }

    private fun setMoodReminder(hour: Int, minute: Int) {
        setAlarm(MoodReminderReceiver::class.java, hour, minute)
    }

    private fun setGoalReminder(hour: Int, minute: Int) {
        setAlarm(GoalReminderReceiver::class.java, hour, minute)
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(receiverClass: Class<*>, hour: Int, minute: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, receiverClass)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DATE, 1)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis + AlarmManager.INTERVAL_DAY,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun cancelAlarm(receiverClass: Class<*>) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, receiverClass)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.cancel(pendingIntent)
    }

    private fun saveReminderState() {
        val sharedPreferences = getSharedPreferences("MoodPreferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("moodReminder", switchMoodReminder.isChecked)
            putBoolean("goalReminder", switchGoalReminder.isChecked)
            apply()
        }
    }

    private fun loadReminderState() {
        val sharedPreferences = getSharedPreferences("MoodPreferences", Context.MODE_PRIVATE)
        val moodReminder = sharedPreferences.getBoolean("moodReminder", false)
        val goalReminder = sharedPreferences.getBoolean("goalReminder", false)

        switchMoodReminder.isChecked = moodReminder
        switchGoalReminder.isChecked = goalReminder
    }
}
