package edu.uw.hw3

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditViewModel :ViewModel() {

    private var _firstBitmap = MutableLiveData<Bitmap>()
    private var _originalFirstBitmap = MutableLiveData<Bitmap>()



    var firstBitmap: LiveData<Bitmap> = _firstBitmap
    var originalFirstBitmap: LiveData<Bitmap> = _originalFirstBitmap


    fun setFirstBitmap(bitmap: Bitmap) {
        _firstBitmap.value = bitmap
    }

    fun setOriginalFirstBitmap(bitmap: Bitmap) {
        _originalFirstBitmap.value = bitmap
    }
}