package edu.uw.fallalarm.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "history")
class HistoryEntity {
    @PrimaryKey(autoGenerate = true)
    private var id = 0

    private var message: String? = null

    @ColumnInfo(name = "created_date")
    private var createdDate: Date? = null

    @Ignore
    constructor(message: String, createdDate: Date?) {
        this.message = message
        this.createdDate = createdDate
    }

    constructor(id: Int, message: String) {
        this.id = id
        this.message = message
    }

    fun getMessage(): String? {
        return this.message
    }

    fun getId(): Int {
        return this.id
    }

    fun getCreatedDate(): Date? {
        return this.createdDate
    }

    fun setCreatedDate(date: Date?) {
        this.createdDate = date
    }

}

