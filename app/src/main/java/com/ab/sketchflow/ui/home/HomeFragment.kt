package com.ab.sketchflow.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ab.sketchflow.R
import com.ab.sketchflow.SketchFlowApplication
import com.ab.sketchflow.databinding.FragmentHomeBinding
import com.ab.sketchflow.model.DraftEntity
import com.ab.sketchflow.model.HomeDrawing
import com.ab.sketchflow.util.AdapterUtils
import com.ab.sketchflow.util.Constants
import com.ab.sketchflow.util.Utils
import rx.android.schedulers.AndroidSchedulers

class HomeFragment : Fragment(),PaintDetailViewHolder.PaintDetailDelegate {

    private var _binding: FragmentHomeBinding? = null
    lateinit var viewModel: HomeViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun bind(){

        viewModel.output.getRecentPainting()
            .observeOn(AndroidSchedulers.mainThread())
            .filter{ _binding != null}
            .subscribe {
                updateList(it)
            }

        viewModel.error.errorHandling()
            .observeOn(AndroidSchedulers.mainThread())
            .filter{ _binding != null}
            .subscribe {
                Toast.makeText(requireContext(),it.toString(),Toast.LENGTH_SHORT).show()
            }

        binding.drawingLayout.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigattion_drawing_layout)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.input.recentPainting()
        bind()
        updateDrawingLayout()
    }

    private fun updateDrawingLayout(){
        binding.drawingList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun updateList(homeDrawingList : List<HomeDrawing>) {
        binding.drawingList.layoutManager = LinearLayoutManager(requireContext())
        binding.drawingList.adapter = AdapterUtils.setUpHomeScreenCategoryListAdapter(homeDrawingList,this)
    }

    override fun selectedPainting(data: DraftEntity) {
        SketchFlowApplication.bitDraft = Utils.byteArrayToBitmap(data.bitmapData)
        val bundle = Bundle()
        bundle.putLong(Constants.DRAFT_DATA_KEY,data.id)
        findNavController().navigate(R.id.action_navigation_home_to_navigattion_drawing_layout,bundle)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}