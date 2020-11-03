package com.poney.gpuimage.activity

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.poney.gpuimage.R
import com.poney.gpuimage.adapter.FilterAdapter
import com.poney.gpuimage.camera.Camera1Loader
import com.poney.gpuimage.camera.Camera2Loader
import com.poney.gpuimage.camera.CameraLoader
import com.poney.gpuimage.camera.doOnLayout
import com.poney.gpuimage.filter.base.*
import com.poney.gpuimage.filter.group.GPUImageFilterGroup
import com.poney.gpuimage.utils.FilterTypeHelper
import com.poney.gpuimage.utils.Rotation
import com.poney.gpuimage.view.GPUImageView

class CameraActivity : Activity(), View.OnClickListener {
    private val gpuImageView: GPUImageView by lazy { findViewById<GPUImageView>(R.id.gpu_image) }
    private val filterListView: RecyclerView by lazy { findViewById<RecyclerView>(R.id.filter_listView) }
    private val fragmentAdjustRadiogroup: RadioGroup by lazy { findViewById<RadioGroup>(R.id.fragment_adjust_radiogroup) }
    private val btnAdjust: ImageView by lazy { findViewById<ImageView>(R.id.btn_adjust) }
    private val btnFilter: ImageView by lazy { findViewById<ImageView>(R.id.btn_filter) }
    private val btnCamera: ImageButton by lazy { findViewById<ImageButton>(R.id.button_capture) }
    private val imgSwitchCamera: ImageView by lazy { findViewById<ImageView>(R.id.img_switch_camera) }
    private val filterAdjust: LinearLayout by lazy { findViewById<LinearLayout>(R.id.filter_adjust) }
    private val seekBar: SeekBar by lazy { findViewById<SeekBar>(R.id.seekBar) }
    private val cameraLoader: CameraLoader by lazy {
        if (Build.VERSION.SDK_INT < 21) {
            Camera1Loader(this)
        } else {
            Camera2Loader(this)
        }
    }
    private var filterAdjuster: FilterAdjuster? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        GPUImageParams.init(this)
        initAction()
        initFilterList()
        initCamera()
    }

    private fun initCamera() {
        cameraLoader.setOnPreviewFrameListener { data: ByteArray?, width: Int?, height: Int? ->
            gpuImageView.updatePreviewFrame(data, width!!, height!!)
        }
        gpuImageView.setRotation(getRotation(cameraLoader!!.getCameraOrientation()))
        gpuImageView.setRenderMode(GPUImageView.RENDERMODE_CONTINUOUSLY)
    }

    private fun getRotation(cameraOrientation: Int): Rotation {
        return when (cameraOrientation) {
            90 -> Rotation.ROTATION_90
            180 -> Rotation.ROTATION_180
            270 -> Rotation.ROTATION_270
            else -> Rotation.NORMAL
        }
    }

    private fun initFilterList() {
        filterListView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val filterAdapter = FilterAdapter(this, FilterTypeList.TYPES)
        filterAdapter.setOnFilterChangeListener { filterType -> switchFilterTo(FilterTypeHelper.createGroupFilterBy(filterType)) }
        filterListView.adapter = filterAdapter
    }

    private fun switchFilterTo(filter: GPUImageFilter) {
        fragmentAdjustRadiogroup.clearCheck()
        val originalFilter = gpuImageView.filter
        if (originalFilter == null || originalFilter.javaClass != filter.javaClass) {
            gpuImageView.filter = filter
        }
    }

    private fun initAction() {
        btnAdjust.setOnClickListener(this)
        btnFilter.setOnClickListener(this)
        fragmentAdjustRadiogroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            seekBar.progress = 0
            if (checkedId == -1) {
                seekBar.visibility = View.GONE
                filterAdjuster = null
                return@OnCheckedChangeListener
            }
            seekBar.visibility = View.VISIBLE
            val originalFilter = gpuImageView.filter
            var gpuImageFilterGroup: GPUImageFilterGroup? = null
            if (originalFilter is GPUImageFilterGroup) {
                gpuImageFilterGroup = originalFilter
            } else {
                gpuImageFilterGroup = GPUImageFilterGroup()
                gpuImageFilterGroup.addFilter(originalFilter)
            }
            var imageAdjustFilterBy: GPUImageFilter? = null
            //image adjust filter
            if (checkedId == R.id.fragment_radio_contrast) {
                imageAdjustFilterBy = FilterTypeHelper.createImageAdjustFilterBy(GPUImageFilterType.CONTRAST)
            } else if (checkedId == R.id.fragment_radio_saturation) {
                imageAdjustFilterBy = FilterTypeHelper.createImageAdjustFilterBy(GPUImageFilterType.SATURATION)
            } else if (checkedId == R.id.fragment_radio_exposure) {
                imageAdjustFilterBy = FilterTypeHelper.createImageAdjustFilterBy(GPUImageFilterType.EXPOSURE)
            } else if (checkedId == R.id.fragment_radio_sharpness) {
                imageAdjustFilterBy = FilterTypeHelper.createImageAdjustFilterBy(GPUImageFilterType.SHARPEN)
            } else if (checkedId == R.id.fragment_radio_bright) {
                imageAdjustFilterBy = FilterTypeHelper.createImageAdjustFilterBy(GPUImageFilterType.BRIGHTNESS)
            } else if (checkedId == R.id.fragment_radio_hue) {
                imageAdjustFilterBy = FilterTypeHelper.createImageAdjustFilterBy(GPUImageFilterType.HUE)
            }
            gpuImageFilterGroup.addFilter(imageAdjustFilterBy)
            if (filterAdjuster == null) filterAdjuster = FilterAdjuster(imageAdjustFilterBy)
            gpuImageView.filter = gpuImageFilterGroup
            gpuImageView.requestRender()
            seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    if (filterAdjuster!!.canAdjust()) {
                        Log.w(GalleryActivity::class.java.simpleName, "onProgressChanged")
                        filterAdjuster!!.adjust(progress)
                    }
                    gpuImageView.requestRender()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
        })

        btnCamera.setOnClickListener {
            saveSnapshot()
        }
        imgSwitchCamera.run {
            if (!cameraLoader.hasMultipleCamera()) {
                visibility = View.GONE
            }
            setOnClickListener {
                cameraLoader.switchCamera()
                gpuImageView.setRotation(getRotation(cameraLoader.getCameraOrientation()))
            }
        }
    }

    private fun saveSnapshot() {
        val folderName = "GPUImage"
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        gpuImageView.saveToPictures(folderName, fileName) {
            Toast.makeText(this, "$folderName/$fileName saved", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onClick(v: View) {
        filterListView.visibility = View.GONE
        filterAdjust.visibility = View.GONE
        if (v.id == R.id.btn_filter) {
            filterListView.visibility = View.VISIBLE
        } else if (v.id == R.id.btn_adjust) {
            filterAdjust.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        gpuImageView.doOnLayout {
            cameraLoader.onResume(it.width, it.height)
        }
    }

    override fun onPause() {
        cameraLoader.onPause()
        super.onPause()
    }
}