package com.alfanthariq.skeleton.data.model

import androidx.room.*

@Entity(tableName = "conversation")
data class Conversation(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "user_id")
    var user_id: Int = 0,
    @ColumnInfo(name = "sender_id")
    var sender_id: Int = 0
)
{
    @Ignore
    constructor() : this(0)
}

@Dao
interface ConversationDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: Conversation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLists(lists: List<Conversation>)

    @Query("SELECT * FROM conversation")
    fun all(): List<Conversation>

    @Query("SELECT * FROM conversation WHERE id = :id")
    fun one(id : Int): List<Conversation>

    @Query("DELETE FROM conversation")
    fun deleteAll()
}