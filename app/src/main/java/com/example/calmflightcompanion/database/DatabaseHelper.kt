package com.example.calmflightcompanion.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "cfc_database.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_FREQUENT = "FrequentQuestions"
        const val TABLE_DIARY = "FlightDiary"

        // Columns for the frequent questions table.
        const val COLUMN_FREQUENT_ID = "id_question"
        const val COLUMN_FREQUENT_QUESTION = "question"
        const val COLUMN_FREQUENT_ANSWER = "answer"

        // Columns for the Flight Diary table.
        const val COLUMN_FLIGHT_DIARY_ENTRY_ID = "id_entry"
        const val COLUMN_FLIGHT_DIARY_ORIGIN = "origin"
        const val COLUMN_FLIGHT_DIARY_DESTINATION = "destination"
        const val COLUMN_FLIGHT_DIARY_DATE = "date"
        const val COLUMN_FLIGHT_DIARY_ANXIETY_LEVEL = "anxiety_level"
        const val COLUMN_FLIGHT_DIARY_DESCRIPTION = "description"

        private const val CREATE_FREQUENT_QUESTIONS_TABLE = """ 
            CREATE TABLE $TABLE_FREQUENT (
                $COLUMN_FREQUENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FREQUENT_QUESTION TEXT,
                $COLUMN_FREQUENT_ANSWER TEXT
            )
        """

        private const val CREATE_FLIGHT_DIARY_TABLE = """
            CREATE TABLE $TABLE_DIARY (
                $COLUMN_FLIGHT_DIARY_ENTRY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FLIGHT_DIARY_ORIGIN TEXT,
                $COLUMN_FLIGHT_DIARY_DESTINATION TEXT,
                $COLUMN_FLIGHT_DIARY_DATE DATETIME DEFAULT CURRENT_TIMESTAMP,
                $COLUMN_FLIGHT_DIARY_ANXIETY_LEVEL INTEGER,
                $COLUMN_FLIGHT_DIARY_DESCRIPTION TEXT
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_FREQUENT_QUESTIONS_TABLE)
        db.execSQL(CREATE_FLIGHT_DIARY_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $CREATE_FLIGHT_DIARY_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $CREATE_FREQUENT_QUESTIONS_TABLE")
        onCreate(db)
    }

    fun deleteAll() {
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FREQUENT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DIARY")
        onCreate(db)
    }

    // FrequentQuestions CRUD operations
    // Insert a new question-answer pair into FrequentQuestions
    fun insertFrequentQuestion(question: String, answer: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FREQUENT_QUESTION, question)
            put(COLUMN_FREQUENT_ANSWER, answer)
        }
        return db.insert(TABLE_FREQUENT, null, values)
    }

    // Get all questions from FrequentQuestions
    fun getAllFrequentQuestions(): Cursor {
        val db = this.readableDatabase
        return db.query(TABLE_FREQUENT, null, null, null, null, null, "$COLUMN_FREQUENT_ID ASC")
    }

    // Update a question-answer pair in FrequentQuestions
    fun updateFrequentQuestion(id: Long, question: String, answer: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FREQUENT_QUESTION, question)
            put(COLUMN_FREQUENT_ANSWER, answer)
        }
        return db.update(TABLE_FREQUENT, values, "$COLUMN_FREQUENT_ID = ?", arrayOf(id.toString()))
    }

    // Delete a question-answer pair from FrequentQuestions
    fun deleteFrequentQuestion(id: Long): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_FREQUENT, "$COLUMN_FREQUENT_ID = ?", arrayOf(id.toString()))
    }

    // FlightDiary CRUD operations.
    // Insert a new diary entry into FlightDiary
    fun insertFlightDiaryEntry(origin: String, destination: String, anxietyLevel: Int, description: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FLIGHT_DIARY_ORIGIN, origin)
            put(COLUMN_FLIGHT_DIARY_DESTINATION, destination)
            put(COLUMN_FLIGHT_DIARY_ANXIETY_LEVEL, anxietyLevel)
            put(COLUMN_FLIGHT_DIARY_DESCRIPTION, description)
        }
        return db.insert(TABLE_DIARY, null, values)
    }

    // Get all entries from FlightDiary
    fun getAllFlightDiaryEntries(): Cursor {
        val db = this.readableDatabase
        return db.query(TABLE_DIARY, null, null, null, null, null, "$COLUMN_FLIGHT_DIARY_DATE DESC")
    }

    // Update a diary entry in FlightDiary
    fun updateFlightDiaryEntry(id: Long, origin: String, destination: String, anxietyLevel: Int, description: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FLIGHT_DIARY_ORIGIN, origin)
            put(COLUMN_FLIGHT_DIARY_DESTINATION, destination)
            put(COLUMN_FLIGHT_DIARY_ANXIETY_LEVEL, anxietyLevel)
            put(COLUMN_FLIGHT_DIARY_DESCRIPTION, description)
        }
        return db.update(TABLE_DIARY, values, "$COLUMN_FLIGHT_DIARY_ENTRY_ID = ?", arrayOf(id.toString()))
    }

    // Delete a diary entry from FlightDiary
    fun deleteFlightDiaryEntry(id: Long): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_DIARY, "$COLUMN_FLIGHT_DIARY_ENTRY_ID = ?", arrayOf(id.toString()))
    }

    fun testDatabase() {
        // Insert sample data into the FrequentQuestions table
        this.insertFrequentQuestion("What is turbulence?", "Turbulence is a common occurrence during flights...")
        this.insertFrequentQuestion("Why do ears pop during flights?", "This happens due to pressure changes in the cabin.")

        // Insert sample data into the FlightDiary table
        this.insertFlightDiaryEntry("Los Angeles", "New York", 5, "Felt anxious during takeoff but calmed down after reaching altitude.")
        this.insertFlightDiaryEntry("San Francisco", "Chicago", 3, "Mild anxiety but overall a smooth flight.")

        // Retrieve and log all entries from the FrequentQuestions table
        val questionsCursor: Cursor = this.getAllFrequentQuestions()
        if (questionsCursor.moveToFirst()) {
            do {
                val question = questionsCursor.getString(questionsCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FREQUENT_QUESTION))
                val answer = questionsCursor.getString(questionsCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FREQUENT_ANSWER))
                Log.d("Database", "Question: $question, Answer: $answer")
            } while (questionsCursor.moveToNext())
        }
        questionsCursor.close()

        // Retrieve and log all entries from the FlightDiary table
        val entriesCursor: Cursor = this.getAllFlightDiaryEntries()
        if (entriesCursor.moveToFirst()) {
            do {
                val origin = entriesCursor.getString(entriesCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FLIGHT_DIARY_ORIGIN))
                val destination = entriesCursor.getString(entriesCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FLIGHT_DIARY_DESTINATION))
                val anxietyLevel = entriesCursor.getInt(entriesCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FLIGHT_DIARY_ANXIETY_LEVEL))
                val description = entriesCursor.getString(entriesCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FLIGHT_DIARY_DESCRIPTION))
                Log.d("Database", "Origin: $origin, Destination: $destination, Anxiety Level: $anxietyLevel, Description: $description")
            } while (entriesCursor.moveToNext())
        }
        entriesCursor.close()
    }
}