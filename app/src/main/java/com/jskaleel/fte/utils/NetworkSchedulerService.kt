package com.jskaleel.fte.utils

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.content.IntentFilter
import com.jskaleel.fte.R
import com.jskaleel.fte.model.NetWorkMessage
import com.jskaleel.fte.utils.network.ConnectivityReceiver
import com.jskaleel.fte.utils.network.ConnectivityReceiverListener

class NetworkSchedulerService : JobService(), ConnectivityReceiverListener {

    private lateinit var mConnectivityReceiver: ConnectivityReceiver

    override fun onCreate() {
        super.onCreate()
        mConnectivityReceiver = ConnectivityReceiver(this);
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        try {
            unregisterReceiver(mConnectivityReceiver)
        } catch (e: Exception) {

        }
        return true
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        registerReceiver(mConnectivityReceiver, IntentFilter(Constants.CONNECTIVITY_ACTION))
        return true
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        val message = if (isConnected) getString(R.string.connected_to_internet) else getString(R.string.no_internet)
        RxBus.publish(NetWorkMessage(message))
    }
}