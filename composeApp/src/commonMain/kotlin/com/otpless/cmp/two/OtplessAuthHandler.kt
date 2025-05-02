package com.otpless.cmp.two


expect fun initializeOtpless(appId: String, onOtplessResponse: (String) -> Unit, loginUri: String?)

expect fun start(otplessCMPRequest: OtplessCMPRequest)

expect fun cleanup()


data class OtplessCMPRequest(
    val phoneNumber: String?,
    val countryCode: String?,
    val email: String? = null,
    val otp: String? = null,
    val otpExpiry: String? = null,
    val otpLength: String? = null,
    val deliveryChannel: String? =  null,
    val oAuthChannel: String? = null
)