package de.fhe.ai.colivingpilot.core

import android.app.Application
import android.content.Context
import android.util.Log
import de.fhe.ai.colivingpilot.network.RetrofitClient
import de.fhe.ai.colivingpilot.storage.Repository
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CoLiPiApplication : Application() {

    lateinit var keyValueStore: KeyValueStore
    lateinit var repository: Repository

    override fun onCreate() {
        super.onCreate()
        instance = this
        keyValueStore = KeyValueStore(this)
        repository = Repository()
        RetrofitClient.initialize(keyValueStore)
        Log.i(LOG_TAG, "Application initialized.")
    }

    companion object {

        const val LOG_TAG = "CoLiPi"

        lateinit var instance: CoLiPiApplication
            private set

        fun applicationContext() : Context {
            return instance.applicationContext
        }

    }

}