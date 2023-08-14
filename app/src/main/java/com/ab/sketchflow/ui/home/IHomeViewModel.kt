package com.ab.sketchflow.ui.home

import com.ab.sketchflow.model.HomeDrawing
import rx.Observable

interface IHomeViewModel {

    interface Inputs {
        fun recentPainting()
    }

    interface  Outputs {
        fun getRecentPainting() : Observable<List<HomeDrawing>>
    }

    interface Errors {
        fun errorHandling() : Observable<String>
    }
}