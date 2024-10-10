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
        private const val DATABASE_VERSION = 7
        const val TABLE_USERS = "users"
        const val TABLE_MOODS = "moods"
        const val COLUMN_ID = "_id"
        const val COLUMN_LOGIN = "login"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_NAME = "name"
        const val COLUMN_MOOD_DATE = "date"
        const val COLUMN_MOOD_COMMENT = "comment"
        const val COLUMN_MOOD_EMOTIONS = "emotions"
        const val COLUMN_MOOD = "mood"
        const val COLUMN_USER_ID = "user_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            val createUserTable = ("CREATE TABLE $TABLE_USERS (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_LOGIN TEXT UNIQUE, " +
                    "$COLUMN_PASSWORD TEXT, " +
                    "$COLUMN_NAME TEXT, " +
                    "profile_image BLOB)"
                    )

            val createMoodTable = ("CREATE TABLE $TABLE_MOODS (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_USER_ID INTEGER, " +
                    "$COLUMN_MOOD TEXT, " +
                    "$COLUMN_MOOD_DATE TEXT, " +
                    "$COLUMN_MOOD_COMMENT TEXT, " +
                    "$COLUMN_MOOD_EMOTIONS TEXT, " +
                    "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID))"
                    )
            db.execSQL(createUserTable)
            db.execSQL(createMoodTable)
        } catch (e: Exception) {
            // Handle any exceptions (logging, etc.)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOODS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS") // Drop the users table
        onCreate(db)
    }

    // Добавление настроения с датой, комментарием и эмоциями
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

    // Добавление пользователя
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

    @SuppressLint("Range")
    fun getPassword(login: String): String? {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT $COLUMN_PASSWORD FROM $TABLE_USERS WHERE $COLUMN_LOGIN = ?", arrayOf(login))
        var password: String? = null
        if (cursor.moveToFirst()) {
            password = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD))
        }
        cursor.close()
        return password
    }

    fun updateUser(name: String, username: String, password: String, profileImage: ByteArray?): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_PASSWORD, password)
            put("profile_image", profileImage)
        }
        val result = db.update(TABLE_USERS, values, "$COLUMN_LOGIN = ?", arrayOf(username))
        return result > 0
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
}
