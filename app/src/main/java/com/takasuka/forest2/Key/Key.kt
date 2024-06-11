package com.takasuka.everydaytask

import android.content.Context
import android.content.SharedPreferences
import java.lang.IllegalArgumentException

public enum class Key {

    FIRSTSORTUD(1),
    SECONDSORTUD(1),
    LASTSORTUD(1),
    FIRSTSORTVALUE(0),
    SECONDSORTVALUE(4),
    LASTSORTVALUE(3),
    SETTING1(true),
    SETTING2(false),
    SETTING3(false),
    SETTING4(false),
    ADDRESSCONECTOR(0),
    ADDRESSVIEWKEY(2310),
    SETTINGIMV(false),
    TODAYCOUNTOR(0),
    ALLDAYCOUNTOR(0),
    TODAYDATE(0),
    LASTSORTSTRING(""),
    ;


    private enum class Type{
        BOOLEAN,
        INT,
        LONG,
        STRING,
        STRING_SET,
    }
    private val type:Type
    private val defaultValue:Any
    constructor(value:Any){
      type = when(value){
          is Boolean-> Type.BOOLEAN
          is Int-> Type.INT
          is Long-> Type.LONG
          is String-> Type.STRING
          is Set<*>-> Type.STRING_SET
          else-> throw IllegalArgumentException("型の名前ちがくね？")
      }
      this.defaultValue = value
    }
    fun getDef():Any{ return this.defaultValue }

    public fun getThisPreference(context: Context):SharedPreferences{ return context.getSharedPreferences("DEFAULT_PREF",Context.MODE_PRIVATE) }

    internal fun isBooleanKey():Boolean { return type === Type.BOOLEAN }
    internal fun isIntKey():Boolean { return type === Type.INT }
    internal fun isLongKey():Boolean { return type === Type.LONG }
    internal fun isStringKey():Boolean { return type === Type.STRING}
    internal fun isSetKey():Boolean{return  type === Type.STRING_SET}

}