package com.takasuka.everydaytask

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.lang.IllegalArgumentException
/*
SQLiteのデータ型
NUMERIC		TEXT型が入れられると数字に変換しようとする。むりならTEXT型のまま。
INTEGER	整数にして収納
REAL		doubleにして収納
TEXT		まんま
NONE		自動で判断(NULL,INTERGER,REAL,TEXT,BLOB)

    var proDate :Long = SimpleDateFormat("yyyyMMdd").format(Date()).toString().toLong()
    var day :Long = 0L
    var rand:Long = (Date().time%1000).toLong()
 */

class DatabaseHelper(context: Context):SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
	companion object{
		//	ここの4つを変える。
		//データベースの名前
		private const val PRIVATE_DB_NAME = "forests_database"
		//各データの名前ただし、最初のデータ名(_id)は固定
		private val PRIVATE_db_names  = listOf<String>("_id","question","answer","second","count","count_true","address")
		//各データの型。順番は前のものと対応させる。
		private val PRIVATE_db_values = listOf<String>("INTEGER","TEXT","TEXT","TEXT","INTEGER","INTEGER","INTEGER")
		val PRIVATE_db_default :List<String?> = listOf(null,"null","null","null","0","0","0")


		private  const val DATABASE_NAME =  PRIVATE_DB_NAME + ".db"
		private const val DATABASE_VERSION = 1
	}
	val DB_TITLE = PRIVATE_DB_NAME

	val db_default = PRIVATE_db_default
	val db_names  = PRIVATE_db_names
	val db_values = PRIVATE_db_values


	override fun onCreate(db: SQLiteDatabase) {
		if(db_names.size != db_values.size){   throw IllegalArgumentException("names.size != values.size") }

		val sb = StringBuilder()
		sb.append("CREATE TABLE ")
		sb.append(DB_TITLE)
		sb.append(" (")
		sb.append("_id INTEGER PRIMARY KEY")	//これ必須
		for(i in 1 .. db_names.size-1){
			sb.append(",")
			sb.append(db_names[i])
			sb.append(" ")
			sb.append(db_values[i])
			if(PRIVATE_db_default.size == db_names.size && PRIVATE_db_default.get(i)!= null){
				sb.append(" DEFAULT ")
				sb.append(PRIVATE_db_default.get(i))
			}
		}
		sb.append(");")
		val sql = sb.toString()

		//SQLの実行。
		db.execSQL(sql)

	}
	//今のバージョンと違うと実行。ALTER TABLE など。
	override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

}