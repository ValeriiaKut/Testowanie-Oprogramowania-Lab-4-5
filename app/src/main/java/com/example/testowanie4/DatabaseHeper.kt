import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.testowanie4.Person

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "personal_data.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_PERSONS = "persons"
        const val COL_ID = "id"
        const val COL_FIRST_NAME = "first_name"
        const val COL_LAST_NAME = "last_name"
        const val COL_BIRTH_DATE = "birth_date"
        const val COL_PHONE = "phone"
        const val COL_EMAIL = "email"
        const val COL_ADDRESS = "address"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_PERSONS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_FIRST_NAME TEXT NOT NULL,
                $COL_LAST_NAME TEXT NOT NULL,
                $COL_BIRTH_DATE TEXT NOT NULL,
                $COL_PHONE TEXT NOT NULL,
                $COL_EMAIL TEXT NOT NULL,
                $COL_ADDRESS TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PERSONS")
        onCreate(db)
    }

    fun insertPerson(person: Person): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_FIRST_NAME, person.firstName)
            put(COL_LAST_NAME, person.lastName)
            put(COL_BIRTH_DATE, person.birthDate)
            put(COL_PHONE, person.phone)
            put(COL_EMAIL, person.email)
            put(COL_ADDRESS, person.address)
        }
        val id = db.insert(TABLE_PERSONS, null, values)
        db.close()
        return id
    }

    fun getAllPersons(): List<Person> {
        val list = mutableListOf<Person>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PERSONS ORDER BY $COL_ID DESC", null)

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    list.add(
                        Person(
                            id = it.getLong(it.getColumnIndexOrThrow(COL_ID)),
                            firstName = it.getString(it.getColumnIndexOrThrow(COL_FIRST_NAME)),
                            lastName = it.getString(it.getColumnIndexOrThrow(COL_LAST_NAME)),
                            birthDate = it.getString(it.getColumnIndexOrThrow(COL_BIRTH_DATE)),
                            phone = it.getString(it.getColumnIndexOrThrow(COL_PHONE)),
                            email = it.getString(it.getColumnIndexOrThrow(COL_EMAIL)),
                            address = it.getString(it.getColumnIndexOrThrow(COL_ADDRESS))
                        )
                    )
                } while (it.moveToNext())
            }
        }
        db.close()
        return list
    }

    fun deletePersonById(id: Long): Int {
        val db = writableDatabase
        val deleted = db.delete(TABLE_PERSONS, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
        return deleted
    }
}