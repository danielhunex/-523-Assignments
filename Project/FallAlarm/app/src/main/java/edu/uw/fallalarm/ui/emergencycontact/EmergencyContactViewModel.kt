package edu.uw.fallalarm.ui.emergencycontact

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import edu.uw.fallalarm.database.AppDatabase
import edu.uw.fallalarm.database.ContactEntity
import edu.uw.fallalarm.database.ContactRepository
import java.util.*


class EmergencyContactViewModel : AndroidViewModel {

    private val TAG = EmergencyContactViewModel::class.java.simpleName
    private var _contact: LiveData<ContactEntity>? = null
    private lateinit var _contactRepository: ContactRepository

    public constructor(application: Application) : super(application) {

        val database = AppDatabase.getInstance(application!!.applicationContext)
        _contactRepository = ContactRepository(database!!)
        _contact = _contactRepository?.getContact()
    }

    fun getContact(): LiveData<ContactEntity>? {
        return _contact
    }

    fun insertContact(name: String, phoneNumber: String) {

        if (_contact == null || _contact!!.value == null) {
            _contactRepository.insertContact(ContactEntity(name, phoneNumber))
        } else {
            var contact = _contact!!.value
            _contactRepository.updateContact(ContactEntity(contact!!.getId(), name, phoneNumber,
                contact!!.getCreatedDate()!!, Date()))
        }
    }
}