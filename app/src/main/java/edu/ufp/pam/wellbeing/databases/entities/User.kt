package edu.ufp.pam.wellbeing.databases.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user",
)
data class User(

    @PrimaryKey(autoGenerate = true)
    val id : Int,

    @ColumnInfo(name = "username")
    val username : String,

    @ColumnInfo(name = "email")
    val email : String,

    @ColumnInfo(name = "password")
    val password : String

)
