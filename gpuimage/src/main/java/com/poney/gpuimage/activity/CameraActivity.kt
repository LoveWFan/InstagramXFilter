package com.poney.gpuimage.activity

import android.app.Activity
import android.os.Build
import android.os.Bundle
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
import com.poney.gpuimage.filter.base.FilterTypeList
import com.poney.gpuimage.filter.base.GPUImageFilter
import com.poney.gpuimage.filter.base.GPUImageParams
import com.poney.gpuimage.filter.group.GPUImageAdjustFilterGroup
import com.poney.gpuimage.filter.group.GPUImageFilterGroup
import com.poney.gpuimage.utils.FilterTypeHelper
import com.poney.gpuimage.utils.Rotation
import com.poney.gpuimage.view.GPUImageView
import java.util.*

class CameraActivity : Activity(), View.OnClickListener {
    private lateinit var gpuImageAdjustFilterGroup: GPUImageAdjustFilterGroup;
    private var mCheckedId: Int = 0
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
        gpuImageView.filter = GPUImageFilterGroup(getFilterList(GPUImageFilter(), GPUImageAdjustFilterGroup()))
        gpuImageView.setRotation(getRotation(cameraLoader.getCameraOrientation()))
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
        if (originalFilter is GPUImageFilterGroup) {
            val gpuImageFilter = originalFilter.filters[0]
            if (gpuImageFilter == null || gpuImageFilter.javaClass != filter.javaClass) {
                gpuImageView.filter = GPUImageFilterGroup(getFilterList(filter, GPUImageAdjustFilterGroup()))
            }
        }
    }

    private fun initAction() {
        btnAdjust.setOnClickListener(this)
        btnFilter.setOnClickListener(this)
        fragmentAdjustRadiogroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            if (checkedId == -1) {
                seekBar.visibility = View.GONE
                return@OnCheckedChangeListener
            }

            seekBar.visibility = View.VISIBLE

            val originalFilter = gpuImageView.filter
            if (originalFilter is GPUImageFilterGroup) {
                val filters = originalFilter.filters
                gpuImageAdjustFilterGroup = filters[1] as GPUImageAdjustFilterGroup
                mCheckedId = checkedId
                if (checkedId == R.id.fragment_radio_contrast) {
                    val contrastProgress: Int = gpuImageAdjustFilterGroup.getContrastProgress()
                    seekBar.progress = contrastProgress
                    gpuImageAdjustFilterGroup.setContrast(contrastProgress)
                } else if (checkedId == R.id.fragment_radio_saturation) {
                    val saturationProgress: Int = gpuImageAdjustFilterGroup.getSaturationProgress()
                    seekBar.progress = saturationProgress
                    gpuImageAdjustFilterGroup.setSaturation(saturationProgress)
                } else if (checkedId == R.id.fragment_radio_exposure) {
                    val exposureProgress: Int = gpuImageAdjustFilterGroup.getExposureProgress()
                    seekBar.progress = exposureProgress
                    gpuImageAdjustFilterGroup.setExposure(exposureProgress)
                } else if (checkedId == R.id.fragment_radio_sharpness) {
                    val sharpnessProgress: Int = gpuImageAdjustFilterGroup.getSharpnessProgress()
                    seekBar.progress = sharpnessProgress
                    gpuImageAdjustFilterGroup.setSharpness(sharpnessProgress)
                } else if (checkedId == R.id.fragment_radio_bright) {
                    val brightnessProgress: Int = gpuImageAdjustFilterGroup.getBrightnessProgress()
                    seekBar.progress = brightnessProgress
                    gpuImageAdjustFilterGroup.setBrightness(brightnessProgress)
                } else if (checkedId == R.id.fragment_radio_hue) {
                    val hueProgress: Int = gpuImageAdjustFilterGroup.getHueProgress()
                    seekBar.progress = hueProgress
                    gpuImageAdjustFilterGroup.setHue(hueProgress)
                }
            }

        })

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (gpuImageAdjustFilterGroup == null) return
                if (mCheckedId == R.id.fragment_radio_contrast) {
                    gpuImageAdjustFilterGroup.setContrast(progress)
                } else if (mCheckedId == R.id.fragment_radio_saturation) {
                    gpuImageAdjustFilterGroup.setSaturation(progress)
                } else if (mCheckedId == R.id.fragment_radio_exposure) {
                    gpuImageAdjustFilterGroup.setExposure(progress)
                } else if (mCheckedId == R.id.fragment_radio_sharpness) {
                    gpuImageAdjustFilterGroup.setSharpness(progress)
                } else if (mCheckedId == R.id.fragment_radio_bright) {
                    gpuImageAdjustFilterGroup.setBrightness(progress)
                } else if (mCheckedId == R.id.fragment_radio_hue) {
                    gpuImageAdjustFilterGroup.setHue(progress)
                }
                gpuImageView.requestRender()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
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

    private fun getFilterList(originalFilter: GPUImageFilter?, gpuImageAdjustFilterGroup: GPUImageAdjustFilterGroup): MutableList<GPUImageFilter>? {
        val groupFilters: MutableList<GPUImageFilter> = ArrayList(2)
        groupFilters.add(originalFilter!!)
        groupFilters.add(gpuImageAdjustFilterGroup)
        return groupFilters
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