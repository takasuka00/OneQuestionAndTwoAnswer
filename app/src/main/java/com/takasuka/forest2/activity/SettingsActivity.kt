package com.takasuka.forest2.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.takasuka.availabefiles.data.SQLite.DatabaseEditor
import com.takasuka.everydaytask.EditKey
import com.takasuka.everydaytask.Key
import com.takasuka.forest2.R
import kotlinx.android.synthetic.main.activity_settings.*
import java.lang.IllegalArgumentException
import java.util.regex.Pattern

@Suppress("DEPRECATION")
class SettingsActivity : AppCompatActivity() {
    lateinit var editKey :EditKey
    lateinit var editor :DatabaseEditor
    var countor = 0
    val first_id_list = listOf<Int>(
        R.id.radioButton15,
        R.id.radioButton16,
        R.id.radioButton17,
        R.id.radioButton18,
        R.id.radioButton19,
        R.id.radioButton2
    )
    val second_id_list = listOf<Int>(
        R.id.radioButton6,
        R.id.radioButton7,
        R.id.radioButton,
        R.id.radioButton8,
        R.id.radioButton9,
        R.id.radioButton3
    )
    val last_id_list = listOf<Int>(
        -1,
        -1,
        -1,
        R.id.radioButton11,
        R.id.radioButton10
        )
    val first_bl = listOf<Int>(
        R.id.radioFirstDown,
        R.id.radioFirstUp)
    val second_bl = listOf<Int>(
        R.id.radioSecondDown,
        R.id.radioSecondUp)
    val last_bl = listOf<Int>(
        R.id.radioLastDown,
        R.id.radioLastUp)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        editor = DatabaseEditor(applicationContext)
        editKey = EditKey(applicationContext)

        var first = editKey.readInt((Key.FIRSTSORTVALUE))
        var second = editKey.readInt((Key.SECONDSORTVALUE))
        val last = editKey.readInt((Key.LASTSORTVALUE))
        firstSortRadioGroup.check(first_id_list[first])
        secondSortRadioGroup.check(second_id_list[second])
        lastSortRadioGroup.check(last_id_list[last])
        if(first >=3){
            for(i in second_id_list){ findViewById<RadioButton>(i).isEnabled = false }
            for(i in second_bl){ findViewById<RadioButton>(i).isEnabled = false }
        }
        if(second >= 3 || first>=3){
            for(i in last_id_list){
                if(i!=-1)
                findViewById<RadioButton>(i).isEnabled = false
            }
            for(i in last_bl){ findViewById<RadioButton>(i).isEnabled = false }
        }
        firstSortRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            for(k in 0 until first_id_list.size)
                if(first_id_list[k] == checkedId){
                    first = k
                    var bl = true
                    if(k >= 3)
                        bl = false
                    for(i in second_id_list){ findViewById<RadioButton>(i).isEnabled = bl}
                    for(i in second_bl){ findViewById<RadioButton>(i).isEnabled = bl }
                    for(i in last_id_list) if(i!=-1)
                        findViewById<RadioButton>(i).isEnabled = bl

                    for(i in last_bl){ findViewById<RadioButton>(i).isEnabled = bl }

                    break
                }
        }
        secondSortRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            for(k in 0 until second_id_list.size){
                if(second_id_list[k] == checkedId){
                    second = k
                    var bl = true
                    if(k>=3)
                        bl = false
                    for(i in last_id_list) if(i!=-1)
                        findViewById<RadioButton>(i).isEnabled = bl
                    for(i in last_bl){ findViewById<RadioButton>(i).isEnabled = bl}

                    break
                }
            }
        }


        val radiolist = listOf<Int>(
            R.id.radioButton01,
            R.id.radioButton02,
            R.id.radioButton03,
            R.id.radioButton04,
            R.id.radioButton05
        )

        firstBl.check(first_bl[editKey.readInt(Key.FIRSTSORTUD)])
        secondBl.check(second_bl[editKey.readInt(Key.SECONDSORTUD)])
        lastBl.check(last_bl[editKey.readInt(Key.LASTSORTUD)])

        settingSwitch1.isChecked = editKey.readBoolean(Key.SETTING1)
        settingSwitch2.isChecked = editKey.readBoolean(Key.SETTING2)
        settingSwitch3.isChecked = editKey.readBoolean(Key.SETTING3)
        settingSwitch4.isChecked = editKey.readBoolean(Key.SETTING4)
        switchim.isChecked = editKey.readBoolean(Key.SETTINGIMV)


        if(switchim.isChecked){
            linerlayout.visibility = View.VISIBLE
            switchim.visibility = View.VISIBLE
        }
        textView6.setOnClickListener {
            if(++countor >= 3 && switchim.visibility == View.INVISIBLE)
                switchim.setVisibility(View.VISIBLE)
        }
        switchim.setOnCheckedChangeListener { _, isChecked ->
            editKey.writeBoolean(Key.SETTINGIMV,isChecked)
            if(isChecked){
                Toast.makeText(applicationContext, R.string.debug_on_toast, Toast.LENGTH_LONG).show()
                linerlayout.visibility = View.VISIBLE
            }else{
                Toast.makeText(applicationContext, R.string.debug_off_toast, Toast.LENGTH_LONG).show()
                linerlayout.visibility = View.INVISIBLE
            }
        }
        inputButton.setOnClickListener {
            val txt = editTextTextMultiLine.text.toString()
            val p = Pattern.compile(".*[}][,].*")
            val s = Pattern.compile("[}][,]\n").toRegex()   //再考の余地あり

            if(p.matcher(txt).find()){
                editTextTextMultiLine.setText("")
                var tmpList = mutableMapOf<String,Any?>()
                val qlist = txt.split(s) as MutableList
                qlist.remove("")

                for(i in qlist){
                   val list =  i.split(",,") as MutableList
                    if(list.size == 0 ) continue
                    /*
                    if(list.size == 1) list[1] = ""
                   if(list.size == 2) list[3] = "0"
                   if(list.size==3) list[2] = "null"
                   if(list.size==4) list[4] = "-1"
                   if(list.size==5) list[5] = "0"
                    if(list.size==5)
                        tmpList = mutableMapOf("_id" to -1L,"question" to list[0],"answer" to list[1], "address" to list[2].toInt(),"second" to "null","count" to list[3],"true_count" to list[4])
                    */
                   // Toast.makeText(applicationContext,qlist.toString(), Toast.LENGTH_SHORT).show()
                    editTextTextMultiLine.setText(qlist.toString())
                    if(list.size==2)
                        tmpList = mutableMapOf("_id" to -1L,"question" to list[0],"answer" to list[1])
                    if(list.size==6)
                        tmpList = mutableMapOf("_id" to -1L,"question" to list[0],"answer" to list[1], "address" to list[2].toInt(),"second" to list[3],"count" to list[4],"count_true" to list[5])


                    editor.insertColumn(tmpList)
                }

            }else{
                Toast.makeText(applicationContext,"error:", Toast.LENGTH_LONG).show()
            }
        }

        switch3.setOnCheckedChangeListener { _, isChecked ->
            for (i in radiolist){
                findViewById<RadioButton>(i).isEnabled = isChecked
            }
        }
        var selected = -1
        settingsAddressRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            deleteButton.isEnabled = true
            for(i in 0 until radiolist.size){
                if(radiolist[i] == checkedId){
                    selected = i
                }
            }
        }
        deleteButton.setOnClickListener {
            editor.deleteColumn(selected,"address")
            deleteButton.isEnabled = false
            Toast.makeText(applicationContext,"address:$selected of data was deleted.", Toast.LENGTH_LONG).show()
        }

    }


    private fun saveData(){
        editKey.writeBoolean(Key.SETTING1,settingSwitch1.isChecked)
        editKey.writeBoolean(Key.SETTING2,settingSwitch2.isChecked)
        editKey.writeBoolean(Key.SETTING3,settingSwitch3.isChecked)
        editKey.writeBoolean(Key.SETTING4,settingSwitch4.isChecked)
        val idList = listOf<Int>(
            firstSortRadioGroup.checkedRadioButtonId,
            secondSortRadioGroup.checkedRadioButtonId,
            lastSortRadioGroup.checkedRadioButtonId,
            firstBl.checkedRadioButtonId,
            secondBl.checkedRadioButtonId,
            lastBl.checkedRadioButtonId)

        val sortList = listOf<List<Int>>(
            first_id_list,
            second_id_list,
            last_id_list,
            first_bl,
            second_bl,
            last_bl)

        val keyList = listOf(
            Key.FIRSTSORTVALUE,
            Key.SECONDSORTVALUE,
            Key.LASTSORTVALUE,
            Key.FIRSTSORTUD,
            Key.SECONDSORTUD,
            Key.LASTSORTUD)

        for(i in 0..5){
            editKey.writeInt(keyList[i],getvalue(idList[i],sortList[i]))
        }
        Toast.makeText(applicationContext, R.string.data_save_toast, Toast.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            saveData()
          //  CustomApplication.selectedAction = ActEnum.Main
            Handler().postDelayed({finish()},10L)
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when(keyCode){
            KeyEvent.KEYCODE_BACK->{
                saveData()
               // CustomApplication.selectedAction = ActEnum.Main
                Handler().postDelayed({finish()},10L)
            }

        }
        return super.onKeyDown(keyCode, event)
    }
    fun getvalue(id:Int,list:List<Int>):Int{
        for(i in 0..list.size-1)
            if(list[i] == id)
                return i
        IllegalArgumentException("なんかしら値が間違ってる")
        return -1
    }

    override fun onDestroy() {
        super.onDestroy()
        editor.closeHelper()

    }
}

