package com.takasuka.forest2.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.takasuka.availabefiles.data.SQLite.DatabaseEditor
import com.takasuka.everydaytask.EditKey
import com.takasuka.everydaytask.Key
import com.takasuka.forest2.CustomApplication.Companion.TFCountor
import com.takasuka.forest2.CustomApplication.Companion.selectedAction
import com.takasuka.forest2.CustomApplication.Companion.selectedBl
import com.takasuka.forest2.CustomApplication.Companion.allDataSorted
import com.takasuka.forest2.CustomApplication.Companion.dataAddressList
import com.takasuka.forest2.CustomApplication.Companion.selectedId
import com.takasuka.forest2.CustomRecyclerViewAdapter
import com.takasuka.forest2.Key.ActEnum
import com.takasuka.forest2.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.testButton
import java.lang.StringBuilder
import java.text.Collator
import java.text.SimpleDateFormat
import java.util.*

@Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING", "DEPRECATION")
class MainActivity : AppCompatActivity() ,View.OnCreateContextMenuListener{
    private lateinit var adapter: CustomRecyclerViewAdapter
    private lateinit var editor:DatabaseEditor
    private lateinit var switchlist :MutableList<Boolean>
    private var temp = 0
    private lateinit var editKey: EditKey
    private lateinit var searchView: SearchView
    private val keyNames = listOf<String>(
        "pri",
        "count",
        "address",
        "_id",
        "rand",
        "name"
    )
    private var sortnames = listOf<String>()
    private var sortud = listOf<Int>()
    private val crimes = arrayOf(2,3,5,7,11)
    private var flag = true
    private var createFlag = false
    val handler = Handler()
    var timeValue = 0
    private val runnable = object : Runnable {
        override fun run() {
            timeValue++
            timeToText(timeValue)?.let{ TFView.text = it }
            handler.postDelayed(this, 1000)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        val allData = mutableMapOf<Long,MutableMap<String,Any?>>()
        createFlag = true
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = ""
        setSupportActionBar(findViewById(R.id.toolbar))
        window.setBackgroundDrawableResource(R.color.color_background)
        editor =   DatabaseEditor(applicationContext)
        editKey = EditKey(applicationContext)
        switchlist = mutableListOf<Boolean>(
            checkBox2.isChecked,
            checkBox3.isChecked,
            checkBox4.isChecked,
            checkBox5.isChecked,
            checkBox6.isChecked
        )
        val switches = listOf(
            checkBox2,
            checkBox3,
            checkBox4,
            checkBox5,
            checkBox6)
        val avk = editKey.readInt(Key.ADDRESSVIEWKEY)
        for (i in 0 until crimes.size) {
            if(avk%crimes[i] == 0){
                switchlist[i] = true
                switches[i].isChecked = true
            }
            else{
                switchlist[i] = false
                switches[i].isChecked = false
            }
        }
        var startStopFlag = true
        startButton.setOnClickListener{
            if(startStopFlag) { handler.post(runnable) }else{ handler.removeCallbacks(runnable)}
            startStopFlag=!startStopFlag }
        startButton.setOnLongClickListener {
            handler.removeCallbacks(runnable)
            timeValue = 0
            startStopFlag = true
            TFCountor = Pair(0,0)
            timeToText()?.let { TFView.text = it }
            return@setOnLongClickListener true
        }
        fab.setOnClickListener {
            selectedAction = ActEnum.Edit
            val intent = Intent(applicationContext,
                EditActivity()::class.java)
            intent.putExtra("key",-1L)
            startActivity(intent)
        }
        changeQAswitch.setOnClickListener {
            selectedBl = changeQAswitch.isChecked
            replaceData()
        }
        testButton.setOnClickListener {
            val data = mutableMapOf<String,Any?>("_id" to -1L,"question" to "question${temp++}","answer" to "answer","address" to 0)
            for(i in 0..49)
            editor.insertColumn(data)
            testView.text = testView.text.toString() +"\n"+ editor.getData(editor.getMaxId()).toString()
        }
        editText.height =0
        toggleButton2.setOnClickListener {
            if(flag){
                editText.height = 5000
                editText.visibility = View.VISIBLE
                val all = allData
                val sb = StringBuilder()
                for(i in all.keys) {
                    // sb.append("{")
                    sb.append(all[i]?.get("question").toString())
                    sb.append(",,")
                    sb.append(all[i]?.get("answer").toString())
                    sb.append(",,")
                    sb.append(all[i]?.get("address").toString())
                    sb.append(",,")
                    sb.append(all[i]?.get("second").toString())
                    sb.append(",,")
                    sb.append(all[i]?.get("count").toString())
                    sb.append(",,")
                    sb.append(all[i]?.get("count_true").toString())
                    sb.append("},\n")

                }
                editText.setText(sb.toString())
                testView.setBackgroundColor(Color.argb(200,255,255,255))
            }
            else{
                editText.height =-100
                editText.visibility = View.GONE
                testView.text = ""
                testView.setBackgroundColor(Color.argb(0,255,255,255))
            }
            flag = !flag
        }

        checkBox2.setOnClickListener(myClickListener(0))
        checkBox3.setOnClickListener(myClickListener(1))
        checkBox4.setOnClickListener(myClickListener(2))
        checkBox5.setOnClickListener(myClickListener(3))
        checkBox6.setOnClickListener(myClickListener(4))
        val layout = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layout
        val adRequest = AdRequest.Builder().build()
        adView2.loadAd(adRequest)

        for (i in 0..4) {
            dataAddressList.add(i,editor.getData("address", i))
            if (switchlist[i]) {
                allData.plusAssign(dataAddressList[i])
            }
        }

        val tmp = editKey.readString(Key.LASTSORTSTRING).split(",")
        if(tmp[0]!="")
            for(i in tmp)
                    allData[i.toLong()]?.let { allDataSorted.add(it) }

        adapter = CustomRecyclerViewAdapter(allDataSorted,editor,editKey)
        recyclerView.adapter = adapter

        settingsFunction()
        countText()
    }
    inner class myClickListener(private val boxId: Int):View.OnClickListener{
        override fun onClick(v: View?) {
            switchlist[boxId] = !switchlist[boxId]
            val a = dataAddressList[boxId]
            if(switchlist[boxId]){
                for(i in a.keys)
                allDataSorted.add(a[i] as MutableMap<String, Any?>)
                replaceData()
            }else{
                for(i in a.keys)
                allDataSorted.remove(a[i] as MutableMap<String, Any?>)
                replaceData()
                var flag = true
                for(i in switchlist){
                    if(i) {
                        flag = false
                        break
                    }
                }
                if(flag){
                    allDataSorted.clear()
                }
            }

        }
    }
    @SuppressLint("SimpleDateFormat")
    override fun onStart() {
        super.onStart()
        val date =SimpleDateFormat("yyyyMMdd").format(Date()).toString().toInt()
        if( editKey.readInt(Key.TODAYDATE ) != date) {
            editKey.writeInt(Key.TODAYCOUNTOR, 0)
            editKey.writeInt(Key.TODAYDATE, date)
        }
    }
    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        if(!createFlag) {
            when (selectedAction) {
                ActEnum.AddressChange -> { }
                ActEnum.Edit -> { dataAllReplace() }
                ActEnum.Settings -> {
                    settingsFunction()
                    replaceData()
                }
                ActEnum.EditAddressChange -> { handler.postDelayed({dataAllReplace()},30L) }
                else -> { replaceData() }
            }
            selectedAction = ActEnum.Main
            createFlag = false
        }

    }
    override fun onPause() {
        super.onPause()
        var avk : Int = 1
        for(i in 0 until crimes.size){
            if(switchlist[i]){ avk *= crimes[i] }
        }
        editKey.writeInt(Key.ADDRESSVIEWKEY,avk)

        
    }
    override fun onStop() {
        super.onStop()
        for(i in allDataSorted)
            editor.insertColumn(i)

        val shortlists = getSortList(allDataSorted)
        if(shortlists.size == 0){
            editKey.writeString(Key.LASTSORTSTRING,"")
        }else {
            val sb = StringBuilder()
            sb.append(shortlists[0])
            for(i in 1 until shortlists.size) {
                sb.append(",")
                sb.append(shortlists[i])
            }
            editKey.writeString(Key.LASTSORTSTRING, sb.toString())
        }
    }
    override fun onDestroy() {
        editor.closeHelper()
        super.onDestroy()
    }

    private fun settingsFunction(){
        if(editKey.readBoolean(Key.SETTINGIMV)){
            testButton.visibility = View.VISIBLE
            toggleButton2.visibility = View.VISIBLE
        } else{
            testButton.visibility = View.INVISIBLE
            toggleButton2.visibility = View.INVISIBLE
        }
        sortnames = listOf<String>(
                keyNames[editKey.readInt(Key.FIRSTSORTVALUE)],
                keyNames[editKey.readInt(Key.SECONDSORTVALUE)],
                keyNames[editKey.readInt(Key.LASTSORTVALUE)],
                ""
        )
        sortud = listOf<Int>(
                editKey.readInt(Key.FIRSTSORTUD),
                editKey.readInt(Key.SECONDSORTUD),
                editKey.readInt(Key.LASTSORTUD)
        )
    }



    @SuppressLint("SetTextI18n")
    fun countText(){
        if(editKey.readBoolean(Key.SETTING4))
            testView.text = "${getString(R.string.count)} : ${allDataSorted.size.toString()}    "
        else
            testView.text = ""
        testView.text = "${testView.text}${getString(R.string.today)} : ${editKey.readInt(Key.TODAYCOUNTOR)}\t${getString(R.string.allday)} : ${editKey.readInt(Key.ALLDAYCOUNTOR)}"
    }

    private fun replaceData(){
        getSort()
        adapter = CustomRecyclerViewAdapter(allDataSorted,editor,editKey)
        recyclerView.adapter = adapter
        countText()
    }
    private fun getSort(){
        val tmp :MutableList<MutableMap<String,Any?>> = mutableListOf()
        val allData:MutableMap<Long,MutableMap<String,Any?>> = mutableMapOf()
        for(i in dataAddressList)
            allData.plusAssign(i)

        for(i in getSortList(allDataSorted)){
            if(allData[i] != null)
            tmp.add(allData[i] as MutableMap<String, Any?>)
        }
        allDataSorted = tmp
    }

    private fun getSortList(data:MutableMap<Long,MutableMap<String,Any?>>): MutableList<Long> {
        val sortlistlist:MutableList<MutableList<Long>> = mutableListOf()
        val rand = Random()
        val s = mutableMapOf<Long,Long>() //  junban = s[id]
        if(sortnames.contains("name")){
            val stList = mutableListOf<String>()
            val stMap = mutableMapOf<String,Long>()
            var selected = "question"
            if(changeQAswitch.isChecked)
                selected = "answer"

            for(id in data.keys){
                val st =data[id]?.get(selected).toString()
                stMap.put(st,id)
                stList.add(st)
            }

            val collator = Collator.getInstance(Locale.JAPANESE)
            Collections.sort(stList,collator)


            for(i in 0 until  stList.size){
                s.put(stMap[stList[i]]!!,i.toLong())
            }
        }
        for(id in data.keys) {
            val list = mutableListOf<Long>()
            list.add(0, data[id]?.get("_id").toString().toLong())
            for(k in 1..3){
                if(sortnames[k-1]==sortnames[k]){ continue }

                if (sortnames[k-1] == "pri") {
                    val x = data[id]?.get("count").toString().toLong()
                    if (x == 0L)
                        list.add(-1L)
                    else
                        list.add( data[id]?.get("count_true").toString().toLong() * 10 / x)   // 10のところは改変し
                }
                else if(sortnames[k-1] == "rand"){
                    list.add( rand.nextLong())
                    break
                }else if(sortnames[k-1] == "name"){

                    list.add(s[id]!!)
                    break

                } else{
                    if(sortnames[k-1]=="_id" || sortnames[k-1] == "count" || sortnames[k-1] == "address")
                        list.add(data[id]?.get(sortnames[k-1]).toString().toLong())
                    if(sortnames[k-1] == "id")
                        break
                }
            }
            sortlistlist.add(list)
        }

        var flag = true
        while (flag) {  //sorting
            flag = false
            for (i in 1 until sortlistlist.size) {
                if (getBl(sortlistlist[i],sortlistlist[i-1])) {
                    flag = true
                    val tmp = sortlistlist[i]
                    sortlistlist[i] = sortlistlist[i - 1]
                    sortlistlist[i - 1] = tmp
                }
            }
            for(i in sortlistlist.size-1 downTo 1)
                if (getBl(sortlistlist[i],sortlistlist[i-1])) {
                    flag = true
                    val tmp = sortlistlist[i]
                    sortlistlist[i] = sortlistlist[i - 1]
                    sortlistlist[i - 1] = tmp
                }
        }

        val sortlist = mutableListOf<Long>()
        for(i in sortlistlist)
            sortlist.add(i[0])
        return sortlist
    }
    private fun getSortList(data:MutableList<MutableMap<String,Any?>>): MutableList<Long>{
        val exData = mutableMapOf<Long,MutableMap<String,Any?>>()
        for(i in 0L until data.size){
            exData[i] = data[i.toInt()]
        }
        return getSortList(exData)
    }
    private fun getBl(first:MutableList<Long>, second:MutableList<Long>):Boolean{
        if (sortud[0] == 1 && first[1] < second[1]) return true
        if (sortud[0] == 0 && first[1] > second[1]) return true
        if (first.size >= 3 && first[1] == second[1]) {
            if (sortud[1] == 1 && first[2] < second[2]) { return true }
            if (sortud[1] == 0 && first[2] > second[2]) { return true }


            if (first.size == 4 && first[2] == second[2]) {
                if (sortud[2] == 1 && first[3] < second[3]) { return true }
                if (sortud[2] == 0 && first[3] > second[3]) { return true }


            }
        }

        return false
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_replace, menu)
        val menuItem = menu.findItem(R.id.search_menu_view)
        this.searchView = menuItem.actionView as SearchView
        //if(!this.searchView.)
        return true
    }//////////////////////////////////////////////////////////////////////////////////////////////////clear を消したい
    @SuppressLint("SetTextI18n")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_replace ->{
                if(selectedAction==ActEnum.EditAddressChange || selectedAction == ActEnum.AddressChange){
                    dataAllReplace()
                }
                selectedAction=ActEnum.Main
                replaceData()
            }
            R.id.action_delete ->{
                selectedAction = if(selectedAction!=ActEnum.Delete) {ActEnum.Delete } else{ ActEnum.Main }
                replaceData()
            }
            R.id.action_settings ->{
                selectedAction = ActEnum.Settings
                val intent: Intent = Intent(applicationContext, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.action_address->{
                selectedAction=ActEnum.AddressChange
                replaceData()
            }
            R.id.search_menu_view ->{


            }
            R.id.action_tf->{
                if(TFLayout.visibility == View.GONE){
                    TFLayout.visibility =View.VISIBLE
                    TFView.text= timeToText(0)
                }else{
                    TFLayout.visibility = View.GONE
                    handler.removeCallbacks(runnable)
                    timeValue = 0
                    TFCountor = Pair(0,0)
                }
                replaceData()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun dataAllReplace(){
        allDataSorted.clear()
        for(i in 0 until switchlist.size)
            if(switchlist[i])
                for(k in dataAddressList[i].keys)
                allDataSorted.plusAssign(dataAddressList[i][k]!!)
    }
    private fun timeToText(time: Int = 0): String? {
        return if (time < 0) { null } else {
            "${TFCountor.first} / ${TFCountor.second}  = ${(TFCountor.first.toDouble()/ TFCountor.second*100).toInt()}% \t "+"%1$3d:%2$02d".format(time  / 60,time % 60) } }
}