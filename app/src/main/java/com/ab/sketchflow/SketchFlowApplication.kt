package com.ab.sketchflow

import android.app.Application
import android.content.Context
import android.content.Intent
import android.database.Observable
import android.graphics.Bitmap
import android.util.Log
import androidx.room.Room
import com.ab.sketchflow.util.Utils
import rx.subjects.PublishSubject
import kotlin.system.exitProcess

class SketchFlowApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        if(!Utils.isBuildVariantDebug()){
            Thread.setDefaultUncaughtExceptionHandler { _, e ->
                handleUncaughtException(e)
                restartApp()
            }
        }
    }

    private fun handleUncaughtException(e : Throwable){
        Log.e("app_crash",e.message.toString())
    }

    private fun restartApp() {
        val intent = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        exitProcess(1)
    }

    companion object {
        private var appContext: Context? = null
        private val selectedColorCode : PublishSubject<Int> = PublishSubject.create()
        lateinit var bitDraft : Bitmap

        fun appContext(): Context? {
            return appContext
        }

        fun setSelectedColorCode(int : Int) {
            selectedColorCode.onNext(int)
        }

        fun getSelectedColorCode() : PublishSubject<Int> {
            return selectedColorCode
        }
    }
}