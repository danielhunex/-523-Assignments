package edu.uw.fallalarm.database

import androidx.lifecycle.LiveData
class HistoryRepository(var database: AppDatabase) {

    private var histories: LiveData<List<HistoryEntity>>? = null

    fun getHistories(): LiveData<List<HistoryEntity>>? {
        histories = database.historyDao?.getHistories()
        return histories
    }

    fun insertContact(history: HistoryEntity) {

        database.historyDao?.inserHistory(history)
    }
}