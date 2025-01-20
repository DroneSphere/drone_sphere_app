package io.dronesphere.aircraft.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dji.sdk.keyvalue.value.common.ComponentIndexType
import dji.v5.manager.datacenter.MediaDataCenter
import dji.v5.manager.interfaces.ICameraStreamManager.AvailableCameraUpdatedListener

class CameraStreamListVM : ViewModel(), AvailableCameraUpdatedListener {

    private val _availableCameraListData = MutableLiveData<List<ComponentIndexType>>(ArrayList())

    init {
        MediaDataCenter.getInstance().cameraStreamManager.addAvailableCameraUpdatedListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        MediaDataCenter.getInstance().cameraStreamManager.removeAvailableCameraUpdatedListener(this)
    }

    val availableCameraListData: LiveData<List<ComponentIndexType>>
        get() = _availableCameraListData

    override fun onAvailableCameraUpdated(availableCameraList: List<ComponentIndexType>) {
        Log.i("CameraStreamListVM", "onAvailableCameraUpdated: $availableCameraList")
        _availableCameraListData.postValue(availableCameraList)
    }
}