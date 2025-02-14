package edu.ufp.pam.wellbeing.databases

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import edu.ufp.pam.wellbeing.data.SessionManager
import edu.ufp.pam.wellbeing.databases.entities.Answer
import edu.ufp.pam.wellbeing.databases.entities.Question
import edu.ufp.pam.wellbeing.databases.entities.Questionnaire
import edu.ufp.pam.wellbeing.databases.entities.User
import edu.ufp.pam.wellbeing.databases.entitiesdao.AnswerDao
import edu.ufp.pam.wellbeing.databases.entitiesdao.QuestionDao
import edu.ufp.pam.wellbeing.databases.entitiesdao.QuestionnaireDao
import edu.ufp.pam.wellbeing.databases.entitiesdao.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Questionnaire::class,
        Question::class,
        Answer::class,
    ],
    version = 1,
    exportSchema = false
)

@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun questionnaireDao(): QuestionnaireDao
    abstract fun questionDao(): QuestionDao
    abstract fun answerDao(): AnswerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getAppDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user-database"
                )
                    // Allow queries on the main thread. Avoid in production apps.
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getAppDatabase2(context: Context,  scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user-database"
                )
                    .fallbackToDestructiveMigration() //to delete and recreate the bdd when version change
                    .addCallback(WellBeingDatabaseCallback(context, scope))
                    .build()

                INSTANCE = instance
                instance
            }
        }

        private class WellBeingDatabaseCallback(
            private val context: Context,
            private val scope: CoroutineScope) :
            Callback()
        {

            /** Override onOpen() to clear and populate DB every time app is started. */
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // To keep DB data through app restarts comment coroutine exec:
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        Log.d("DatabaseCallback", "Starting database seeding...")
                        DatabaseSeeder.preFillDatabase(
                            database.questionnaireDao(),
                            database.questionDao()
                        )
                        Log.d("DatabaseCallback", "Database seeding completed.")
                    }
                }
                // Clear session every time the database is launched
                val sessionManager = SessionManager(context)
                sessionManager.clearSession()
            }

            /** overrite onCreate() to populate DB only first time app is launched. */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                //To clear and repopulate DB every time app is started comment coroutine exec:
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        /*Log.d("DatabaseCallback", "Starting database seeding...")
                        DatabaseSeeder.preFillDatabase(
                            database.questionnaireDao(),
                            database.questionDao()
                        )
                        Log.d("DatabaseCallback", "Database seeding completed.")

                         */
                    }
                }
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}