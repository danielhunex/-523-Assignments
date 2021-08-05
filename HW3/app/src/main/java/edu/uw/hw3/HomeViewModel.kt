package edu.uw.hw3

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private var _firstBitmap = MutableLiveData<Bitmap>()
    private var _originalFirstBitmap = MutableLiveData<Bitmap>()

    private var _secondBitmap = MutableLiveData<Bitmap>()
    private var _originalSecondBitmap = MutableLiveData<Bitmap>()

    var firstBitmap: LiveData<Bitmap> = _firstBitmap
    var originalFirstBitmap: LiveData<Bitmap> = _originalFirstBitmap
    var secondBitmap: LiveData<Bitmap> = _secondBitmap
    var originalSecondBitmap: LiveData<Bitmap> = _originalSecondBitmap

    fun setFirstBitmap(bitmap: Bitmap) {
        _firstBitmap.value = bitmap
    }

    fun setSecondBitmap(bitmap: Bitmap) {
        _secondBitmap.value = bitmap
    }

    fun setOriginalFirstBitmap(bitmap: Bitmap) {
        _originalFirstBitmap.value = bitmap
    }

    fun setOriginalSecondBitmap(bitmap: Bitmap) {
        _originalSecondBitmap.value = bitmap
    }
}