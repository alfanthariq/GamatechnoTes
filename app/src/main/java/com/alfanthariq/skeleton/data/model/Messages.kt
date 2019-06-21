package com.alfanthariq.skeleton.data.model

import androidx.room.*

@Entity(tableName = "messages")
data class Messages(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "user_id")
    var user_id: Int = 0,
    @ColumnInfo(name = "sender_id")
    var sender_id: Int = 0,
    @ColumnInfo(name = "conversation_id")
    var conversation_id: Int = 0,
    @ColumnInfo(name = "message")
    var message: String = "",
    @ColumnInfo(name = "message_time")
    var message_time: String = ""
)
{
    @Ignore
    constructor() : this(0)
}

@Dao
interface MessageDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: Messages)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLists(lists: List<Messages>)

    @Query("SELECT * FROM messages ORDER BY message_time DESC")
    fun all(): List<Messages>

    @Query("SELECT * FROM messages WHERE id = :id ORDER BY message_time DESC")
    fun one(id : Int): List<Messages>

    @Query("SELECT * FROM messages WHERE (sender_id = :user_id OR sender_id = :sender_id) ORDER BY message_time DESC")
    fun lastByUser(sender_id : Int, user_id : Int): Messages?

    @Query("SELECT * FROM messages WHERE conversation_id = :c_id ORDER BY message_time DESC")
    fun lastByConversation(c_id : Int): Messages?

    @Query("SELECT * FROM messages WHERE (sender_id = :user_id AND sender_id = :sender_id) OR (sender_id = :sender_id AND user_id = :sender_id) ORDER BY message_time DESC")
    fun ByUser(sender_id : Int, user_id : Int): List<Messages>

    @Query("SELECT * FROM messages WHERE conversation_id = :id ORDER BY message_time DESC")
    fun ByConversation(id : Int): List<Messages>

    @Query("DELETE FROM messages")
    fun deleteAll()

    @Query("SELECT * FROM messages WHERE (message LIKE :keyword) ORDER BY message_time ASC")
    fun search(keyword : String): List<Messages>
}