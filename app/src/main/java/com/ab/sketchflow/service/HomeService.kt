package com.ab.sketchflow.service

import com.ab.sketchflow.SketchFlowApplication
import com.ab.sketchflow.api.RetrofitWrapper
import com.ab.sketchflow.database.SketchFlowDatabase
import com.ab.sketchflow.model.HomeDrawing
import com.ab.sketchflow.repository.DraftRepository
import rx.Observable
import rx.schedulers.Schedulers

class HomeService {

    private val apiService = RetrofitWrapper.clipVideoApiInterface
    private val sketchDb = SketchFlowDatabase.getDatabase(SketchFlowApplication.appContext()!!)
    private val draftRepository : DraftRepository = DraftRepository(sketchDb)

    fun getHomeScreenRecentPainting() : Observable<List<HomeDrawing>> {
        return Observable.just(Unit)
            .observeOn(Schedulers.io())
            .map {
                val response = apiService.getHomeScreenRecentList().execute()
                    response.body()
            }
    }

    fun getAllDraftPainting(homeList : List<HomeDrawing>) : Observable<List<HomeDrawing>> {
        return Observable.just(Unit)
            .observeOn(Schedulers.io())
            .map {
                val draftList = draftRepository.getAllDrafts()
                val list = homeList.toMutableList()
                if(draftList.isNotEmpty()) {
                    list.add(0,HomeDrawing("Draft",draftList))
                }
                list
            }
    }
}