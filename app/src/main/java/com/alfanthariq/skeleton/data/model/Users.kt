package com.alfanthariq.skeleton.data.model

import androidx.room.*

@Entity(tableName = "users")
data class Users(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    var user_id: Int,
    @ColumnInfo(name = "user_key")
    var user_key: String = "",
    @ColumnInfo(name = "user_name")
    var user_name: String = "",
    @ColumnInfo(name = "user_username")
    var user_username: String = "",
    @ColumnInfo(name = "user_email")
    var user_email: String = "",
    @ColumnInfo(name = "user_photo")
    var user_photo: String = "",
    @ColumnInfo(name = "user_location")
    var user_location: String = "",
    @ColumnInfo(name = "user_flag")
    var user_flag: String = "",
    @ColumnInfo(name = "user_distance")
    var user_distance: String = "",
    @ColumnInfo(name = "user_online")
    var user_online: Int = 0,
    @ColumnInfo(name = "last_message_date")
    var last_message_date: String = ""
)
{
    @Ignore
    constructor() : this(0)
}

@Dao
interface UsersDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: Users)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLists(lists: List<Users>)

    @Query("SELECT * FROM users WHERE user_id IN (SELECT sender_id FROM messages) ORDER BY user_name ASC")
    fun all(): List<Users>

    @Query("SELECT * FROM users ORDER BY user_name ASC")
    fun allUserOnly(): List<Users>

    @Query("SELECT * FROM users WHERE user_id = :id ORDER BY user_name ASC")
    fun one(id : Int): List<Users>

    @Query("UPDATE users SET last_message_date = :lmd WHERE user_id = :id")
    fun updateLastMsgDate(lmd : String, id : Int)

    @Query("DELETE FROM users")
    fun deleteAll()

    @Query("SELECT * FROM users WHERE user_id IN (SELECT sender_id FROM messages) AND (user_name LIKE :keyword) ORDER BY user_name ASC")
    fun search(keyword : String): List<Users>

    @Query("SELECT * FROM users WHERE (user_name LIKE :keyword) ORDER BY user_name ASC")
    fun searchUserOnly(keyword : String): List<Users>
}