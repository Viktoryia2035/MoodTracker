package com.mindhealth.EmoMind

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private var name: String? = null
    private var login: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        login = intent.getStringExtra("LOGIN")

        if (login == null) {
            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            login = sharedPreferences.getString("LOGIN", null)
        }

        name = intent.getStringExtra("NAME")

        val users = dbHelper.getAllUsers()
        for (user in users) {
            Log.d("Users", "Login: ${user.first}, Name: ${user.second}")
        }

        val addMoodButton = findViewById<Button>(R.id.buttonAddMood)
        addMoodButton.setOnClickListener {
            val intent = Intent(this, AddingMoodActivity::class.java)
            intent.putExtra("LOGIN", login)
            startActivity(intent)
        }

        val addEventButton = findViewById<Button>(R.id.buttonAddActivity)
        addEventButton.setOnClickListener {
            val intent = Intent(this, AddingEvent::class.java)
            intent.putExtra("LOGIN", login)
            startActivity(intent)
        }

        val addNoteButton = findViewById<Button>(R.id.buttonAddNote)
        addNoteButton.setOnClickListener {
            val intent = Intent(this, AddingNoteActivity::class.java)
            intent.putExtra("LOGIN", login)
            startActivity(intent)
        }

        val addGoalButton = findViewById<Button>(R.id.buttonAddPhoto3)
        addGoalButton.setOnClickListener {
            val intent = Intent(this, SettingGoalsActivity::class.java)
            intent.putExtra("LOGIN", login)
            startActivity(intent)
        }

        val moodAnalysisButton = findViewById<Button>(R.id.buttonAddPhoto2)
        moodAnalysisButton.setOnClickListener {
            val intent = Intent(this, MoodAnalysisActivity::class.java)
            intent.putExtra("LOGIN", login)
            startActivity(intent)
        }

        val profileButton = findViewById<Button>(R.id.buttonAddPhoto)
        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("NAME", name)
            startActivity(intent)
        }
    }
}