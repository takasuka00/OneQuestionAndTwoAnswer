package com.takasuka.forest2.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.takasuka.availabefiles.data.SQLite.DatabaseEditor
import com.takasuka.everydaytask.EditKey
import com.takasuka.everydaytask.Key
import com.takasuka.forest2.CustomApplication
import com.takasuka.forest2.CustomApplication.Companion.allDataSorted
import com.takasuka.forest2.CustomApplication.Companion.dataAddressList
import com.takasuka.forest2.CustomApplication.Companion.selectedAction
import com.takasuka.forest2.Key.ActEnum
import com.takasuka.forest2.R
import kotlinx.android.synthetic.main.activity_edit.*

@Suppress("DEPRECATION")
class EditActivity() : AppCompatActivity() {
    private lateinit var editor:DatabaseEditor
    private lateinit var data:Map<String,Any?>
    private var selectedId :Long = -1L
    private lateinit var radioId :ArrayList<Int>
    private lateinit var editKey :EditKey
    private var position :Int = -1
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        editor =   DatabaseEditor(applicationContext)
        editKey = EditKey(applicationContext)
        radioId = arrayListOf<Int>(
            R.id.radioButton01,
            R.id.radioButton02,
            R.id.radioButton03,
            R.id.radioButton04,
            R.id.radioButton05
            )
        val intents = intent
        selectedId = intents.getLongExtra("key",-1L)
        position = intents.getIntExtra("position",-1)
        val maxId =  editor.getMaxId().toInt()
        view_1.text = "id:${(maxId+1)}"
        if(selectedId!=-1L){
            val tmp = editor.getData(selectedId)
            if(tmp!= null)
                data = tmp
            questionEditText.setText(data["question"] as String)
            answerEditText.setText(data["answer"] as String)

            if(data["second"].toString() != "null" && data["second"].toString()!= ""){
                secondAnswerEditText.setText(data["second"] as String)
                switch2.isChecked = true
            }
            view_1.text = "id:" + data["_id"]
            addressRadioGroup.check(radioId[data["address"].toString().toInt()])
        }
        else {
            view_1.text = "id:" + (maxId + 1).toString()
            addressRadioGroup.check(radioId[editKey.readInt(Key.ADDRESSCONECTOR)])
        }

        addressRadioGroup.setOnCheckedChangeListener{_,_->
            if(selectedId!=-1L) {
                selectedAction = ActEnum.EditAddressChange
            }
        }
        if(switch2.isChecked){
            switch2.setTextColor(Color.rgb(0,0,0))
            secondAnswerEditText.isEnabled = true
        }else{
            switch2.setTextColor(Color.rgb(150,150,150))
            secondAnswerEditText.isEnabled = false
        }
        switch2.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                switch2.setTextColor(Color.rgb(0,0,0))
                secondAnswerEditText.isEnabled = true
            }else{
                switch2.setTextColor(Color.rgb(150,150,150))
                secondAnswerEditText.isEnabled = false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            back()
        }else if( item.itemId == R.id.context_action_delete){
            selectedAction = ActEnum.Main
            if(selectedId != -1L){
                editor.deleteColumn(selectedId)

                if(position != -1)
                    allDataSorted.removeAt(position)

             }
            Handler().postDelayed({finish()},30L)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_context, menu)
        return true
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when(keyCode){
            KeyEvent.KEYCODE_BACK->{
                back()
                return false
            }

        }
        return super.onKeyDown(keyCode, event)
    }

    private fun back(){
        saveData(questionEditText.text.toString(), answerEditText.text.toString())
        // selectedAction = ActEnum.Main
        Handler().postDelayed({finish()},10L)
    }
    private fun saveData(
        question :String
        ,answer:String
    ){
        if(!question.isBlank()){
            var saveData = mutableMapOf<String,Any?>()
        if(selectedId == -1L) {
            saveData["_id"] = -1L
            CustomApplication.selectedId = editor.getMaxId()+1L
        }else {
            saveData = data as MutableMap
            CustomApplication.selectedId = this.selectedId
        }
            saveData["question"] = question
            saveData["answer"] = answer
            if(switch2.isChecked && secondAnswerEditText.text.toString() != "")
                saveData["second"] = secondAnswerEditText.text
            else
                saveData["second"] = "null"
            val selectedRadioId = addressRadioGroup.checkedRadioButtonId
            for(i in 0 until radioId.size){
                if(radioId[i] == selectedRadioId) {
                    saveData["address"] = i
                    editKey.writeInt(Key.ADDRESSCONECTOR,i)
                    editor.insertColumn(saveData)
                    val tmpData =editor.getData(CustomApplication.selectedId)!!
                    if(selectedAction == ActEnum.EditAddressChange &&selectedId != -1L) {
                       for(k in dataAddressList)
                            k.remove(tmpData["_id"].toString().toLong())
                        allDataSorted[position] = tmpData


                    }else if(selectedId == -1L){
                        allDataSorted.add(tmpData)
                        selectedAction = ActEnum.NewCard
                    }else{
                        allDataSorted[position] = tmpData

                    }
                    dataAddressList[i][tmpData["_id"].toString().toLong()] = tmpData
                }
            }


        }
    }
    override fun onDestroy() {
        editor.closeHelper()
        super.onDestroy()
    }


}