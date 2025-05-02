package com.otpless.cmp.two

expect object AppLogger {
    fun e(message: String, throwable: Throwable? = null)
    fun d(message: String)
    fun i(message: String)

}