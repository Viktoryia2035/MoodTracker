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
        private const val DATABASE_VERSION = 15

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
        const val COLUMN_GOAL_SAVED_TIME = "saved_time"

        const val COLUMN_NOTE_TOPIC = "topic"
        const val COLUMN_NOTE_COMMENT = "comment"
        const val COLUMN_NOTE_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            val createUserTable = ("CREATE TABLE $TABLE_USERS (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_LOGIN TEXT UNIQUE, " +
                    "$COLUMN_PASSWORD TEXT, " +
                    "$COLUMN_NAME TEXT) "
                    )

            val createMoodTable = ("CREATE TABLE $TABLE_MOODS (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_MOOD TEXT, " +
                    "$COLUMN_MOOD_DATE TEXT, " +
                    "$COLUMN_MOOD_COMMENT TEXT, " +
                    "$COLUMN_MOOD_EMOTIONS TEXT)"
                    )

            val createActivityTable = ("CREATE TABLE $TABLE_ACTIVITIES (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_ACTIVITY_NAME TEXT, " +
                    "$COLUMN_ACTIVITY_CATEGORY TEXT, " +
                    "$COLUMN_ACTIVITY_INTENSITY TEXT, " +
                    "$COLUMN_ACTIVITY_DURATION TEXT, " +
                    "$COLUMN_ACTIVITY_COMMENTS TEXT, " +
                    "$COLUMN_ACTIVITY_TIMESTAMP TEXT)"
                    )

            val createGoalsTable = ("CREATE TABLE $TABLE_GOALS (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_GOAL TEXT, " +
                    "$COLUMN_GOAL_DEADLINE TEXT, " +
                    "$COLUMN_GOAL_NOTES TEXT, " +
                    "$COLUMN_GOAL_SAVED_TIME TEXT)"
                    )

            val createNotesTable = ("CREATE TABLE $TABLE_NOTES (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_NOTE_TOPIC TEXT, " +
                    "$COLUMN_NOTE_COMMENT TEXT, " +
                    "$COLUMN_NOTE_TIMESTAMP TEXT)"
                    )

            db.execSQL(createUserTable)
            db.execSQL(createMoodTable)
            db.execSQL(createActivityTable)
            db.execSQL(createGoalsTable)
            db.execSQL(createNotesTable)
        } catch (e: Exception) {
            // Handle exceptions, like logging
        }
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

    fun addGoal(goal: String, deadline: String, notes: String): Boolean {
        val db = this.writableDatabase
        val currentTime = System.currentTimeMillis().toString() // Текущая временная метка в миллисекундах

        val values = ContentValues().apply {
            put(COLUMN_GOAL, goal)
            put(COLUMN_GOAL_DEADLINE, deadline)
            put(COLUMN_GOAL_NOTES, notes)
            put(COLUMN_GOAL_SAVED_TIME, currentTime) // Сохраняем текущее время
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
            put(COLUMN_ACTIVITY_TIMESTAMP, timestamp)
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
        // Исправляем SQL-запрос для фильтрации по login, а не по name
        val cursor = db.rawQuery("SELECT $COLUMN_NAME FROM $TABLE_USERS WHERE $COLUMN_LOGIN = ?", arrayOf(login))
        return if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
        } else {
            null
        }.also { cursor.close() }
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
    fun getMoodsForPeriod(startDate: String, endDate: String): List<Mood> {
        val db = this.readableDatabase
        val moodList = mutableListOf<Mood>()

        // rawQuery для выборки настроений в указанном диапазоне дат
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_MOODS WHERE $COLUMN_MOOD_DATE BETWEEN ? AND ?",
            arrayOf(startDate, endDate)
        )

        // Проверяем, есть ли результаты
        if (cursor.moveToFirst()) {
            do {
                // Создаем объект Mood и заполняем его данными из курсора
                val mood = Mood(
                    mood = cursor.getString(cursor.getColumnIndex(COLUMN_MOOD)),
                    date = cursor.getString(cursor.getColumnIndex(COLUMN_MOOD_DATE)),
                    comment = cursor.getString(cursor.getColumnIndex(COLUMN_MOOD_COMMENT)),
                    emotions = cursor.getString(cursor.getColumnIndex(COLUMN_MOOD_EMOTIONS))
                )
                moodList.add(mood)
            } while (cursor.moveToNext()) // Переходим к следующему элементу
        }

        cursor.close() // Закрываем курсор
        return moodList // Возвращаем список настроений
    }


    @SuppressLint("Range")
    fun getNotesForPeriod(startDate: String, endDate: String): List<Note> {
        val db = this.readableDatabase
        val noteList = mutableListOf<Note>()

        // Запрос для выборки заметок в заданном диапазоне временных меток
        val query = "SELECT * FROM $TABLE_NOTES WHERE $COLUMN_NOTE_TIMESTAMP BETWEEN ? AND ?"

        db.rawQuery(query, arrayOf(startDate, endDate)).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val note = Note(
                        topic = cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TOPIC)),
                        comment = cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_COMMENT)),
                        timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_TIMESTAMP))
                    )
                    noteList.add(note)
                } while (cursor.moveToNext())
            }
        }

        return noteList
    }


    @SuppressLint("Range")
    fun getActivitiesForPeriod(startDate: String, endDate: String): List<Activity> {
        val db = this.readableDatabase
        val activityList = mutableListOf<Activity>()

        // Запрос для выборки активностей в заданном диапазоне временных меток
        val query = "SELECT * FROM $TABLE_ACTIVITIES WHERE $COLUMN_ACTIVITY_TIMESTAMP BETWEEN ? AND ?"

        db.rawQuery(query, arrayOf(startDate, endDate)).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val activity = Activity(
                        activityName = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_NAME)),
                        category = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_CATEGORY)),
                        intensity = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_INTENSITY)),
                        duration = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_DURATION)),
                        comments = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVITY_COMMENTS))
                    )
                    activityList.add(activity)
                } while (cursor.moveToNext())
            }
        }

        return activityList
    }

    @SuppressLint("Range")
    fun getGoalsForPeriod(startDate: String, endDate: String): List<Goal> {
        val db = this.readableDatabase
        val goalList = mutableListOf<Goal>()

        // Запрос для выборки целей по диапазону дедлайнов
        val query = "SELECT * FROM $TABLE_GOALS WHERE $COLUMN_GOAL_DEADLINE BETWEEN ? AND ?"
        val cursor = db.rawQuery(query, arrayOf(startDate, endDate))

        // Используем курсор для перебора результатов
        if (cursor.moveToFirst()) {
            do {
                val goal = Goal(
                    goal = cursor.getString(cursor.getColumnIndex(COLUMN_GOAL)),
                    deadline = cursor.getString(cursor.getColumnIndex(COLUMN_GOAL_DEADLINE)),
                    notes = cursor.getString(cursor.getColumnIndex(COLUMN_GOAL_NOTES))
                )
                goalList.add(goal)
            } while (cursor.moveToNext())
        }

        // Закрываем курсор после использования
        cursor.close()

        return goalList
    }
}

// Модель для настроений
data class Mood(
    val mood: String,
    val date: String,
    val comment: String,
    val emotions: String
)

// Модель для заметок
data class Note(
    val topic: String,
    val comment: String,
    val timestamp: String
)

// Модель для целей
data class Goal(
    val goal: String,
    val deadline: String,
    val notes: String
)

// Модель для активностей
data class Activity(
    val activityName: String,
    val category: String,
    val intensity: String,
    val duration: String,
    val comments: String
)
