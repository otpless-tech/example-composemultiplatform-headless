package com.otpless.cmp.two

import platform.Foundation.NSLog

actual object AppLogger {
    val tag = "OtplessCMP"
    actual fun e(message: String, throwable: Throwable?) {

        if (throwable != null) {
            NSLog("ERROR: [$tag] $message. Throwable: $throwable CAUSE ${throwable.cause}")
        } else {
            NSLog("ERROR: [$tag] $message")
        }
    }

    actual fun d(message: String) {
        NSLog("DEBUG: [$tag] $message")
    }

    actual fun i(message: String) {
        NSLog("INFO: [$tag] $message")
    }

}