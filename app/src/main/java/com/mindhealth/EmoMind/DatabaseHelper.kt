package com.mindhealth.EmoMind

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "users.db"
        private const val DATABASE_VERSION = 17

        const val TABLE_USERS = "users"
        const val TABLE_MOODS = "moods"
        const val TABLE_ACTIVITIES = "activities"
        const val TABLE_NOTES = "notes"
        const val TABLE_GOALS = "goals"

        const val COLUMN_ID = "_id"
        const val COLUMN_LOGIN = "login"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_NAME = "name"

        const val COLUMN_MOOD = "mood"
        const val COLUMN_MOOD_DATE = "date"
        const val COLUMN_MOOD_COMMENT = "comment"
        const val COLUMN_MOOD_EMOTIONS = "emotions"

        const val COLUMN_ACTIVITY_NAME = "activity_name"
        const val COLUMN_ACTIVITY_CATEGORY = "category"
        const val COLUMN_ACTIVITY_INTENSITY = "intensity"
        const val COLUMN_ACTIVITY_DURATION = "duration"
        const val COLUMN_ACTIVITY_COMMENTS = "comments"
        const val COLUMN_ACTIVITY_TIMESTAMP = "timestamp"

        const val COLUMN_GOAL = "goal"
        const val COLUMN_GOAL_DEADLINE = "deadline"
        const val COLUMN_GOAL_NOTES = "notes"
        const val COLUMN_GOAL_TIMESTAMP = "timestamp" // Use this instead of saved_time

        const val COLUMN_NOTE_TOPIC = "topic"
        const val COLUMN_NOTE_COMMENT = "comment"
        const val COLUMN_NOTE_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = "CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_LOGIN TEXT UNIQUE, " +
                "$COLUMN_PASSWORD TEXT, " +
                "$COLUMN_NAME TEXT)"

        val createMoodTable = "CREATE TABLE $TABLE_MOODS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_MOOD TEXT, " +
                "$COLUMN_MOOD_DATE TEXT, " +
                "$COLUMN_MOOD_COMMENT TEXT, " +
                "$COLUMN_MOOD_EMOTIONS TEXT)"

        val createActivityTable = "CREATE TABLE $TABLE_ACTIVITIES (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_ACTIVITY_NAME TEXT, " +
                "$COLUMN_ACTIVITY_CATEGORY TEXT, " +
                "$COLUMN_ACTIVITY_INTENSITY TEXT, " +
                "$COLUMN_ACTIVITY_DURATION TEXT, " +
                "$COLUMN_ACTIVITY_COMMENTS TEXT, " +
                "$COLUMN_ACTIVITY_TIMESTAMP TEXT)"

        val createGoalsTable = "CREATE TABLE $TABLE_GOALS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_GOAL TEXT, " +
                "$COLUMN_GOAL_DEADLINE TEXT, " +
                "$COLUMN_GOAL_NOTES TEXT," +
                "$COLUMN_GOAL_TIMESTAMP TEXT)" // Add the timestamp column here

        val createNotesTable = "CREATE TABLE $TABLE_NOTES (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NOTE_TOPIC TEXT, " +
                "$COLUMN_NOTE_COMMENT TEXT, " +
                "$COLUMN_NOTE_TIMESTAMP TEXT)"

        db.execSQL(createUserTable)
        db.execSQL(createMoodTable)
        db.execSQL(createActivityTable)
        db.execSQL(createGoalsTable)
        db.execSQL(createNotesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACTIVITIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOODS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GOALS")
        onCreate(db)
    }

    fun addNote(topic: String, comment: String, timestamp: String): Boolean {
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_NOTE_TOPIC, topic)
            put(COLUMN_NOTE_COMMENT, comment)
            put(COLUMN_NOTE_TIMESTAMP, timestamp)
        }
        val result = db.insert(TABLE_NOTES, null, values)
        return result != -1L
    }

    fun deleteUser(login: String): Boolean {
        val db = this.writableDatabase
        return db.delete(TABLE_USERS, "$COLUMN_LOGIN = ?", arrayOf(login)) > 0
    }

    fun addUser(login: String, password: String, name: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LOGIN, login)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_NAME, name)
        }
        val result = db.insert(TABLE_USERS, null, values)
        return result != -1L
    }

    fun addGoal(goal: String, deadline: String, notes: String, timestamp: String): Boolean {
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_GOAL, goal)
            put(COLUMN_GOAL_DEADLINE, deadline)
            put(COLUMN_GOAL_NOTES, notes)
            put(COLUMN_GOAL_TIMESTAMP, timestamp) // Use the goal timestamp
        }
        val result = db.insert(TABLE_GOALS, null, values)
        return result != -1L
    }

    fun addMood(mood: String, date: String, comment: String, emotions: String): Boolean {
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_MOOD, mood)
            put(COLUMN_MOOD_DATE, date)
            put(COLUMN_MOOD_COMMENT, comment)
            put(COLUMN_MOOD_EMOTIONS, emotions)
        }
        val result = db.insert(TABLE_MOODS, null, values)
        return result != -1L
    }

    fun addActivity(
        activityName: String,
        category: String,
        intensity: String,
        duration: String,
        comments: String,
        timestamp: String
    ): Boolean {
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_ACTIVITY_NAME, activityName)
            put(COLUMN_ACTIVITY_CATEGORY, category)
            put(COLUMN_ACTIVITY_INTENSITY, intensity)
            put(COLUMN_ACTIVITY_DURATION, duration)
            put(COLUMN_ACTIVITY_COMMENTS, comments)
            put(COLUMN_ACTIVITY_TIMESTAMP, timestamp) // Use the activity timestamp
        }
        val result = db.insert(TABLE_ACTIVITIES, null, values)
        return result != -1L
    }

    fun updateUser(oldLogin: String, newLogin: String, newName: String, newPassword: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_LOGIN, newLogin) // Update login
            put(COLUMN_NAME, newName)
            put(COLUMN_PASSWORD, newPassword)
        }

        // Update based on old login
        val rowsUpdated = db.update(TABLE_USERS, values, "$COLUMN_LOGIN = ?", arrayOf(oldLogin))
        return rowsUpdated > 0
    }

    fun checkIfLoginExists(login: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_LOGIN = ?", arrayOf(login))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    @SuppressLint("Range")
    fun getUsername(login: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_NAME FROM $TABLE_USERS WHERE $COLUMN_LOGIN = ?", arrayOf(login))
        return if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
        } else {
            null
        }.also { cursor.close() }
    }

    @SuppressLint("Range")
    fun getAllUsers(): List<Pair<String, String>> {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT $COLUMN_LOGIN, $COLUMN_NAME FROM $TABLE_USERS", null)
        val userList = mutableListOf<Pair<String, String>>()

        if (cursor.moveToFirst()) {
            do {
                val login = cursor.getString(cursor.getColumnIndex(COLUMN_LOGIN))
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                userList.add(Pair(login, name))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return userList
    }

    fun checkUser(login: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_LOGIN = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(login, password))

        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    @SuppressLint("Range")
    fun getPassword(login: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_PASSWORD FROM $TABLE_USERS WHERE $COLUMN_LOGIN = ?", arrayOf(login))
        return if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD))
        } else {
            null
        }.also { cursor.close() }
    }

    @SuppressLint("Range")
    fun getMoodsForPeriod(startDate: String, endDate: String): List<Mood> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_MOODS WHERE $COLUMN_MOOD_DATE BETWEEN ? AND ?", arrayOf(startDate, endDate))
        val moods = mutableListOf<Mood>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                val mood = cursor.getString(cursor.getColumnIndex(COLUMN_MOOD))
                val date = cursor.getString(cursor.getColumnIndex(COLUMN_MOOD_DATE))
                val comment = cursor.getString(cursor.getColumnIndex(COLUMN_MOOD_COMMENT))
                val emotions = cursor.getString(cursor.getColumnIndex(COLUMN_MOOD_EMOTIONS))
                moods.add(Mood(id, mood, date, comment, emotions))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return moods
    }

    @SuppressLint("Range")
    fun getActivitiesForPeriod(startDate: String, endDate: String): List<Activity> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ACTIVITIES WHERE $COLUMN_ACTIVITY_TIMESTAMP BETWEEN ? AND ?", arrayOf(startDate, endDate))
        val activities = mutableListOf<Activity>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_NAME))
                val category = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_CATEGORY))
                val intensity = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_INTENSITY))
                val duration = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_DURATION))
                val comments = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_COMMENTS))
                val timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_TIMESTAMP))
                activities.add(Activity(id, name, category, intensity, duration, comments, timestamp))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return activities
    }

    @SuppressLint("Range")
    fun getGoalsForPeriod(startDate: String, endDate: String): List<Goal> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_GOALS WHERE $COLUMN_GOAL_TIMESTAMP BETWEEN ? AND ?", arrayOf(startDate, endDate))
        val goals = mutableListOf<Goal>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                val goal = cursor.getString(cursor.getColumnIndex(COLUMN_GOAL))
                val deadline = cursor.getString(cursor.getColumnIndex(COLUMN_GOAL_DEADLINE))
                val notes = cursor.getString(cursor.getColumnIndex(COLUMN_GOAL_NOTES))
                val timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_GOAL_TIMESTAMP))
                goals.add(Goal(id, goal, deadline, notes, timestamp))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return goals
    }

    @SuppressLint("Range")
    fun getNotesForPeriod(startDate: String, endDate: String): List<Note> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NOTES WHERE $COLUMN_NOTE_TIMESTAMP BETWEEN ? AND ?", arrayOf(startDate, endDate))
        val notes = mutableListOf<Note>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                val topic = cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TOPIC))
                val comment = cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_COMMENT))
                val timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TIMESTAMP))
                notes.add(Note(id, topic, comment, timestamp))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return notes
    }

    fun deleteMood(id: Int): Boolean {
        val db = this.writableDatabase
        return db.delete(TABLE_MOODS, "$COLUMN_ID = ?", arrayOf(id.toString())) > 0
    }

    // Для заметок
    fun deleteNote(id: Int): Boolean {
        val db = this.writableDatabase
        return db.delete(TABLE_NOTES, "$COLUMN_ID = ?", arrayOf(id.toString())) > 0
    }

    // Для активностей
    fun deleteActivity(id: Int): Boolean {
        val db = this.writableDatabase
        return db.delete(TABLE_ACTIVITIES, "$COLUMN_ID = ?", arrayOf(id.toString())) > 0
    }

    // Для целей
    fun deleteGoal(id: Int): Boolean {
        val db = this.writableDatabase
        return db.delete(TABLE_GOALS, "$COLUMN_ID = ?", arrayOf(id.toString())) > 0
    }
}

data class Mood(
    val id: Int,
    val mood: String,
    val date: String,
    val comment: String,
    val emotions: String
)

data class Activity(
    val id: Int,
    val name: String,
    val category: String,
    val intensity: String,
    val duration: String,
    val comments: String,
    val timestamp: String
)

data class Goal(
    val id: Int,
    val goal: String,
    val deadline: String,
    val notes: String,
    val timestamp: String
)

data class Note(
    val id: Int,
    val topic: String,
    val comment: String,
    val timestamp: String
)
