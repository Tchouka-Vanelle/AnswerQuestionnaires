package edu.ufp.pam.wellbeing.databases.entitiesdao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import edu.ufp.pam.wellbeing.databases.entities.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE username LIKE :username AND password LIKE :password")
    suspend fun findByName(username: String, password: String): User

    @Query("SELECT * FROM user WHERE email LIKE :email")
    suspend fun findByEmail(email: String): User?

    @Query("SELECT * FROM user WHERE username LIKE :username LIMIT 1")
    suspend fun findByUsername(username: String): User?

    @Query("SELECT COUNT(*) FROM user")
    suspend fun countUsers(): Int

    @Insert
    suspend fun insertAll(vararg users: User): List<Long>

    @Delete
    suspend fun delete(user: User)
}