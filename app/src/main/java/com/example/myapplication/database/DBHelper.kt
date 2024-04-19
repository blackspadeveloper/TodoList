package com.example.myapplication.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DBHelper(context: Context): SQLiteOpenHelper(context,
    DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "todoList.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "todo"
        private const val COLUMN_ID = "id"
        const val COLUMN_TASK = "task"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableStatement = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TASK TEXT)"
        db?.execSQL(createTableStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int,
                           newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addTask(task: String): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COLUMN_TASK, task)
        val result = db.insert(TABLE_NAME, null, cv)
        db.close()
        return result != (-1).toLong()
    }

    fun getAllTasks(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }
}
