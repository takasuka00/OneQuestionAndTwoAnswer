package com.takasuka.everydaytask

import android.content.Context
import java.lang.IllegalArgumentException

class EditKey(context: Context) {
    private val context = context
    fun writeBoolean( key:Key, value:Boolean){
        if(!key.isBooleanKey())
            throw IllegalArgumentException(key.name + "is not key for Boolean")
        else
            key.getThisPreference(context).edit().putBoolean(key.name,value).apply()
    }
    fun writeInt( key:Key, value:Int){
        if(!key.isIntKey())
            throw IllegalArgumentException(key.name + "is not key for Int")
        else
            key.getThisPreference(context).edit().putInt(key.name,value).apply()
    }
    fun writeLong(key:Key, value:Long){
        if(!key.isLongKey())
            throw IllegalArgumentException(key.name + "is not key for Long")
        else
            key.getThisPreference(context).edit().putLong(key.name,value).apply()
    }
    fun writeString( key:Key, value:String) {
        if (!key.isStringKey())
            throw IllegalArgumentException(key.name + "is not key for String")
        else
            key.getThisPreference(context).edit().putString(key.name, value).apply()
    }
    fun readBoolean(key: Key):Boolean{
        return key.getThisPreference(context).getBoolean(key.name,key.getDef() as Boolean)
    }
    fun readInt(key: Key):Int{
        return key.getThisPreference(context).getInt(key.name,key.getDef() as Int)
    }
    fun readLong(key: Key):Long{
        return key.getThisPreference(context).getLong(key.name,key.getDef() as Long)
    }
    fun readString(key: Key):String{
        return key.getThisPreference(context).getString(key.name,key.getDef() as String).toString()
    }

}