package com.example.mini_widget_n01.db.entity

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    private val _photoUiState = MutableLiveData<List<PhotoModel>>()

    val photoUiState: LiveData<List<PhotoModel>> get() = _photoUiState

    fun getAllImagesFromSdCard(contentResolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.IO){
            val allPhotoModels: MutableList<PhotoModel> = mutableListOf()
            val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )

            val orderBy: String = MediaStore.Images.Media.DATE_TAKEN
            val cursor = contentResolver.query(uri, projection, null, null, "$orderBy DESC")

            val columnIndexData = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()){
                val absolutePathOfImage = ContentUris.withAppendedId(uri, cursor.getLong(columnIndexData)).toString()
                allPhotoModels.add(PhotoModel(path = absolutePathOfImage))
            }

            cursor.close()

            withContext(Dispatchers.Main){
                _photoUiState.value = allPhotoModels
            }

        }
    }
}