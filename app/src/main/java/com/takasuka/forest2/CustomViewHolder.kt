package com.takasuka.forest2

import android.view.ContextMenu
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.remindlist_layout.view.*

class CustomViewHolder(view: View):RecyclerView.ViewHolder(view),View.OnCreateContextMenuListener {
    var textView :TextView

    var trueButton :RadioButton
    var falseButton :RadioButton
    var view_1 :TextView
    var view_2 :TextView
    var view_3 :TextView
    var radioGroup :RadioGroup
    var testButton : Button
    init{
        this.textView = itemView.textView
        this.trueButton = itemView.trueButton
        this.falseButton = itemView.falseButton
        this.view_1 = itemView.view_1
        this.view_2 = itemView.view_2
        this.view_3 = itemView.view_3
        this.radioGroup = itemView.radioGroup
        this.testButton = itemView.testButton
        view.setOnCreateContextMenuListener(this)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        menu.setHeaderTitle("Select The Action")
        menu.add(0,v.getId(),0,"call")
        menu.add(0,v.getId(),0,"SMS")

    }


}