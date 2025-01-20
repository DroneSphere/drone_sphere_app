package io.dronesphere.aircraft

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import dji.v5.manager.interfaces.ICameraStreamManager
import io.dronesphere.aircraft.models.CameraStreamDetailVM
import io.dronesphere.aircraft.models.CameraStreamListVM

class CameraStreamActivity : ComponentActivity() {
    private lateinit var listViewModel: CameraStreamListVM
    private lateinit var viewModel: CameraStreamDetailVM

    private lateinit var cameraSurfaceView: SurfaceView
    private var surface: Surface? = null
    private var width = -1
    private var height = -1
    private var scaleType = ICameraStreamManager.ScaleType.CENTER_INSIDE

    fun newIntent(context: Context) = Intent(context, CameraStreamActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_stream)

        listViewModel = ViewModelProvider(this)[CameraStreamListVM::class.java]
        viewModel = ViewModelProvider(this)[CameraStreamDetailVM::class.java]

        // 打印日志
        Log.i("CameraStreamActivity", "onCreate")
        Log.i(
            "CameraStreamActivity",
            "listViewModel: ${listViewModel.availableCameraListData.value}"
        )

        cameraSurfaceView = findViewById(R.id.sv_camera)
        cameraSurfaceView.holder.addCallback(cameraSurfaceCallback)

        // 监听可用摄像头列表
        listViewModel.availableCameraListData.observe(this) {
            Log.i("CameraStreamActivity", "availableCameraListData: $it")
            if (it.isEmpty()) {
                return@observe
            }
            Log.i("CameraStreamActivity", "cameraIndex: ${it.first()}")
            viewModel.setCameraIndex(it.first())

            viewModel.availableLensListData.observe(this) {
                Log.i("CameraStreamActivity", "availableLensListData: $it")
                if (it.isEmpty()) {
                    return@observe
                }
                Log.i("CameraStreamActivity", "currentLensData: ${it.first()}")
            }

            viewModel.currentLensData.observe(this) {
                Log.i("CameraStreamActivity", "currentLensData: $it")
                updateCameraStream()
            }
        }
    }

    private fun updateCameraStream() {
        if (width <= 0 || height <= 0 || surface == null) {
            if (surface != null) {
                viewModel.removeCameraStreamSurface(surface!!)
            }
            return
        }

        viewModel.putCameraStreamSurface(
            surface!!,
            width,
            height,
            scaleType
        )
    }

    private val cameraSurfaceCallback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            surface = holder.surface
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            this@CameraStreamActivity.width = width
            this@CameraStreamActivity.height = height

            updateCameraStream()
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            width = 0
            height = 0
            updateCameraStream()
        }
    }
}