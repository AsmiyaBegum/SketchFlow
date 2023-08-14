package com.ab.sketchflow.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ab.sketchflow.databinding.PaintingRowBinding
import com.ab.sketchflow.model.DraftEntity
import com.bumptech.glide.Glide
import com.jakewharton.rxbinding.view.clicks

class DrawingListAdapter(private val streamDetailList : List<DraftEntity>, private val delegate : PaintDetailViewHolder.PaintDetailDelegate)  : RecyclerView.Adapter<PaintDetailViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaintDetailViewHolder {
        val binding = PaintingRowBinding.inflate(LayoutInflater.from(parent.context))
        return PaintDetailViewHolder(binding,delegate)
    }

    override fun onBindViewHolder(holder: PaintDetailViewHolder, position: Int) {
        holder.onBind(streamDetailList[position])
    }

    override fun getItemCount(): Int {
        return streamDetailList.size
    }


}


class PaintDetailViewHolder(private val binding : PaintingRowBinding, private val delegate: PaintDetailDelegate) : RecyclerView.ViewHolder(binding.root){

    fun onBind(data  : DraftEntity){

      val glideQuery =   Glide.with(binding.root)
                if(data.url.isBlank()){
                    glideQuery.load(data.bitmapData)
                }else{
                    glideQuery.load(data.url)
                }
            .into(binding.imageThumbnail)


        binding.imageThumbnail.clicks()
            .subscribe {
                delegate.selectedPainting(data)
            }

    }


    interface PaintDetailDelegate{
        fun selectedPainting(data: DraftEntity)
    }
}