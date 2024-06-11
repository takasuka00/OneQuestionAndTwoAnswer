package com.takasuka.forest2.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.takasuka.forest2.R

class TempActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_temp)
        finish()
    }
}