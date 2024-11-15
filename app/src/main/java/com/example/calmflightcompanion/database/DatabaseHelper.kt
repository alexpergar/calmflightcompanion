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

    // FrequentQuestions CRUD operations.
    // Insert a new question-answer pair into FrequentQuestions.
    fun insertFrequentQuestion(question: String, answer: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FREQUENT_QUESTION, question)
            put(COLUMN_FREQUENT_ANSWER, answer)
        }
        return db.insert(TABLE_FREQUENT, null, values)
    }

    // Get all questions from FrequentQuestions.
    fun getAllFrequentQuestions(): Cursor {
        val db = this.readableDatabase
        return db.query(TABLE_FREQUENT, null, null, null, null, null, "$COLUMN_FREQUENT_ID ASC")
    }

    // FlightDiary CRUD operations.
    // Insert a new diary entry into FlightDiary.
    fun insertFlightDiaryEntry(origin: String, destination: String, anxietyLevel: Int, description: String, date: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FLIGHT_DIARY_ORIGIN, origin)
            put(COLUMN_FLIGHT_DIARY_DESTINATION, destination)
            put(COLUMN_FLIGHT_DIARY_ANXIETY_LEVEL, anxietyLevel)
            put(COLUMN_FLIGHT_DIARY_DESCRIPTION, description)
            put(COLUMN_FLIGHT_DIARY_DATE, date)
        }
        return db.insert(TABLE_DIARY, null, values)
    }

    // Get all entries from FlightDiary.
    fun getAllFlightDiaryEntries(): Cursor {
        val db = this.readableDatabase
        return db.query(TABLE_DIARY, null, null, null, null, null, "$COLUMN_FLIGHT_DIARY_DATE DESC")
    }

    // Update a diary entry in FlightDiary.
    fun updateFlightDiaryEntry(id: Long, origin: String, destination: String, anxietyLevel: Int, description: String, date: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FLIGHT_DIARY_ORIGIN, origin)
            put(COLUMN_FLIGHT_DIARY_DESTINATION, destination)
            put(COLUMN_FLIGHT_DIARY_ANXIETY_LEVEL, anxietyLevel)
            put(COLUMN_FLIGHT_DIARY_DESCRIPTION, description)
            put(COLUMN_FLIGHT_DIARY_DATE, date)
        }
        return db.update(TABLE_DIARY, values, "$COLUMN_FLIGHT_DIARY_ENTRY_ID = ?", arrayOf(id.toString()))
    }

    // Delete a diary entry from FlightDiary.
    fun deleteFlightDiaryEntry(id: Long): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_DIARY, "$COLUMN_FLIGHT_DIARY_ENTRY_ID = ?", arrayOf(id.toString()))
    }

    // Get a diary entry by ID.
    fun getFlightDiaryEntryById(id: Long): Cursor? {
        val db = this.readableDatabase
        return db.query(
            TABLE_DIARY,
            null,
            "$COLUMN_FLIGHT_DIARY_ENTRY_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
    }

    // Check if the database is empty.
    fun isDatabaseEmpty(): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_FREQUENT", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count == 0
    }

    // Insert data into the database.
    fun initiateDatabase() {
        // Insert sample data into the FrequentQuestions table
        this.insertFrequentQuestion("¿Qué es la turbulencia?", "La turbulencia es un fenómeno común durante los vuelos causado por movimientos irregulares del aire, a menudo debido a condiciones meteorológicas, corrientes en chorro o montañas.")
        this.insertFrequentQuestion("¿Cómo de seguros son los aviones?", "Los aviones son uno de los medios de transporte más seguros. Están diseñados con múltiples sistemas de seguridad y los pilotos reciben entrenamiento riguroso.")
        this.insertFrequentQuestion("¿Qué pasa si el avión entra en turbulencia?", "La turbulencia es como conducir por un camino con baches. Los aviones están diseñados para resistirla, y aunque puede ser incómoda, rara vez representa un riesgo.")
        this.insertFrequentQuestion("¿Y si algo sale mal durante el vuelo?", "Los aviones están equipados con sistemas de respaldo para casi todo. Además, los pilotos están altamente capacitados para manejar emergencias.")
        this.insertFrequentQuestion("¿Con qué frecuencia se revisan los aviones?", "Los aviones se inspeccionan antes de cada vuelo y reciben mantenimientos regulares, siguiendo estrictos estándares internacionales.")
        this.insertFrequentQuestion("¿Puede un avión aterrizar si pierde un motor?", "Sí, los aviones pueden volar y aterrizar con seguridad incluso si pierden un motor, ya que están diseñados para funcionar en estas situaciones.")
        this.insertFrequentQuestion("¿Qué pasa si el avión es alcanzado por un rayo?", "Los aviones están diseñados para soportar impactos de rayos. Estos atraviesan el fuselaje sin causar daño gracias a su construcción especial.")
        this.insertFrequentQuestion("¿Cómo sé que los pilotos son confiables?", "Los pilotos pasan por años de formación, simulaciones avanzadas y evaluaciones periódicas para garantizar su preparación.")
        this.insertFrequentQuestion("¿Qué pasa si hay un problema técnico en el aire?", "Los aviones tienen sistemas redundantes para manejar problemas técnicos. Además, los pilotos están entrenados para resolver situaciones inusuales.")
        this.insertFrequentQuestion("¿Por qué se sienten extraños los despegues y aterrizajes?", "Es normal sentir presión o vibraciones durante estas fases, pero no indican problemas. Son parte del funcionamiento normal del avión.")
        this.insertFrequentQuestion("¿Qué pasa si hay un problema de presión en la cabina?", "Los aviones están equipados con máscaras de oxígeno que se despliegan automáticamente para garantizar tu seguridad.")
        this.insertFrequentQuestion("¿Puede el avión quedarse sin combustible?", "No, los vuelos se planifican con combustible extra para emergencias y desvíos, asegurando que siempre haya suficiente.")
        this.insertFrequentQuestion("¿Qué tan raro es que ocurra un accidente aéreo?", "Los accidentes aéreos son extremadamente raros. Tienes más probabilidades de ser alcanzado por un rayo que de estar en un accidente aéreo.")
        this.insertFrequentQuestion("¿Qué hacen los pilotos durante el vuelo?", "Los pilotos monitorean constantemente los sistemas del avión y se comunican con el control de tráfico aéreo para garantizar un vuelo seguro.")
        this.insertFrequentQuestion("¿Por qué se apagan las luces de la cabina durante el despegue y el aterrizaje?", "Esto se hace como medida de seguridad, para que tus ojos se adapten a la oscuridad en caso de una emergencia.")
        this.insertFrequentQuestion("¿Es seguro volar durante tormentas?", "Los aviones están diseñados para volar en condiciones climáticas adversas, y los pilotos siempre buscan rutas seguras para evitar las tormentas.")
        this.insertFrequentQuestion("¿Qué tan comunes son los problemas técnicos?", "Los problemas técnicos graves son muy raros. Además, los aviones se revisan constantemente para garantizar su buen funcionamiento.")
        this.insertFrequentQuestion("¿Qué pasa si el avión vuela sobre agua?", "Los aviones están preparados para vuelos largos sobre agua y cuentan con equipos como balsas salvavidas y chalecos en caso de emergencia.")
        this.insertFrequentQuestion("¿Qué hace el personal de cabina para garantizar la seguridad?", "El personal de cabina está entrenado en primeros auxilios, evacuación y procedimientos de emergencia para cuidar de los pasajeros.")
        this.insertFrequentQuestion("¿Por qué a veces se siente que el avión cae?", "Esa sensación ocurre por cambios normales en la velocidad o altitud debido a corrientes de aire, pero el avión siempre está bajo control.")
        this.insertFrequentQuestion("¿Cómo puedo calmarme durante el vuelo?", "Respirar profundamente, escuchar música relajante o distraerte con películas puede ayudarte. También puedes avisar al personal si necesitas apoyo.")
        this.insertFrequentQuestion("¿Por qué se taponan los oídos durante el vuelo?", "Los oídos se taponan debido a los cambios de presión en la cabina cuando el avión asciende o desciende. Tragar saliva o masticar chicle puede ayudar.")
        this.insertFrequentQuestion("¿Con cuánta antelación debo llegar al aeropuerto?", "Se recomienda llegar al menos 2 horas antes para vuelos nacionales y 3 horas antes para vuelos internacionales.")
        this.insertFrequentQuestion("¿Qué objetos están prohibidos en el equipaje de mano?", "Están prohibidos objetos afilados, líquidos inflamables y líquidos de más de 100 ml. Siempre revisa las regulaciones de tu aerolínea.")
        this.insertFrequentQuestion("¿Puedo llevar comida en el avión?", "Sí, puedes llevar comida en el avión, pero debe cumplir con las regulaciones de seguridad, especialmente para vuelos internacionales.")
        this.insertFrequentQuestion("¿Por qué se retrasan los vuelos?", "Los retrasos pueden deberse a condiciones meteorológicas, problemas técnicos, tráfico aéreo o disponibilidad de la tripulación.")
        this.insertFrequentQuestion("¿Qué pasa si pierdo mi vuelo?", "Si pierdes tu vuelo, contacta a la aerolínea inmediatamente. Algunas aerolíneas pueden reprogramarte en el siguiente vuelo disponible, pero podrían aplicarse cargos.")
        this.insertFrequentQuestion("¿Por qué algunos vuelos están sobrevendidos?", "Las aerolíneas sobrevenden vuelos para compensar a los pasajeros que no se presentan. Si todos se presentan, algunos pasajeros podrían ser reubicados en otro vuelo.")
        this.insertFrequentQuestion("¿Cuál es el límite de peso para el equipaje facturado?", "Los límites de peso varían según la aerolínea, pero generalmente oscilan entre 23 kg (50 lbs) y 32 kg (70 lbs) para clase económica.")
        this.insertFrequentQuestion("¿Qué debo hacer si pierdo mi equipaje?", "Reporta el equipaje perdido a la aerolínea de inmediato. Proporciona una descripción detallada y tus datos de contacto.")
        this.insertFrequentQuestion("¿Por qué debo apagar los dispositivos electrónicos durante el despegue y el aterrizaje?", "Los dispositivos electrónicos pueden interferir con los sistemas de comunicación y navegación del avión durante las fases críticas del vuelo.")
        this.insertFrequentQuestion("¿Puedo usar Wi-Fi en el avión?", "Muchas aerolíneas ofrecen Wi-Fi a bordo por un costo adicional, aunque la velocidad puede variar.")
        this.insertFrequentQuestion("¿Para qué sirve la demostración de seguridad?", "La demostración de seguridad asegura que los pasajeros estén informados sobre los procedimientos de emergencia y la ubicación del equipo de seguridad.")
        this.insertFrequentQuestion("¿Por qué las ventanas de los aviones tienen pequeños agujeros?", "Los pequeños agujeros en las ventanas ayudan a regular la presión de la cabina y evitan que el panel exterior soporte demasiado estrés.")
        this.insertFrequentQuestion("¿Puedo viajar con mi mascota?", "Muchas aerolíneas permiten viajar con mascotas en la cabina o en la bodega, pero los requisitos y tarifas varían. Consulta con tu aerolínea antes de reservar.")
        this.insertFrequentQuestion("¿Por qué los asientos de los aviones son tan estrechos?", "Las aerolíneas optimizan el espacio para maximizar el número de pasajeros por vuelo, especialmente en clase económica.")
        this.insertFrequentQuestion("¿Cómo se filtra el aire en la cabina?", "Los aviones modernos utilizan filtros HEPA que eliminan el 99,97% de bacterias, virus y partículas del aire.")
        this.insertFrequentQuestion("¿Qué debo usar en un vuelo largo?", "Usa ropa cómoda y holgada, y considera llevar capas para adaptarte a los cambios de temperatura.")
        this.insertFrequentQuestion("¿Por qué se atenúan las luces de la cabina durante el despegue y el aterrizaje?", "Las luces se atenúan para que los ojos de los pasajeros se adapten a la oscuridad en caso de una evacuación de emergencia.")
        this.insertFrequentQuestion("¿Puedo mejorar mi asiento después de reservar?", "Sí, las mejoras pueden estar disponibles por un costo adicional o utilizando millas de viajero frecuente. Contacta a tu aerolínea para conocer las opciones.")

        // Insert sample data into the FlightDiary table
        this.insertFlightDiaryEntry("Barcelona", "Málaga", 5, "Estuve un poco nervioso en el despegue, pero luego bien.", date="2021-09-01")
        this.insertFlightDiaryEntry("Málaga", "Granada", 3, "El vuelo fue bien aunque hubo turbulencias a mitad del viaje.", date="2021-09-30")
    }
}