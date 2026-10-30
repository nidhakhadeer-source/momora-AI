package com.example.SIMATS

import android.app.Application
import com.example.SIMATS.data.local.SessionManager

class MomoraApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
    }
}

