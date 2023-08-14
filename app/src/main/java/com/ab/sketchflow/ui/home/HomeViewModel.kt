package com.ab.sketchflow.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ab.sketchflow.model.HomeDrawing
import com.ab.sketchflow.service.HomeService
import rx.Observable
import rx.subjects.PublishSubject

class HomeViewModel : ViewModel(),IHomeViewModel.Inputs,IHomeViewModel.Outputs,IHomeViewModel.Errors {

    private val homeService : HomeService = HomeService()
    val input : IHomeViewModel.Inputs = this
    val output : IHomeViewModel.Outputs = this
    val error : IHomeViewModel.Errors = this

    private val recentPaintingSubject :PublishSubject<List<HomeDrawing>> = PublishSubject.create()
    private val errorResponse : PublishSubject<String> = PublishSubject.create()

    override fun recentPainting() {
        homeService.getHomeScreenRecentPainting()
            .flatMap {
               homeService.getAllDraftPainting(it)
            }
            .subscribe ({
                recentPaintingSubject.onNext(it)
                        },{ errorResult ->
                errorResponse.onNext(errorResult.message)
            })
    }

    override fun getRecentPainting(): Observable<List<HomeDrawing>> {
        return recentPaintingSubject
    }

    override fun errorHandling(): Observable<String> {
        return errorResponse
    }
}