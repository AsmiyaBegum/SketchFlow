package com.ab.sketchflow.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ab.sketchflow.model.DraftEntity

@Dao
interface DraftDao {
    @Insert
     fun insertDraft(draft: DraftEntity): Long

     @Update
     fun updateDraft(draft: DraftEntity)

    @Query("SELECT * FROM draftentity ORDER BY id desc")
    fun getAllDrafts(): List<DraftEntity>

    @Query("SELECT * FROM draftentity where id = :draftId")
    fun getDraftDataBasedOnId(draftId : Long) : DraftEntity
}