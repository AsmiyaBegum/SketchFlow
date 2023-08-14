package com.ab.sketchflow.model

import android.graphics.Bitmap

data class HomeDrawing(
    val paintName: String,
    val paintList: List<DraftEntity>
)

data class Painting(
    val name: String = "",
    val url: String = "",
    val bitmap: Bitmap? = null
)