package com.example.tudors.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user_profile_table")
data class TudorUser(
    @PrimaryKey(autoGenerate = true)
    var userId: Long = 0L,

    @ColumnInfo(name = "user_name")
    var userName: String = "a",

    @ColumnInfo(name = "user_password")
    var userPassword: String = "b",

    @ColumnInfo(name = "user_location")
    var userLocation: String = "c",

    @ColumnInfo(name = "user_phone")
    var userPhone: String = "d",

    @ColumnInfo(name = "user_email")
    var userEmail: String = "e",

    @ColumnInfo(name = "is_student")
    var isStudent: Boolean = true,

    @ColumnInfo(name = "user_subject")
    var userSubject: String = "f"

)
