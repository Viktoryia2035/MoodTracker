package com.mindhealth.EmoMind

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat

import java.util.*

class MoodAnalysisActivity : AppCompatActivity() {

    private lateinit var reportLinearLayout: LinearLayout
    private lateinit var periodSpinner: Spinner
    private lateinit var dataTypeSpinner: Spinner
    private lateinit var databaseHelper: DatabaseHelper
    private var startX: Float = 0f
    private var endX: Float = 0f

    private var selectedPeriod: String? = null
    private var selectedDataType: String? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_analysis)

        reportLinearLayout = findViewById(R.id.reportLinearLayout)
        periodSpinner = findViewById(R.id.periodSpinner)
        dataTypeSpinner = findViewById(R.id.dataTypeSpinner)
        databaseHelper = DatabaseHelper(this)

        setupSpinners()

        window.decorView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    true
                }
                MotionEvent.ACTION_UP -> {
                    endX = event.x
                    when {
                        endX > startX -> swipeRight()
                        startX > endX -> swipeLeft()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun setupSpinners() {
        val dataTypeArray = resources.getStringArray(R.array.data_type_array)
        val dataTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dataTypeArray)
        dataTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dataTypeSpinner.adapter = dataTypeAdapter
        dataTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedDataType = dataTypeArray[position]
                filterAndDisplayData()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedDataType = null
            }
        }

        val periodArray = resources.getStringArray(R.array.period_array)
        val periodAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, periodArray)
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        periodSpinner.adapter = periodAdapter

        periodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedPeriod = periodArray[position]
                filterAndDisplayData()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedPeriod = null
            }
        }
    }

    private fun filterAndDisplayData() {
        if (selectedDataType == null || selectedPeriod == null) {
            reportLinearLayout.removeAllViews()

            val textView = TextView(this)
            textView.text = getString(R.string.please_select_filters)
            reportLinearLayout.addView(textView)
            return
        }

        val dateRange = getDateRange(selectedPeriod!!)
        if (dateRange == null) {
            reportLinearLayout.removeAllViews()
            val textView = TextView(this)
            textView.text = getString(R.string.error_getting_date_range)
            reportLinearLayout.addView(textView)
            return
        }

        val (startDate, endDate) = dateRange

        when (selectedDataType) {
            "Настроения" -> {
                val moods = databaseHelper.getMoodsForPeriod(startDate, endDate)
                displayMoodData(moods)
            }
            "Заметки" -> {
                val notes = databaseHelper.getNotesForPeriod(startDate, endDate)
                displayNoteData(notes)
            }
            "Занятия" -> {
                val activities = databaseHelper.getActivitiesForPeriod(startDate, endDate)
                displayActivityData(activities)
            }
            "Цели" -> {
                val goals = databaseHelper.getGoalsForPeriod(startDate, endDate)
                displayGoalData(goals)
            }
        }
    }

    private fun getDateRange(period: String): Pair<String, String>? {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val calendar = Calendar.getInstance()

        return when (period) {
            "Сегодня" -> {
                val startDate = sdf.format(calendar.apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }.time)
                val endDate = sdf.format(calendar.apply { set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59) }.time)
                Pair(startDate, endDate)
            }
            "Неделя" -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                val startDate = sdf.format(calendar.time)
                calendar.add(Calendar.DAY_OF_WEEK, 6)
                val endDate = sdf.format(calendar.time)
                Pair(startDate, endDate)
            }
            "Месяц" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val startDate = sdf.format(calendar.time)
                calendar.add(Calendar.MONTH, 1)
                calendar.set(Calendar.DAY_OF_MONTH, 0) // Последний день месяца
                val endDate = sdf.format(calendar.time)
                Pair(startDate, endDate)
            }
            else -> null
        }
    }

    private fun displayMoodData(moods: List<Mood>) {
        reportLinearLayout.removeAllViews()

        if (moods.isEmpty()) {
            val noDataTextView = TextView(this)
            noDataTextView.text = getString(R.string.no_mood_data)
            reportLinearLayout.addView(noDataTextView)
        } else {
            for (mood in moods) {
                val moodTextView = TextView(this)
                moodTextView.text = getString(R.string.mood_label, mood.mood, mood.date, mood.comment, mood.emotions)
                moodTextView.isClickable = true
                moodTextView.setOnClickListener {
                    showDeleteConfirmationDialog(mood.id, "mood")
                }
                reportLinearLayout.addView(moodTextView)
            }
        }
    }

    private fun displayNoteData(notes: List<Note>) {
        reportLinearLayout.removeAllViews()

        if (notes.isEmpty()) {
            val noDataTextView = TextView(this)
            noDataTextView.text = getString(R.string.no_notes)
            reportLinearLayout.addView(noDataTextView)
        } else {
            for (note in notes) {
                val noteTextView = TextView(this)
                noteTextView.text = getString(R.string.note_label, note.topic, note.comment, note.timestamp)
                noteTextView.isClickable = true
                noteTextView.setOnClickListener {
                    showDeleteConfirmationDialog(note.id, "note")
                }
                reportLinearLayout.addView(noteTextView)
            }
        }
    }

    private fun displayActivityData(activities: List<Activity>) {
        reportLinearLayout.removeAllViews()

        if (activities.isEmpty()) {
            val noDataTextView = TextView(this)
            noDataTextView.text = getString(R.string.no_activities)
            reportLinearLayout.addView(noDataTextView)
        } else {
            for (activity in activities) {
                val activityTextView = TextView(this)
                activityTextView.text = getString(R.string.activity_label, activity.name, activity.category, activity.intensity, activity.duration, activity.comments)
                activityTextView.isClickable = true
                activityTextView.setOnClickListener {
                    showDeleteConfirmationDialog(activity.id, "activity")
                }
                reportLinearLayout.addView(activityTextView)
            }
        }
    }

    private fun displayGoalData(goals: List<Goal>) {
        reportLinearLayout.removeAllViews()

        if (goals.isEmpty()) {
            val noDataTextView = TextView(this)
            noDataTextView.text = getString(R.string.no_goals)
            reportLinearLayout.addView(noDataTextView)
        } else {
            for (goal in goals) {
                val goalTextView = TextView(this)
                goalTextView.text = getString(R.string.goal_label, goal.goal, goal.deadline, goal.notes)
                goalTextView.isClickable = true
                goalTextView.setOnClickListener {
                    showDeleteConfirmationDialog(goal.id, "goal")
                }
                reportLinearLayout.addView(goalTextView)
            }
        }
    }

    private fun showDeleteConfirmationDialog(id: Int, type: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.delete_confirmation_title))
        builder.setMessage(getString(R.string.delete_confirmation_message))

        builder.setPositiveButton(getString(R.string.delete)) { _, _ ->
            when (type) {
                "mood" -> databaseHelper.deleteMood(id)
                "note" -> databaseHelper.deleteNote(id)
                "activity" -> databaseHelper.deleteActivity(id)
                "goal" -> databaseHelper.deleteGoal(id)
            }
            filterAndDisplayData()
        }

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun swipeRight() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.swipe_right)
        window.decorView.startAnimation(animation)

        val registerIntent = Intent(this, MainActivity::class.java)
        startActivity(registerIntent)
        finish()
    }

    private fun swipeLeft() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.swipe_left)
        window.decorView.startAnimation(animation)

        val loginIntent = Intent(this, MainActivity::class.java)
        startActivity(loginIntent)
        finish()
    }
}
