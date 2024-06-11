package com.takasuka.forest2

import android.app.Application
import com.takasuka.forest2.Key.ActEnum

class CustomApplication :Application() {
    companion object {
        var selectedAction = ActEnum.Main
        var selectedBl = false
        var selectedId = -1L
        val dataAddressList :MutableList<MutableMap<Long,MutableMap<String,Any?>>> = mutableListOf()
        var allDataSorted:MutableList<MutableMap<String,Any?>> = mutableListOf()
        var TFCountor :Pair<Int,Int> = Pair(0,0)
    }
    override fun onCreate() {
        super.onCreate()
    }
}