package com.mindhealth.EmoMind

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        name = intent.getStringExtra("NAME")

        val users = dbHelper.getAllUsers()
        for ((login, name) in users) {
            Log.d("User Info", "Username: $login, Name: $name")
        }

        findViewById<Button>(R.id.buttonAddMood).setOnClickListener {
            startActivity(Intent(this, AddingMoodActivity::class.java))
        }

        findViewById<Button>(R.id.buttonAddActivity).setOnClickListener {
            startActivity(Intent(this, AddingEvent::class.java))
        }

        findViewById<Button>(R.id.buttonAddNote).setOnClickListener {
            startActivity(Intent(this, AddingNoteActivity::class.java))
        }

        findViewById<Button>(R.id.buttonAddPhoto).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            Log.d("MainActivity", "Sending name to ProfileActivity: $name")
            intent.putExtra("NAME", name)
            startActivity(intent)
        }

        findViewById<Button>(R.id.buttonAddPhoto3).setOnClickListener {
            startActivity(Intent(this, SettingGoalsActivity::class.java))
        }

        findViewById<Button>(R.id.buttonAddPhoto2).setOnClickListener {
            startActivity(Intent(this, MoodAnalysisActivity::class.java))
        }
    }
}