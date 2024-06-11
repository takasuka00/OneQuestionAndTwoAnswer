
package com.takasuka.forest2

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.takasuka.availabefiles.data.SQLite.DatabaseEditor
import com.takasuka.everydaytask.EditKey
import com.takasuka.everydaytask.Key
import com.takasuka.forest2.CustomApplication.Companion.TFCountor
import com.takasuka.forest2.CustomApplication.Companion.allDataSorted
import com.takasuka.forest2.CustomApplication.Companion.dataAddressList
import com.takasuka.forest2.CustomApplication.Companion.selectedAction
import com.takasuka.forest2.CustomApplication.Companion.selectedBl
import com.takasuka.forest2.Key.ActEnum
import com.takasuka.forest2.activity.EditActivity
import com.takasuka.forest2.activity.MainActivity
import kotlinx.android.synthetic.main.remindlist_layout.view.*
import java.lang.StringBuilder
import java.util.regex.Pattern

class CustomRecyclerViewAdapter (private val allData:List<Map<String,Any?>>, private val editor: DatabaseEditor, private val editKey: EditKey): RecyclerView.Adapter<CustomViewHolder>() {
    lateinit var view: View
    val p = Pattern.compile(".*-{2,}.*")
    val s = Pattern.compile("-{2,}").toRegex()
    val t = Pattern.compile("\n").toRegex()
    lateinit var radioList :List<Int>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        view = inflater.inflate(R.layout.remindlist_layout,parent,false)
        radioList = listOf(
                R.id.radioButton000,
                R.id.radioButton001,
                R.id.radioButton002,
                R.id.radioButton003,
                R.id.radioButton004,
                )
        if(selectedAction==ActEnum.Delete) {
            view.testButton.visibility = View.VISIBLE
            view.radioGroup.visibility = View.INVISIBLE
        }else if(selectedAction == ActEnum.AddressChange){
            view.layoutRadioGroup.visibility = View.VISIBLE
            view.radioGroup.visibility = View.GONE
        }
        return CustomViewHolder(view)

    }

    override fun getItemCount(): Int {
        return if(allData.size<200) allData.size else 200
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val data = allData[position]
        val question = data["question"].toString()
        var answer = data["answer"].toString()
        var setTextile :Boolean = selectedBl
        var secondflag :Boolean = true
        val id = data["_id"].toString().toLong()
        var countTrue = data["count_true"].toString().toInt()
        var count = data["count"].toString().toInt()


        if(p.matcher(question).find()){
            val qlist = question.split(s)
            val alist = answer.split(t) as MutableList
            alist.remove("")
            val sb = StringBuilder()

            if(qlist.size - alist.size >1){
                for(i in 0..qlist.size-alist.size-2){
                   alist.add("---")
                }
            }
            for(i in 0..qlist.size-2){
                sb.append(qlist[i]).append(alist[i])
            }
            sb.append(qlist[qlist.size-1])
            answer = sb.toString()
        }



        //スイッチでこれの逆転をする
        if(!setTextile) {
            holder.itemView.textView.text = question
            holder.itemView.setBackgroundColor(Color.rgb(255, 255, 255))
        }else {
            holder.itemView.textView.text = answer
            holder.itemView.setBackgroundColor(Color.rgb(244, 244, 244))
        }

        if(editKey.readBoolean(Key.SETTING1)) {
            holder.itemView.view_1.text =  data["count_true"].toString() + "/" + data["count"].toString()
            if(editKey.readBoolean(Key.SETTING2)) {
                holder.itemView.view_2.text = "id:" + data["_id"]
                if(editKey.readBoolean((Key.SETTING3)))
                    holder.itemView.view_3.text ="ad:" + data["address"].toString()
            }else{
                if(editKey.readBoolean((Key.SETTING3)))
                    holder.itemView.view_2.text =  "ad:" + data["address"].toString()
            }
        }else{
            if(editKey.readBoolean(Key.SETTING2)) {
                holder.itemView.view_1.text = "id:" + data["_id"]
                if(editKey.readBoolean((Key.SETTING3)))
                    holder.itemView.view_2.text ="ad:" + data["address"].toString()
            }else{
                if(editKey.readBoolean((Key.SETTING3)))
                    holder.itemView.view_1.text = "ad:" + data["address"].toString()
            }

        }



        holder.itemView.setOnClickListener{
                if (setTextile) {
                    holder.itemView.textView.text =question
                    holder.itemView.setBackgroundColor(Color.rgb(255, 255, 255))
                    setTextile = !setTextile
                } else {
                    if(data["second"] != null && data["second"].toString() !="null") {
                        if(secondflag) {
                            holder.itemView.textView.text = answer
                            holder.itemView.setBackgroundColor(Color.rgb(248, 248, 248))
                        }else{
                            holder.itemView.textView.text = data["second"].toString()
                            holder.itemView.setBackgroundColor(Color.rgb(238, 238, 238))
                            setTextile = !setTextile
                        }
                        secondflag = !secondflag
                    }else{
                        holder.itemView.textView.text = answer
                        holder.itemView.setBackgroundColor(Color.rgb(244, 244, 244))
                        setTextile = !setTextile
                    }


                }



        }
        holder.itemView.radioGroup.setOnCheckedChangeListener { _, checkedId ->

            editKey.writeInt(Key.TODAYCOUNTOR,editKey.readInt(Key.TODAYCOUNTOR)+1)
            editKey.writeInt(Key.ALLDAYCOUNTOR,editKey.readInt(Key.ALLDAYCOUNTOR)+1)

            if(checkedId == R.id.trueButton ) {
                    TFCountor = Pair(TFCountor.first+1, TFCountor.second+1)
                    if(editKey.readBoolean(Key.SETTING1))
                        holder.itemView.view_1.text = "${++countTrue}/${++count}"
                  //  editor.addCount(id, listOf("count","count_true"), listOf(1L,1L))
                    allDataSorted[position]["count"] =count
                    allDataSorted[position]["count_true"] = countTrue
                    // flag= true
                holder.itemView.trueButton.isChecked = false
            }
            if(checkedId == R.id.falseButton) {
                TFCountor = Pair(TFCountor.first, TFCountor.second+1)
                if(editKey.readBoolean(Key.SETTING1))
                    holder.itemView.view_1.text = "${countTrue}/${++count}"
                allDataSorted[position]["count"] = count
                   // flag= true
                holder.itemView.falseButton.isChecked = false
            }

        }
        holder.itemView.testButton.setOnClickListener{

            editor.deleteColumn(id)
            holder.itemView.testButton.text = "deleted"
            holder.itemView.testButton.isEnabled = false
            allDataSorted.removeAt(position)
        }
        holder.itemView.setOnLongClickListener {
            selectedAction = ActEnum.Edit
            val intent = Intent(view.context, EditActivity()::class.java)  //data
            intent.putExtra("key",id)
            intent.putExtra("position",position)
            view.context?.startActivity(intent)
            return@setOnLongClickListener true
        }

        if(selectedAction == ActEnum.AddressChange) {
            val k =  data["address"].toString().toInt()
            holder.itemView.layoutRadioGroup.check(radioList[k])
            holder.itemView.layoutRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                for (i in 0 until radioList.size){
                    if(radioList[i] == checkedId){

                        allDataSorted[position]["address"] = i
                        val data = allDataSorted[position]
                        val id = data["_id"].toString().toLong()
                        dataAddressList[i][id] = data
                        dataAddressList[k].remove(id)
                    }
                }

            }
        }
    }

}