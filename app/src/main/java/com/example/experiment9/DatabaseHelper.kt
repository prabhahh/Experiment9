package com.example.experiment9

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "StudentDB.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "students"
        private const val COL_ID = "id"
        private const val COL_NAME = "name"
        private const val COL_EMAIL = "email"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT, "
                + COL_EMAIL + " TEXT)")
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Insert a new student record
    fun insertStudent(name: String, email: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COL_NAME, name)
            put(COL_EMAIL, email)
        }
        val result = db.insert(TABLE_NAME, null, contentValues)
        return result != -1L
    }

    // Retrieve all student records
    fun getAllStudents(): String {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        val builder = StringBuilder()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL))
                builder.append("ID: $id | Name: $name | Email: $email\n")
            } while (cursor.moveToNext())
        } else {
            builder.append("No student records found.")
        }
        cursor.close()
        return builder.toString()
    }

    // Clear all records from the database
    fun deleteAllStudents(): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, null, null)
    }
}
