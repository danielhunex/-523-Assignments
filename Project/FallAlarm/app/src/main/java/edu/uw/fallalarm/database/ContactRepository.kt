package edu.uw.fallalarm.database

import androidx.lifecycle.LiveData

class ContactRepository(var database: AppDatabase) {
    private var contact: LiveData<ContactEntity>? = null

    fun getEmergencyContact():ContactEntity?{
        return database.contactDao?.getEmergencyContact()
    }
    fun getContact(): LiveData<ContactEntity>? {
        contact = database.contactDao?.getContact()
        return contact
    }

    fun getContactById(contactId: Int): LiveData<ContactEntity>? {
        return database.contactDao?.getContactById(contactId)
    }

    fun insertContact(contact: ContactEntity) {
        database.contactDao?.insertContact(contact)
    }

    fun updateContact(contact: ContactEntity) {
        database.contactDao?.updateContact(contact)
    }

    companion object {
        private val LOG_TAG = ContactRepository::class.java
            .simpleName
    }
}