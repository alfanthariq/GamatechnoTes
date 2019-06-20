package com.alfanthariq.skeleton.data.model

import androidx.room.*

@Entity(tableName = "log")
data class LogKegiatan(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        var id: Int,
        @ColumnInfo(name = "tgl_log")
        var tgl_log: String? = null,
        @ColumnInfo(name = "jam_log")
        var jam_log: String? = null,
        @ColumnInfo(name = "type_log")
        var type_log: Int? = null,
        @ColumnInfo(name = "text_log")
        var text_log: String? = null,
        @ColumnInfo(name = "is_read")
        var is_read: Int = 0
)
{
    @Ignore
    constructor() : this(0)
}

@Dao
interface LogDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(log: LogKegiatan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLists(lists: List<LogKegiatan>)

    @Query("SELECT * FROM log ORDER BY id DESC")
    fun all(): List<LogKegiatan>

    @Query("SELECT * FROM log WHERE is_read = 0 ORDER BY tgl_log, jam_log DESC")
    fun unread(): List<LogKegiatan>

    @Query("SELECT * FROM log WHERE tgl_log = :tgl AND jam_log = :jam ORDER BY tgl_log ASC")
    fun one(tgl : String, jam : String): List<LogKegiatan>

    @Query("UPDATE log SET is_read = 1 WHERE is_read = 0")
    fun setRead()

    @Query("DELETE FROM log")
    fun deleteAll()

    @Query("SELECT * FROM log WHERE (text_log LIKE :keyword) ORDER BY tgl_log ASC")
    fun search(keyword : String): List<LogKegiatan>
}