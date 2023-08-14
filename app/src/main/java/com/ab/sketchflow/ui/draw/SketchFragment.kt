package com.ab.sketchflow.ui.draw


import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.ab.sketchflow.MainActivity
import com.ab.sketchflow.R
import com.ab.sketchflow.SketchFlowApplication
import com.ab.sketchflow.databinding.FragmentDrawingBinding
import com.ab.sketchflow.model.DraftEntity
import com.ab.sketchflow.model.colorSets
import com.ab.sketchflow.util.AdapterUtils
import com.ab.sketchflow.util.Constants
import com.ab.sketchflow.util.Utils
import com.ab.sketchflow.util.Utils.backgroundTintValue
import com.ab.sketchflow.util.Utils.bitmapToByteArray
import com.ab.sketchflow.util.Utils.showVisibility
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.raed.rasmview.RasmContext
import com.raed.rasmview.brushtool.data.Brush
import com.raed.rasmview.brushtool.data.BrushesRepository
import com.raed.rasmview.brushtool.model.BrushConfig
import com.raed.rasmview.brushtool.model.BrushStamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rx.android.schedulers.AndroidSchedulers
import java.io.File
import java.io.FileOutputStream


class DrawingFragment : Fragment(),AdapterUtils.ColorSetDelegate {

    private var _binding: FragmentDrawingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var  rasmContext : RasmContext
    private lateinit var previousColour : ImageView
    lateinit var currentBrushView : ImageView
    private val REQUEST_SAVE_DRAFT = 100
    lateinit var viewModel: SketchViewModel
    private lateinit var draftEntity: DraftEntity
    private var fillColor : Boolean = false

    private val brushesRepository by lazy {
        BrushesRepository(resources)
    }


    private val brushes by lazy {
        listOf(binding.brushLayout.waterColorBrush,binding.brushLayout.airBrush,binding.brushLayout.pencil,
            binding.brushLayout.markerPen,binding.brushLayout.fountainPen,binding.brushLayout.calligraphyPen,binding.brushLayout.crayon)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SketchViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDrawingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentBrushView = binding.brushLayout.fountainPen
        rasmContext = binding.rasmView.rasmContext
        bind()
        showCurrentBrushOnly(true)
        setupColorSets()
        handleClickEvents()
        setDefaultBrush()
        getDraftDataIfExist()
    }

    private fun getDraftDataIfExist() {
        val draftId = arguments?.getLong(Constants.DRAFT_DATA_KEY)
        if(draftId != null && draftId != 0L){
            lifecycleScope.launch (Dispatchers.IO){
                viewModel.getDraftEntityData(draftId){
                    launch (Dispatchers.Main){
                        draftEntity = it
                        rasmContext.setRasm(Utils.byteArrayToBitmap(draftEntity.bitmapData))
                    }
                }
            }
        }
    }

    private fun setDefaultBrush() {
        rasmContext.brushColor =  R.color.black
        setBrushConfigAndUpdatePercent(brushesRepository.get(Brush.Pen),currentBrushView,false)
    }

    private fun bind() {
        SketchFlowApplication.getSelectedColorCode()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if(fillColor) {
                    rasmContext.setBackgroundColor(it)
                } else {
                    binding.colorLayout.customColor.backgroundTintList = it.backgroundTintValue()
                    selectedColor(it,binding.colorLayout.customColor)
                }
                fillColor = false
            }
    }

    private fun setupColorSets(){
        binding.colorLayout.colorSetsRecyclerView.adapter = AdapterUtils.setUpColorSetAdapter(colorSets,this)
    }

    private fun showCurrentBrushOnly(show : Boolean,showAll : Boolean = false){
        if(showAll){
            brushes.showVisibility(show)
            return
        }
        currentBrushView.showVisibility(show)
        highlightSelectedBrush()
        (brushes - currentBrushView).showVisibility(!show)
    }

    private fun highlightSelectedBrush() {
        currentBrushView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_IN)
    }

    override fun onResume() {
        (requireActivity() as MainActivity).showBottomNavigation(false)
        (requireActivity() as MainActivity).showToolBar(false)
        super.onResume()
    }

    private fun handleClickEvents(){
        toggleEraserMode()
        undoAndRedo()
        clearCanvas()
        handleSaveButtonClicks()
        handleSendButtonClicks()
        handleBrushClickEvents()
        handleBrushSelectionIconClick()
        handleCanvasFillClick()
    }

    private fun handleCanvasFillClick() {
        binding.paintIcon.setOnClickListener {
            fillColor = true
            ColorPickerDialog.newBuilder()
                .show(requireActivity())
        }
    }

    private fun handleBrushSelectionIconClick(){
        binding.brushLayout.moveOrForwardBrush.setOnClickListener {
            if(binding.brushLayout.moveOrForwardBrush.contentDescription == getString(R.string.forward)){
            showBrushSelectionLayout()
            } else {
                showOnlyCurrentBrushFullLayout()
            }
        }
    }

    private fun showBrushSelectionLayout() {
        showCurrentBrushOnly(show = true, showAll = true)
        binding.brushLayout.moveOrForwardBrush.background = resources.getDrawable(R.drawable.ic_arrow_back)
        binding.brushLayout.moveOrForwardBrush.contentDescription = getString(R.string.back)
        binding.colorLayout.customColorLayout.showVisibility(false)
    }

    private fun showOnlyCurrentBrushFullLayout() {
        showCurrentBrushOnly(show = true, showAll = false)
        binding.brushLayout.moveOrForwardBrush.background = resources.getDrawable(R.drawable.ic_arrow_forward)
        binding.brushLayout.moveOrForwardBrush.contentDescription = getString(R.string.forward)
        binding.colorLayout.customColorLayout.showVisibility(true)
        binding.brushSettingLayout.showVisibility(false)
        selectedColor(rasmContext.brushColor,previousColour)
    }

    private fun handleBrushClickEvents(){

        binding.brushLayout.pencil.setOnClickListener {
            setBrushConfigAndUpdatePercent(brushesRepository.get(Brush.Pencil),binding.brushLayout.pencil)
        }

        binding.brushLayout.fountainPen.setOnClickListener {
            setBrushConfigAndUpdatePercent(brushesRepository.get(Brush.Pen),binding.brushLayout.fountainPen)
        }

        binding.brushLayout.calligraphyPen.setOnClickListener {
            setBrushConfigAndUpdatePercent(brushesRepository.get(Brush.Calligraphy),binding.brushLayout.calligraphyPen)
        }

        binding.brushLayout.airBrush.setOnClickListener {
            setBrushConfigAndUpdatePercent(brushesRepository.get(Brush.AirBrush),binding.brushLayout.airBrush)
        }

        binding.brushLayout.markerPen.setOnClickListener {
            setBrushConfigAndUpdatePercent(brushesRepository.get(Brush.Marker),binding.brushLayout.markerPen)
        }
        binding.brushLayout.crayon.setOnClickListener {
            rasmContext.brushConfig = BrushConfig()
            rasmContext.brushConfig.flow = 0.5F
            rasmContext.brushConfig.opacity = 0.3F
            setBrushConfigAndUpdatePercent(rasmContext.brushConfig,binding.brushLayout.crayon)
        }


        binding.brushLayout.waterColorBrush.setOnClickListener {
            rasmContext.brushConfig = BrushConfig()
            rasmContext.brushConfig.flow = 0.3F
            rasmContext.brushConfig.opacity = 0.3F
            rasmContext.brushConfig.size = 1F
            setBrushConfigAndUpdatePercent(rasmContext.brushConfig,binding.brushLayout.waterColorBrush)

        }

        binding.colorLayout.customColor.setOnClickListener {
                ColorPickerDialog.newBuilder()
                    .setColor(rasmContext.brushColor)
                    .show(requireActivity())
        }

        binding.expandView.setOnClickListener {
            val viewList = listOf(binding.colorLayout.customColorLayout,binding.brushLayout.brushLayout,binding.bottomLayout,binding.brushSettingLayout)
            if(binding.expandView.contentDescription == getString(R.string.show_all)){
                viewList.forEach { it.visibility = View.INVISIBLE }
                binding.expandView.contentDescription = getString(R.string.hide_all)
                binding.expandView.setImageResource(R.drawable.ic_minimize)
                binding.expandView.setImageResource(R.drawable.ic_minimize)
            } else {
                viewList.showVisibility(true)
                binding.expandView.contentDescription = getString(R.string.show_all)
                binding.expandView.setImageResource(R.drawable.ic_expand)
            }
        }

        binding.sizePlus.setOnClickListener {
            val progress = binding.sizeSeekBar.progress
            binding.sizeSeekBar.progress += progress+1
        }

        binding.sizeMinus.setOnClickListener {
            val progress = binding.sizeSeekBar.progress
            binding.sizeSeekBar.progress += progress-1
        }

        binding.opacityPlus.setOnClickListener {
            val progress = binding.opacitySeekBar.progress
            binding.opacitySeekBar.progress += progress-1
            updateSizePercentTextView(binding.opacitySeekBar.progress)
        }

        binding.opacityMinus.setOnClickListener {
            val progress = binding.opacitySeekBar.progress
            binding.opacitySeekBar.progress += progress-1
            updateOpacityPercentTextView(binding.opacitySeekBar.progress)
        }

        binding.sizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateSizePercentTextView(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.opacitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateOpacityPercentTextView(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun updateSizePercentTextView(percent : Int){
        binding.sizePercent.text = percent.toString()
        rasmContext.brushConfig.size = (percent.toFloat() / 100F)
    }

    private fun updateOpacityPercentTextView(percent : Int){
        binding.opacityPercent.text = percent.toString()
        rasmContext.brushConfig.opacity = (percent.toFloat() / 100F)
    }

    private fun setBrushConfigAndUpdatePercent(brushConfig: BrushConfig,brushView : ImageView,openSettingLayout : Boolean = true) {
        if(binding.brushSettingLayout.isVisible && currentBrushView == brushView){
            showOnlyCurrentBrushFullLayout()
        } else {
            rasmContext.brushConfig = brushConfig
            binding.sizeSeekBar.progress = (brushConfig.size * 100).toInt()
            binding.opacitySeekBar.progress = (brushConfig.size * 100).toInt()
            binding.sizePercent.text = binding.sizeSeekBar.progress.toString()
            binding.opacityPercent.text = binding.opacitySeekBar.progress.toString()
            currentBrushView.colorFilter = null
            currentBrushView = brushView
            highlightSelectedBrush()
            if(openSettingLayout && binding.brushLayout.moveOrForwardBrush.contentDescription == getString(R.string.forward)){
                showBrushSelectionLayout()
            }
            binding.brushSettingLayout.showVisibility(openSettingLayout)
        }
    }

    private fun toggleEraserMode(){
        binding.eraseIcon.setOnClickListener {
            rasmContext.brushConfig.isEraser = true
            rasmContext.brushConfig.size = 0.8F
        }
    }

    private fun undoAndRedo(){
        binding.undoIcon.setOnClickListener {
            if(rasmContext.state.canCallUndo()){
                rasmContext.state.undo()
            }
        }

        binding.redoIcon.setOnClickListener {
            if(rasmContext.state.canCallRedo()){
                rasmContext.state.redo()
            }
        }
    }

    private fun clearCanvas(){
        binding.clearIcon.setOnClickListener {
            rasmContext.clear()
        }
    }

    private fun handleSaveButtonClicks(){
        binding.saveIcon.setOnClickListener {
            lifecycleScope.launch (Dispatchers.IO){
                if(this@DrawingFragment::draftEntity.isInitialized){
                    draftEntity.bitmapData = bitmapToByteArray(rasmContext.exportRasm())
                    viewModel.updateDraft(draftEntity){
                        launch ( Dispatchers.Main ){
                            Toast.makeText(requireContext(),getString(R.string.drafts_updated),Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                viewModel.insertDraft(bitmapToByteArray(rasmContext.exportRasm())){
                    launch (Dispatchers.Main){
                        Toast.makeText(requireContext(),getString(R.string.drafts_saved),Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun saveCanvasAsDraft() {
        val canvasBitmap = rasmContext.exportRasm()
        val canvas = Canvas(canvasBitmap!!)
        binding.rasmView.draw(canvas)

        val fileName = "sketchflow_draft_${System.currentTimeMillis()}.png"

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_TITLE, fileName)

        startActivityForResult(intent, REQUEST_SAVE_DRAFT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SAVE_DRAFT && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                saveBitmapToUri(rasmContext.exportRasm(), uri)
                Toast.makeText(requireContext(), "Image saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveBitmapToUri(bitmap: Bitmap?, uri: Uri) {
        try {
            val outputStream = requireActivity().contentResolver.openOutputStream(uri)
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Image save failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSendButtonClicks(){
        binding.sendIcon.setOnClickListener {
            showSaveAlertDialog()
        }
    }

    private fun showSaveAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(getString(R.string.app_name))
        alertDialogBuilder.setMessage(getString(R.string.save_alert))
        alertDialogBuilder.setPositiveButton(getString(R.string.alert_positive)) { dialog, which ->
            saveCanvasAsDraft()
        }
        alertDialogBuilder.setNegativeButton(R.string.alert_negative) { dialog, which ->
            exportCanvasAsImage()
        }
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

        private fun exportCanvasAsImage() {
        val canvasBitmap = Bitmap.createBitmap(binding.rasmView.width, binding.rasmView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(canvasBitmap!!)
        binding.rasmView.draw(canvas)

        val fileName = "sketch_flow_canvas${System.currentTimeMillis()}.png"

        val file = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)
        try {
            val outputStream = FileOutputStream(file)
            canvasBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            shareImage(file)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Export failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareImage(imageFile: File) {
        val uri = FileProvider.getUriForFile(requireContext(), "${Constants.PACKAGE_NAME}.provider", imageFile)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivity(Intent.createChooser(intent, "Share Image"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).showToolBar(true)
        (activity as MainActivity).showBottomNavigation(true)
        _binding = null
    }

    override fun selectedColor(color: Int,view : ImageView) {
        rasmContext.brushColor = color
        rasmContext.brushConfig.isEraser = false
        if(this::previousColour.isInitialized){
            previousColour.setImageResource(0)
        }
        view.setImageResource(R.drawable.ic_tick)
        previousColour = view
    }
}