package com.otpless.cmp.two

import android.util.Log

actual object AppLogger {

    val tag = "OtplessCMP"
    actual fun e(message: String, throwable: Throwable?) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }

    actual fun d(message: String) {
        Log.d(tag, message)
    }

    actual fun i(message: String) {
        Log.i(tag, message)
    }


}