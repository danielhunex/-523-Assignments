package edu.uw.fallalarm.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.uw.fallalarm.database.*
import edu.uw.fallalarm.ui.emergencycontact.EmergencyContactViewModel
import java.util.*

class HomeViewModel : AndroidViewModel {
    private val TAG = HomeViewModel::class.java.simpleName
    private var _histories: LiveData<List<HistoryEntity>>? = null
    private var _historyRepository: HistoryRepository
    private var _text: LiveData<String>? = null

    constructor(application: Application) : super(application) {

        val database = AppDatabase.getInstance(application!!.applicationContext)
        _historyRepository = HistoryRepository(database!!)

    }

    fun getHistories(): LiveData<List<HistoryEntity>>? {
        _histories = _historyRepository?.getHistories()
        return _histories
    }

    fun getText(): LiveData<String>? {
        return _text
    }

}