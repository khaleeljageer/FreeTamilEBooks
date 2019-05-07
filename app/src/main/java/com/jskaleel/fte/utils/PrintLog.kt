package com.jskaleel.fte.utils

import android.util.Log
import com.jskaleel.fte.BuildConfig

class PrintLog {
    companion object {
        fun info(log: String) {
            if(!BuildConfig.DEBUG) {
                return
            }
            if(log.length > 4000) {
                Log.i("FTELog", log.substring(0, 4000))
                info(log.substring(4000))
            }else {
                Log.i("FTELog", log)
            }
        }
    }
}