package edu.uw.fallalarm.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ContactDao {

    @Query("SELECT * FROM contact ORDER by id LIMIT 1")
    fun getContact(): LiveData<ContactEntity>

    @Query("SELECT * FROM contact ORDER by id LIMIT 1")
    fun getEmergencyContact(): ContactEntity

    @Query("SELECT * FROM contact WHERE id=:id")
    fun getContactById(id: Int): LiveData<ContactEntity>

    @Insert
    fun insertContact( contactEntity: ContactEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateContact(contactEntity: ContactEntity)
}