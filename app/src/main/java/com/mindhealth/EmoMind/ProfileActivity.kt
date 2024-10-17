package com.mindhealth.EmoMind

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var login: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        dbHelper = DatabaseHelper(this)

        login = intent.getStringExtra("LOGIN") ?: ""

        findViewById<Button>(R.id.buttonEditProfile).setOnClickListener {
            val intent = Intent(this, RedactorProfileActivity::class.java)
            intent.putExtra("LOGIN", login)
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

        findViewById<Button>(R.id.buttonDeleteAccount).setOnClickListener {
            deleteUserAccount()
        }
    }

    private fun deleteUserAccount() {
        val success = dbHelper.deleteUser(login)

        if (success) {
            Toast.makeText(this, getString(R.string.account_deleted_successfully), Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, getString(R.string.failed_to_delete_account), Toast.LENGTH_SHORT).show()
        }
    }
}
