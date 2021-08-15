package edu.uw.fallalarm.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER by created_date DESC LIMIT 5")
    fun getHistories(): LiveData<List<HistoryEntity>>

    @Insert
    fun inserHistory(history: HistoryEntity)
}