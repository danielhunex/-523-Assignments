package edu.uw.fallalarm.database

import android.text.Editable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "contact")
class ContactEntity {

    @PrimaryKey(autoGenerate = true)
    private var id = 0

    private var name: String? = null

    @ColumnInfo(name = "phone_number")
    private var phoneNumber: String? = null

    @ColumnInfo(name = "created_date")
    private var createdDate: Date? = null

    @ColumnInfo(name = "updated_date")
    private var updatedDate: Date? = null

    @Ignore
    constructor(
        name: String,
        phoneNumber: String
    ) {
        this.name = name
        this.phoneNumber = phoneNumber
        this.createdDate = Date()
        this.updatedDate = Date()
    }

    constructor(
        id: Int,
        name: String,
        phoneNumber: String,
        createdDate: Date,
        updatedDate: Date,
    ) {
        this.id = id
        this.name = name
        this.phoneNumber = phoneNumber
        this.createdDate = createdDate
        this.updatedDate = updatedDate
    }

    fun getId(): Int {
        return id
    }

    fun getName(): String? {
        return name
    }

    fun getPhoneNumber(): String? {
        return phoneNumber
    }

    fun getCreatedDate(): Date? {
        return createdDate
    }

    fun getUpdatedDate(): Date? {
        return updatedDate
    }

}