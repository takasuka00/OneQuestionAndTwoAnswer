package com.takasuka.availabefiles.data.SQLite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import com.takasuka.everydaytask.DatabaseHelper
import java.lang.IllegalArgumentException
import java.lang.NullPointerException

class DatabaseEditor(context: Context) {
    val defaultData :MutableMap<String,Any?> = mutableMapOf()
    private val _helper = DatabaseHelper(context)
    private val db:SQLiteDatabase
    private lateinit var stmt :SQLiteStatement
    init {
        db = _helper.writableDatabase
        for(i in 1.._helper.db_default.size-1){
            defaultData.put(_helper.db_names[i],_helper.db_default[i])
        }
    }


    fun deleteColumn(id:Long){
        val sqlDelete = "DELETE FROM "  +_helper.DB_TITLE+ " WHERE _id = ?"
        stmt = db.compileStatement(sqlDelete)
        //変数のバイド。
        stmt.bindLong(1,id )
        //削除SQLの実行。
        stmt.executeUpdateDelete()
    }
    fun deleteColumn(value: Any?,name: String){
        val sqlDelete = "DELETE FROM "  +_helper.DB_TITLE+ " WHERE $name = $value"
        stmt = db.compileStatement(sqlDelete)
        //変数のバイド。
        //削除SQLの実行。
        stmt.executeUpdateDelete()
    }
    //0のところが-1Lなら最後に追加
    fun insertColumn(values:MutableMap<String,Any?>){

        for(i in values.keys){
            if(defaultData[i] == null)    IllegalArgumentException("指定のnameは存在しません${i}")
        }
        var maxNum =   this.getMaxId()
        var id :Long
        if(values["_id"]==null){
            id = -1L
        }else{
            id = values["_id"].toString().toLong()
        }

        if(id!=-1L && id > maxNum || id < -1L){  IndexOutOfBoundsException("そのid:${id}は存在しない")}

        if(id!=-1L){
            val preData = getData(id)
            if(preData != null)
            for(i in preData.keys){
                if(values[i] == null){
                    values.put(i, preData[i])
                }
            }
            deleteColumn(id)
        }else{

            for(i in defaultData.keys){
                if(values[i] == null){
                    values.put(i,defaultData[i])
                }
            }

        }
        val sb = StringBuilder()
        sb.append("INSERT INTO ")
        sb.append(_helper.DB_TITLE)
        sb.append(" (_id")

        for(i in 1 until _helper.db_names.size) {
            sb.append(",")
            sb.append(_helper.db_names[i])
        }
        sb.append(") VALUES (?")
        for(i in 1.._helper.db_names.size-1) {
            sb.append(",?")
        }
        sb.append(")")
        val sqlInsert = sb.toString()
        stmt = db.compileStatement(sqlInsert)

        //0のところが-1Lなら最後に追加
        if( id == -1L)
            stmt.bindLong(1,maxNum+1L )
        else
            stmt.bindLong(1,id)


        for(i in 1 until _helper.db_names.size){
            if(values[_helper.db_names.get(i)]!=null)
               when(_helper.db_values[i]){
                   "INTEGER"->{stmt.bindLong(i+1, values[_helper.db_names.get(i)].toString().toLong())}
                   "REAL" ->{stmt.bindDouble(i+1, values[_helper.db_names.get(i)] as Double)}
                   "TEXT" ->{stmt.bindString(i+1, values[_helper.db_names.get(i)].toString())}
                   "NONE" ->{stmt.bindString(i+1, values[_helper.db_names.get(i)].toString() )}
               }
        }
        stmt.executeInsert()
    }
    fun getData(id: Long):MutableMap<String,Any?>?{
        val sql = "SELECT * FROM " +_helper.DB_TITLE+ " WHERE _id = ${id}"
        val cursor = db.rawQuery(sql, null)
        val tempData:MutableList<Any> = mutableListOf()
        val data : MutableMap<String,Any?> = mutableMapOf()
        tempData.add(id)

        if(!cursor.moveToNext()){
            return null
        }
        for(i in 1.._helper.db_values.size-1){
            val idx = cursor.getColumnIndex(_helper.db_names[i])
            tempData.add(
                when(_helper.db_values[i]){
                    "INTEGER"->{cursor.getLong(idx)}
                    "REAL" ->{cursor.getDouble(idx)}
                    "TEXT" ->{cursor.getString(idx)}
                    "NONE" ->{cursor.getString(idx)}
                    else -> {
                        closeHelper()
                        throw IllegalArgumentException("db_valueに例外が発生しました。")
                    }
                })
        }

        for(i in 0.._helper.db_values.size-1){
            data.put(_helper.db_names[i],tempData[i])
        }
        return data
    }
    fun getData(name:String,value:Any?):MutableMap<Long,MutableMap<String,Any?>>{
        if(!_helper.db_names.contains(name)){
            closeHelper()
            throw IllegalArgumentException("DATABASEに名前が登録されていません。") }
        val sb = StringBuilder()
        sb.append("SELECT _id FROM ")
        sb.append(_helper.DB_TITLE)
        if(!(value ==null && name == "all"))
             sb.append(" WHERE $name = ${value.toString()}")
        val sql = sb.toString()
        val cursor = db.rawQuery(sql, null)
        val idList = mutableListOf<Long>()
        while (cursor.moveToNext()){
            val idx = cursor.getColumnIndex("_id")
            idList.add(cursor.getLong(idx))
        }
        val dataList :MutableMap<Long,MutableMap<String,Any?>> = mutableMapOf()
        var data : MutableMap<String,Any?>? = null
        for(i in idList){
            data = getData(i)
            if(data != null)
            dataList.put(i, data)
        }
        return dataList
    }

    fun getAllData():MutableMap<Long,MutableMap<String,Any?>> {
        val list = mutableMapOf<Long,MutableMap<String,Any?>>()
        var data : MutableMap<String,Any?>? = null
        for(i in 0L..getMaxId()){
            data = getData(i)
            if(data!=null)
            list.put(i,data)
        }
        return list
    }

    fun getMaxId():Long{
        val sql =  "SELECT _id FROM " +_helper.DB_TITLE
        val cursor = db.rawQuery(sql, null)
        var num =-1L
        while(cursor.moveToNext()) {
            val idx = cursor.getColumnIndex(_helper.db_names[0])
            num = cursor.getLong(idx)
        }
                return num
    }
    fun addCount(id: Long,key:List<String>,count : List<Long>){
        val data = getData(id)
        if(key.size!=count.size)    IllegalArgumentException("sizeが等しくない")
        if(data==null) NullPointerException("idが適正でない")
        val newData = data!!
        for(i in 0..key.size-1) {
            newData.put(key[i], data.get(key[i]).toString().toLong() + count[i])
        }
        insertColumn(newData)
    }

    fun closeHelper(){
        _helper.close()
    }

}

