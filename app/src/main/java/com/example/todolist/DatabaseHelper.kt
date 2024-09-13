package com.example.todolist

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "todolist.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_TASKS = "tasks"
        private const val TABLE_USERS = "users"
        const val COLUMN_ID = "_id"
        const val COLUMN_TASK = "task"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create tables
        val createTasksTable = """
            CREATE TABLE $TABLE_TASKS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TASK TEXT,
                $COLUMN_USER_ID INTEGER
            )
        """
        db.execSQL(createTasksTable)

        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE,
                $COLUMN_PASSWORD TEXT
            )
        """
        db.execSQL(createUsersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // User management
    fun createUser(username: String, password: String): Long? {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
        }
        val userId = db.insert(TABLE_USERS, null, values)
        return if (userId == -1L) null else userId
    }

    fun getUserId(username: String, password: String): Long? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID),
            "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password),
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
        } else {
            null
        }
    }

    // Task management
    fun addTask(userId: Long, task: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TASK, task)
            put(COLUMN_USER_ID, userId)
        }
        db.insert(TABLE_TASKS, null, values)
    }

    fun updateTask(taskId: Long, updatedTask: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TASK, updatedTask)
        }
        db.update(TABLE_TASKS, values, "$COLUMN_ID = ?", arrayOf(taskId.toString()))
    }

    fun deleteTask(taskId: Long) {
        val db = this.writableDatabase
        db.delete(TABLE_TASKS, "$COLUMN_ID = ?", arrayOf(taskId.toString()))
    }

    fun getAllTasks(userId: Long): Cursor {
        val db = this.readableDatabase
        return db.query(
            TABLE_TASKS,
            null,
            "$COLUMN_USER_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )
    }
}
