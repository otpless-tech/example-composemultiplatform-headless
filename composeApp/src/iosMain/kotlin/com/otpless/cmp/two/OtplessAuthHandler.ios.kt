package com.otpless.cmp.two

import cocoapods.OtplessBM.Otpless
import cocoapods.OtplessBM.OtplessRequest
import cocoapods.OtplessBM.setOtplessObjcResponseDelegate
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

var job: Job? = null

@OptIn(ExperimentalForeignApi::class)
actual fun initializeOtpless(appId: String, onOtplessResponse: (String) -> Unit, loginUri: String?) {
    Otpless.shared().setOtplessObjcResponseDelegate(otplessResponseDelegate = { response ->
        Otpless.shared().objcCommit(response)
        if (!response.isNullOrBlank()) {
            onOtplessResponse(response)
        }
    })

    Otpless.shared().initialiseWithAppId(appId = appId, vc = MainViewController(), loginUri = null, shouldShowOtplessOneTapUI = true)
}

@OptIn(ExperimentalForeignApi::class)
actual fun start(otplessCMPRequest: OtplessCMPRequest) {
    val otplessRequest = OtplessRequest()

    if (!otplessCMPRequest.phoneNumber.isNullOrBlank() && !otplessCMPRequest.countryCode.isNullOrBlank()) {
        otplessRequest.setWithPhoneNumber(
            phoneNumber = otplessCMPRequest.phoneNumber,
            withCountryCode = otplessCMPRequest.countryCode
        )
    } else if (!otplessCMPRequest.email.isNullOrBlank()) {
        otplessRequest.setWithEmail(
            email = otplessCMPRequest.email
        )
    } else {
        otplessRequest.setWithObjcChannelType(otplessCMPRequest.oAuthChannel ?: "NONE")
    }

    if (!otplessCMPRequest.deliveryChannel.isNullOrBlank()) {
        otplessRequest.setWithDeliveryChannelForTransaction(otplessCMPRequest.deliveryChannel)
    }

    if (!otplessCMPRequest.otpExpiry.isNullOrBlank()) {
        otplessRequest.setWithOtpExpiry(otplessCMPRequest.otpExpiry)
    }

    if (!otplessCMPRequest.otpLength.isNullOrBlank()) {
        otplessRequest.setWithOtpLength(otplessCMPRequest.otpLength)
    }

    val otp = otplessCMPRequest.otp

    if (!otp.isNullOrBlank()) {
        otplessRequest.setWithOtp(otp)
        job = CoroutineScope(Dispatchers.IO).launch {
            Otpless.shared().startWithRequest(otplessRequest = otplessRequest) {
                // Leave blank because response will be received in response delegate
            }
        }
    } else {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            Otpless.shared().startWithRequest(otplessRequest = otplessRequest) {
                // Leave blank because response will be received in response delegate
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
actual fun cleanup() {
    Otpless.shared().cleanup()
}