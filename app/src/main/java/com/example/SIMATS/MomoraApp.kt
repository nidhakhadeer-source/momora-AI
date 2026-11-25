package com.example.SIMATS

import android.app.Application
import com.example.SIMATS.data.local.FcmTokenManager
import com.example.SIMATS.data.local.LocalNotificationHelper
import com.example.SIMATS.data.local.SessionManager

class MomoraApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
        LocalNotificationHelper.init(this)
        FcmTokenManager.init(this)
    }
}

