package com.example.dailynotifications.core

import android.app.Application
import com.example.dailynotifications.core.notifications.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DailyNotificationsApp : Application() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        notificationHelper.ensureChannel()
    }
}
