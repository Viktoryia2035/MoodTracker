package com.mindhealth.EmoMind

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val login = intent.getStringExtra("LOGIN") // Получаем логин

        findViewById<Button>(R.id.buttonEditProfile).setOnClickListener {
            val intent = Intent(this, RedactorProfileActivity::class.java)
            intent.putExtra("LOGIN", login) // передаем логин для редактирования
            startActivity(intent)
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
}