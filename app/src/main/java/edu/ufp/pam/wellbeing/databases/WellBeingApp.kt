package edu.ufp.pam.wellbeing.databases

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

import android.os.StrictMode
import android.util.Log
import edu.ufp.pam.wellbeing.BuildConfig


/*
* Extending Application:
*
* WellBeingApp inherits from the Application class, which is a global singleton in Android.
* It's created before any activity or service and is used to initialize app-wide resources.
*/
class WellBeingApp : Application() {

    // CoroutineScope for database operations
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Lazy initialization for the database and repository
    val database: AppDatabase by lazy {
        AppDatabase.getAppDatabase2(this, applicationScope)
    }
    val appRepository: MainRepository by lazy {
        MainRepository(
            database.userDao(),
            database.questionnaireDao(),
            database.questionDao(),
            database.answerDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        // Enable StrictMode in debug mode only
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll() // Detect all violations
                    .penaltyLog() // Log the violations
                    .build()
            )
            Log.d("MyApplication", "StrictMode is enabled in Debug mode.")
        }

    }

}
