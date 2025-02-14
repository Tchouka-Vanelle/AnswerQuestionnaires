package edu.ufp.pam.wellbeing.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.*

class SessionManager(private val context: Context) {

    // Lazy initialization of SharedPreferences
    private val prefs by lazy {
        context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    }

    // SharedPreferences keys
    private val LAST_LOGIN_TIME = "lastLoginTime"
    private val USERNAME = "username"
    private val USER_ID = "userId"
    private val KEEP_LOGGED_IN = "keepLoggedIn"

    // Save the user's session information
    fun saveLoginSession(username: String, userId: Int, keepLoggedIn: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            prefs.edit().apply {
                putString(USERNAME, username)
                putInt(USER_ID, userId)
                putLong(LAST_LOGIN_TIME, if (keepLoggedIn) 0 else System.currentTimeMillis())
                putBoolean(KEEP_LOGGED_IN, keepLoggedIn)
                apply()
            }
        }
    }

    // Retrieve the username from the session in the background
    suspend fun getUsernameFromSession(): String? = withContext(Dispatchers.IO) {
        prefs.getString(USERNAME, null)
    }

    // Retrieve the user ID from the session in the background
    suspend fun getUserIdFromSession(): Int = withContext(Dispatchers.IO) {
        prefs.getInt(USER_ID, -1)
    }

    // Check if the session is still valid
    suspend fun isSessionValid(): Boolean = withContext(Dispatchers.IO) {
        val keepLoggedIn = prefs.getBoolean(KEEP_LOGGED_IN, false)
        if (keepLoggedIn && prefs.getString(USERNAME, null) != null) {
            return@withContext true
        }
        val lastLoginTime = prefs.getLong(LAST_LOGIN_TIME, 0L)
        val currentTime = System.currentTimeMillis()
        return@withContext (currentTime - lastLoginTime) <= 5 * 60 * 1000 // 5 minutes
    }

    // Clear the user's session
    fun clearSession() {
        CoroutineScope(Dispatchers.IO).launch {
            prefs.edit().apply {
                remove(LAST_LOGIN_TIME)
                remove(USERNAME)
                remove(USER_ID)
                apply()
            }
        }
    }
}
