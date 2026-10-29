package com.example.experiment9

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.widget.doAfterTextChanged

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SQLite Database Helper
        dbHelper = DatabaseHelper(this)

        // Initialize SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // -------------------------------------------------------------
        // PART 1: SHAREDPREFERENCES CONTROLS (AUTO-SAVE)
        // -------------------------------------------------------------
        val etPrefName = findViewById<EditText>(R.id.etPrefName)
        val btnSavePref = findViewById<Button>(R.id.btnSavePref)
        val btnLoadPref = findViewById<Button>(R.id.btnLoadPref)
        val btnClearPref = findViewById<Button>(R.id.btnClearPref)
        val tvPrefResult = findViewById<TextView>(R.id.tvPrefResult)

        // Disable Save button because saving happens automatically
        btnSavePref.isEnabled = false
        btnSavePref.text = "Auto-Saved"

        // Automatically display saved name on startup if available
        val initialName = sharedPreferences.getString("saved_name", null)
        if (!initialName.isNullOrEmpty()) {
            tvPrefResult.text = "Saved Name: $initialName"
            etPrefName.setText(initialName)
        }

        // Auto-Save Name to SharedPreferences as soon as the user types
        etPrefName.doAfterTextChanged { text ->
            val name = text?.toString()?.trim().orEmpty()
            if (name.isNotEmpty()) {
                sharedPreferences.edit {
                    putString("saved_name", name)
                }
                tvPrefResult.text = "Auto-Saved Name: $name"
            } else {
                sharedPreferences.edit {
                    remove("saved_name")
                }
                tvPrefResult.text = "Saved Name will appear here"
            }
        }

        // Load Name from SharedPreferences
        btnLoadPref.setOnClickListener {
            val savedName = sharedPreferences.getString("saved_name", null)
            if (savedName.isNullOrEmpty()) {
                tvPrefResult.text = "No saved name found in SharedPreferences"
            } else {
                tvPrefResult.text = "Loaded Name: $savedName"
                Toast.makeText(this, "Loaded: $savedName", Toast.LENGTH_SHORT).show()
            }
        }

        // Clear Name from SharedPreferences
        btnClearPref.setOnClickListener {
            sharedPreferences.edit {
                remove("saved_name")
            }
            etPrefName.text.clear()
            tvPrefResult.text = "SharedPreferences cleared."
            Toast.makeText(this, "Cleared SharedPreferences", Toast.LENGTH_SHORT).show()
        }

        // -------------------------------------------------------------
        // PART 2: SQLITE DATABASE CONTROLS
        // -------------------------------------------------------------
        val etDbName = findViewById<EditText>(R.id.etDbName)
        val etDbEmail = findViewById<EditText>(R.id.etDbEmail)
        val btnAddStudent = findViewById<Button>(R.id.btnAddStudent)
        val btnViewStudents = findViewById<Button>(R.id.btnViewStudents)
        val btnClearDb = findViewById<Button>(R.id.btnClearDb)
        val tvDbResult = findViewById<TextView>(R.id.tvDbResult)

        // Add Student to SQLite Database
        btnAddStudent.setOnClickListener {
            val name = etDbName.text.toString().trim()
            val email = etDbEmail.text.toString().trim()

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please enter both Name and Email", Toast.LENGTH_SHORT).show()
            } else {
                val isInserted = dbHelper.insertStudent(name, email)
                if (isInserted) {
                    Toast.makeText(this, "Student added to SQLite Database", Toast.LENGTH_SHORT).show()
                    etDbName.text.clear()
                    etDbEmail.text.clear()

                    // Automatically update display with all students
                    tvDbResult.text = dbHelper.getAllStudents()
                } else {
                    Toast.makeText(this, "Error inserting record", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // View All Students from SQLite Database
        btnViewStudents.setOnClickListener {
            val allRecords = dbHelper.getAllStudents()
            tvDbResult.text = allRecords
        }

        // Clear All Students from SQLite Database
        btnClearDb.setOnClickListener {
            val deletedCount = dbHelper.deleteAllStudents()
            Toast.makeText(this, "Deleted $deletedCount records", Toast.LENGTH_SHORT).show()
            tvDbResult.text = "Database is now empty."
        }
    }
}
