package com.ab.sketchflow.repository

import com.ab.sketchflow.database.SketchFlowDatabase
import com.ab.sketchflow.model.DraftEntity

class DraftRepository(private val draftDatabase: SketchFlowDatabase) {


     fun insertDraft(draft: DraftEntity) {
         draftDatabase.draftDao().insertDraft(draft)
    }

    fun updateDraft(draftEntity: DraftEntity) {
        draftDatabase.draftDao().updateDraft(draftEntity)
    }

     fun getAllDrafts(): List<DraftEntity> {
        return draftDatabase.draftDao().getAllDrafts()
    }

    fun getDraftDataBasedOnId(id : Long) : DraftEntity {
        return draftDatabase.draftDao().getDraftDataBasedOnId(id)
    }
 }