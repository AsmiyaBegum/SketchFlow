package com.ab.sketchflow.ui.draw

import androidx.lifecycle.ViewModel
import com.ab.sketchflow.SketchFlowApplication
import com.ab.sketchflow.database.SketchFlowDatabase
import com.ab.sketchflow.model.DraftEntity
import com.ab.sketchflow.repository.DraftRepository

class SketchViewModel : ViewModel() {

    private val sketchDb = SketchFlowDatabase.getDatabase(SketchFlowApplication.appContext()!!)
    private val draftRepository : DraftRepository = DraftRepository(sketchDb)

     fun insertDraft(bitmapByteArray : ByteArray, callback : (Unit) -> (Unit)){
         val draft = DraftEntity(bitmapData = bitmapByteArray)
         draftRepository.insertDraft(draft)
         callback(Unit)
    }

    fun updateDraft(draftEntity: DraftEntity,callback: (Unit) -> Unit) {
        draftRepository.updateDraft(draftEntity)
        callback(Unit)
    }

    fun getDraftEntityData(id : Long,callback: (DraftEntity) -> Unit) {
        val draft = draftRepository.getDraftDataBasedOnId(id)
        callback(draft)
    }
}