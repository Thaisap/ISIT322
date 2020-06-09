package com.example.tudors.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


/**
 * Defines methods for using the TudorUser class with Room.
 */

@Dao
interface TudorUserDatabaseDao {

    /**
     * When adding a new row ( a new user )
     *
     * @param user new value to write
     */

    @Insert
    fun insert(user: TudorUser)

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param user updated value to write
     */

    @Update
    fun update(user: TudorUser)

    /**
     * Selects and returns the row that matches the supplied userId, which is our key.
     *
     * @param key userId (primary Key) to match
     */

    @Query("SELECT * from user_profile_table WHERE userId = :key")
    fun get(key: Long): TudorUser?

    /**
     * Selects and returns the row that matches the supplied userName and userPassword
     *
     * @param name userName to match
     * @param pw userPassword to match
     */

    @Query("SELECT * from user_profile_table WHERE user_name = :name AND user_password = :pw")
    fun login(name: String, pw: String): TudorUser?

    /**
     * Deletes the row from the table.
     */

    @Query("DELETE FROM user_profile_table WHERE userId = :key")
    fun delete(key: Long)

    /**
     * Selects and returns all rows in the table,
     *
     * that MATCH location and subject
     *
     * sorted by userId in descending order.
     */

    @Query("SELECT * FROM user_profile_table WHERE user_location = :loc AND user_subject = :subject AND is_student = :isStu ORDER BY userId DESC")
    fun match(loc: String, subject: String, isStu: Boolean): List<TudorUser>

}

