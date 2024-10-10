package com.mindhealth.EmoMind

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class AddingMoodActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private var selectedEmotions: MutableList<String> = mutableListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_mood)

        databaseHelper = DatabaseHelper(this)

        val saveMoodButton = findViewById<Button>(R.id.buttonSaveMood)
        val commentEditText = findViewById<EditText>(R.id.editTextComment)

        saveMoodButton.setOnClickListener {
            val login = "User Login" // заменить на фактический логин пользователя
            val userId = databaseHelper.getUserId(login)

            if (userId != null) {
                val mood = "Супер" // Получите значение настроения в зависимости от нажатия
                val comment = commentEditText.text.toString()
                val emotionsString = selectedEmotions.joinToString(", ")

                // Текущая дата
                val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

                // Сохранение настроения с датой, комментариями и эмоциями
                val isSuccess = databaseHelper.addMood(userId, mood, currentDate, comment, emotionsString)

                if (isSuccess) {
                    // Успешно сохранено, переход на главную
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Ошибка при сохранении
                    Toast.makeText(this, "Ошибка сохранения настроения", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show()
            }
        }

        val emotionsButton = findViewById<Button>(R.id.buttonSelectEmotions)
        emotionsButton.setOnClickListener {
            showMultiSelectDialog()
        }
    }

    private fun showMultiSelectDialog() {
        val emotions = resources.getStringArray(R.array.emotion_array)
        val checkedItems = BooleanArray(emotions.size) { false }

        AlertDialog.Builder(this)
            .setTitle("Выберите эмоции")
            .setMultiChoiceItems(emotions, checkedItems) { _, which, isChecked ->
                if (isChecked) {
                    selectedEmotions.add(emotions[which])
                } else {
                    selectedEmotions.remove(emotions[which])
                }
            }
            .setPositiveButton("Готово") { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}
