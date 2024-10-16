package com.mindhealth.EmoMind

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MoodAnalysisActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var reportTextView: TextView
    private lateinit var periodSpinner: Spinner
    private lateinit var dataTypeSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_analysis)

        dbHelper = DatabaseHelper(this)
        reportTextView = findViewById(R.id.reportTextView)
        periodSpinner = findViewById(R.id.periodSpinner)
        dataTypeSpinner = findViewById(R.id.dataTypeSpinner)

        // Action for data type selection
        dataTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                fetchDataForSelectedTypeAndPeriod(dataTypeSpinner.selectedItem.toString(), periodSpinner.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Existing listeners
        periodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                fetchDataForSelectedTypeAndPeriod(dataTypeSpinner.selectedItem.toString(), periodSpinner.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

    }

    private fun fetchDataForSelectedTypeAndPeriod(dataType: String, period: String) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val endDate = dateFormat.format(calendar.time)

        val startDate = when (period) {
            "День" -> dateFormat.format(calendar.time)
            "Неделя" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                dateFormat.format(calendar.time)
            }
            "Месяц" -> {
                calendar.add(Calendar.MONTH, -1)
                dateFormat.format(calendar.time)
            }
            else -> dateFormat.format(calendar.time)
        }

        when (dataType) {
            "Настроения" -> {
                val moods = dbHelper.getMoodsForPeriod(startDate, endDate)
                displayMoods(moods, period)
            }
            "Заметки" -> {
                val notes = dbHelper.getNotesForPeriod(startDate, endDate) // Create this function in DatabaseHelper
                displayNotes(notes)
            }
            "Занятия" -> {
                val activities = dbHelper.getActivitiesForPeriod(startDate, endDate) // Create this function in DatabaseHelper
                displayActivities(activities)
            }
            "Цели" -> {
                val goals = dbHelper.getGoalsForPeriod(startDate, endDate) // Create this function in DatabaseHelper
                displayGoals(goals)
            }
        }
    }

    private fun displayMoods(moods: List<Mood>, period: String) {
        val report = StringBuilder()
        report.append("Отчет по настроению за период: $period\n\n")
        if (moods.isEmpty()) {
            report.append("Нет данных за выбранный период.\n")
        } else {
            for (mood in moods) {
                report.append("- Настроение: ${mood.mood}, Эмоции: ${mood.emotions}\n")
            }
        }
        reportTextView.text = report.toString()
    }

    private fun displayNotes(notes: List<Note>) {
        val report = StringBuilder()
        report.append("Отчет по заметкам:\n\n")
        if (notes.isEmpty()) {
            report.append("Нет заметок за выбранный период.\n")
        } else {
            for (note in notes) {
                report.append("- Тема: ${note.topic}, Комментарий: ${note.comment}\n")
            }
        }
        reportTextView.text = report.toString()
    }

    private fun displayActivities(activities: List<Activity>) {
        val report = StringBuilder()
        report.append("Отчет по занятиям:\n\n")
        if (activities.isEmpty()) {
            report.append("Нет занятий за выбранный период.\n")
        } else {
            for (activity in activities) {
                report.append("- Занятие: ${activity.activityName}, Категория: ${activity.category}\n")
            }
        }
        reportTextView.text = report.toString()
    }

    private fun displayGoals(goals: List<Goal>) {
        val report = StringBuilder()
        report.append("Отчет по целям:\n\n")
        if (goals.isEmpty()) {
            report.append("Нет целей за выбранный период.\n")
        } else {
            for (goal in goals) {
                report.append("- Цель: ${goal.goal}, Срок: ${goal.deadline}\n")
            }
        }
        reportTextView.text = report.toString()
    }
}
