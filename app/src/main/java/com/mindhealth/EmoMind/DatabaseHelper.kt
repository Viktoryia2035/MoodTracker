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
        private const val DATABASE_VERSION = 9

        const val TABLE_USERS = "users"
        const val TABLE_MOODS = "moods"
        const val TABLE_ACTIVITIES = "activities"
        const val TABLE_NOTES = "notes" // Таблица для заметок
        const val COLUMN_ID = "_id"
        const val COLUMN_LOGIN = "login"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_NAME = "name"
        const val COLUMN_MOOD_DATE = "date"
        const val COLUMN_MOOD_COMMENT = "comment"
        const val COLUMN_MOOD_EMOTIONS = "emotions"
        const val COLUMN_MOOD = "mood"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_ACTIVITY_NAME = "activity_name"
        const val COLUMN_ACTIVITY_CATEGORY = "category"
        const val COLUMN_ACTIVITY_INTENSITY = "intensity"
        const val COLUMN_ACTIVITY_DURATION = "duration"
        const val COLUMN_ACTIVITY_COMMENTS = "comments"
        const val COLUMN_ACTIVITY_TIMESTAMP = "timestamp"

        // Столбцы для таблицы заметок
        const val COLUMN_NOTE_TOPIC = "topic"
        const val COLUMN_NOTE_COMMENT = "comment"
        const val COLUMN_NOTE_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            // Создание таблицы пользователей
            val createUserTable = ("CREATE TABLE $TABLE_USERS (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_LOGIN TEXT UNIQUE, " +
                    "$COLUMN_PASSWORD TEXT, " +
                    "$COLUMN_NAME TEXT, " +
                    "profile_image BLOB)"
                    )

            // Создание таблицы настроений
            val createMoodTable = ("CREATE TABLE $TABLE_MOODS (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_USER_ID INTEGER, " +
                    "$COLUMN_MOOD TEXT, " +
                    "$COLUMN_MOOD_DATE TEXT, " +
                    "$COLUMN_MOOD_COMMENT TEXT, " +
                    "$COLUMN_MOOD_EMOTIONS TEXT, " +
                    "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID))"
                    )

            // Создание таблицы активностей
            val createActivityTable = ("CREATE TABLE $TABLE_ACTIVITIES (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_ACTIVITY_NAME TEXT, " +
                    "$COLUMN_ACTIVITY_CATEGORY TEXT, " +
                    "$COLUMN_ACTIVITY_INTENSITY TEXT, " +
                    "$COLUMN_ACTIVITY_DURATION TEXT, " +
                    "$COLUMN_ACTIVITY_COMMENTS TEXT, " +
                    "$COLUMN_ACTIVITY_TIMESTAMP TEXT)"
                    )

            // Создание таблицы заметок
            val createNotesTable = ("CREATE TABLE $TABLE_NOTES (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_NOTE_TOPIC TEXT, " +
                    "$COLUMN_NOTE_COMMENT TEXT, " +
                    "$COLUMN_NOTE_TIMESTAMP TEXT)"
                    )

            db.execSQL(createUserTable)
            db.execSQL(createMoodTable)
            db.execSQL(createActivityTable)
            db.execSQL(createNotesTable)

        } catch (e: Exception) {
            // Обработка исключений (логирование и т.д.)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACTIVITIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOODS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES") // Удаляем таблицу заметок при апгрейде
        onCreate(db)
    }

    // Метод для добавления настроений
    fun addMood(userId: Long, mood: String, date: String, comment: String, emotions: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_MOOD, mood)
            put(COLUMN_MOOD_DATE, date)
            put(COLUMN_MOOD_COMMENT, comment)
            put(COLUMN_MOOD_EMOTIONS, emotions)
        }
        val result = db.insert(TABLE_MOODS, null, values)
        return result != -1L
    }

    // Метод для добавления активности
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

    // Метод для добавления заметок
    fun addNote(topic: String, comment: String, timestamp: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOTE_TOPIC, topic)  // Категория (тема заметки)
            put(COLUMN_NOTE_COMMENT, comment)  // Комментарий
            put(COLUMN_NOTE_TIMESTAMP, timestamp)  // Время записи
        }
        val result = db.insert(TABLE_NOTES, null, values)
        return result != -1L
    }

    // Получение ID пользователя по логину
    @SuppressLint("Range")
    fun getUserId(login: String): Long? {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT $COLUMN_ID FROM $TABLE_USERS WHERE $COLUMN_LOGIN = ?", arrayOf(login))
        return if (cursor.moveToFirst()) {
            cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
        } else {
            null
        }.also {
            cursor.close()
        }
    }

    // Проверка пользователя при авторизации
    fun checkUser(login: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_LOGIN = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(login, password))

        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // Метод для добавления пользователя
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

    // Получение всех пользователей
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

    fun isUserValid(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_LOGIN = ? AND $COLUMN_PASSWORD = ?", arrayOf(username, password))
        val isValid = cursor.count > 0
        cursor.close()
        return isValid
    }

    @SuppressLint("Range")
    fun getPassword(login: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_PASSWORD FROM $TABLE_USERS WHERE $COLUMN_LOGIN = ?", arrayOf(login))

        return if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD))
        } else {
            null
        }.also {
            cursor.close()
        }
    }

    fun updateUser(login: String, newName: String?, newPassword: String?): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()

        newName?.let {
            values.put(COLUMN_NAME, it)
        }

        newPassword?.let {
            values.put(COLUMN_PASSWORD, it)
        }

        val result = db.update(TABLE_USERS, values, "$COLUMN_LOGIN = ?", arrayOf(login))
        return result > 0
    }
}