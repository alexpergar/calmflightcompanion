package com.example.calmflightcompanion.ui.relaxation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RelaxationViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is relaxation Fragment"
    }
    val text: LiveData<String> = _text
}