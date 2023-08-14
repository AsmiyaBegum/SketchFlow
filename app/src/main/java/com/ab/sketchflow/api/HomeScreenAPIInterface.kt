package com.ab.sketchflow.api

import com.ab.sketchflow.model.HomeDrawing
import retrofit2.Call
import retrofit2.http.GET

interface HomeScreenAPIInterface {

    @GET("v3/73744481-e14a-4357-bdce-483575e94ab4")
    fun getHomeScreenRecentList() : Call<List<HomeDrawing>>

}