package edu.uw.falldetectalarm.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Primary Emergency Contact"
    }
    val text: LiveData<String> = _text
}