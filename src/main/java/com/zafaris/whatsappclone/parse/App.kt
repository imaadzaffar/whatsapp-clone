package com.zafaris.whatsappclone.parse

import android.app.Application
import com.parse.Parse

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("05Az0Dc102ku8w1xTgxj0lauW5EVMRO5Szf49N05")
                .clientKey("7oZ9Qvh97IUKeHNNKUEfEnhGRcBSYTu4UvXoKNkA")
                .server("https://parseapi.back4app.com/")
                .build()
        )
    }
}