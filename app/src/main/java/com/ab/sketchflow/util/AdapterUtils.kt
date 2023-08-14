package com.ab.sketchflow.util

import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ab.sketchflow.R
import com.ab.sketchflow.databinding.BrushRecyclerRowBinding
import com.ab.sketchflow.databinding.DrawingCategoryListBinding
import com.ab.sketchflow.model.ColorSet
import com.ab.sketchflow.model.DraftEntity
import com.ab.sketchflow.model.HomeDrawing
import com.ab.sketchflow.ui.home.DrawingListAdapter
import com.ab.sketchflow.ui.home.PaintDetailViewHolder
import com.ab.sketchflow.util.Utils.backgroundTintValue

object AdapterUtils {

    fun setUpColorSetAdapter(colorSets : List<ColorSet>,delegate : ColorSetDelegate) :  GenericAdapter<ColorSet, BrushRecyclerRowBinding,List<Unit>>{

        var initialSetup  = true
        val adapter = GenericAdapter(R.layout.brush_recycler_row,object : GenericAdapterInteraction<ColorSet, BrushRecyclerRowBinding,List<Unit>>(){

            override fun bindingViewHolder(binding: BrushRecyclerRowBinding, data: ColorSet, holder: GenericAdapter.GenericViewHolder<ColorSet, BrushRecyclerRowBinding, List<Unit>>, additionalData: List<Unit>?) {
                if(initialSetup && data.colors[0] == 0xFF000000) {
                    initialSetup = false
                    delegate.selectedColor(data.colors[0].toInt(),binding.color1)
                }

                binding.color1.backgroundTintList = data.colors[0].toInt().backgroundTintValue()
                binding.color2.backgroundTintList = data.colors[1].toInt().backgroundTintValue()
                binding.color3.backgroundTintList = data.colors[2].toInt().backgroundTintValue()
                binding.color4.backgroundTintList = data.colors[3].toInt().backgroundTintValue()
                binding.color5.backgroundTintList = data.colors[4].toInt().backgroundTintValue()
                binding.color6.backgroundTintList = data.colors[5].toInt().backgroundTintValue()
                binding.color7.backgroundTintList = data.colors[6].toInt().backgroundTintValue()
                binding.color8.backgroundTintList = data.colors[7].toInt().backgroundTintValue()

                binding.color1.setOnClickListener{
                    delegate.selectedColor(data.colors[0].toInt(),binding.color1)
                }

                binding.color2.setOnClickListener{
                    delegate.selectedColor(data.colors[1].toInt(),binding.color2)
                }

                binding.color3.setOnClickListener{
                    delegate.selectedColor(data.colors[2].toInt(),binding.color3)
                }

                binding.color4.setOnClickListener{
                    delegate.selectedColor(data.colors[3].toInt(),binding.color4)
                }

                binding.color5.setOnClickListener{
                    delegate.selectedColor(data.colors[4].toInt(),binding.color5)
                }

                binding.color6.setOnClickListener{
                    delegate.selectedColor(data.colors[5].toInt(),binding.color6)
                }

                binding.color7.setOnClickListener{
                    delegate.selectedColor(data.colors[6].toInt(),binding.color7)
                }

                binding.color8.setOnClickListener{
                    delegate.selectedColor(data.colors[7].toInt(),binding.color8)
                }
            }


            override fun onClicked(data: ColorSet, binding: BrushRecyclerRowBinding) {
                //override fun not implemented
            }



        })
        adapter.addItems(colorSets)
        return adapter
    }

    fun setUpHomeScreenCategoryListAdapter(list : List<HomeDrawing>, delegate: PaintDetailViewHolder.PaintDetailDelegate) : GenericAdapter<HomeDrawing, DrawingCategoryListBinding,List<Unit>>{

        val adapter = GenericAdapter(R.layout.drawing_category_list,object : GenericAdapterInteraction<HomeDrawing, DrawingCategoryListBinding,List<Unit>>(){

            override fun bindingViewHolder(
                binding: DrawingCategoryListBinding,
                data: HomeDrawing,
                holder: GenericAdapter.GenericViewHolder<HomeDrawing, DrawingCategoryListBinding, List<Unit>>,
                additionalData: List<Unit>?
            ) {
                binding.categoryName.text = data.paintName
                updateStreamList(data.paintList,binding)
            }


            private fun updateStreamList(paintList : List<DraftEntity>, binding: DrawingCategoryListBinding){
                binding.drawingList.layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.HORIZONTAL,false)
                binding.drawingList.adapter = DrawingListAdapter(paintList,delegate)
            }

            override fun onClicked(data: HomeDrawing, binding: DrawingCategoryListBinding) {
                //override fun not implemented
            }
        })
        adapter.addItems(list)
        return adapter
    }

    interface ColorSetDelegate{
        fun selectedColor(color : Int,view : ImageView)
    }
}