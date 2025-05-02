package com.otpless.cmp.two

import com.otpless.v2.android.sdk.dto.OtplessChannelType
import com.otpless.v2.android.sdk.dto.OtplessRequest
import com.otpless.v2.android.sdk.dto.OtplessResponse
import com.otpless.v2.android.sdk.main.OtplessSDK
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

var job: Job? = null

actual fun initializeOtpless(appId: String, onOtplessResponse: (String) -> Unit, loginUri: String?) {
    ActivityHolder.currentActivity?.get()?.let {
        OtplessSDK.initialize(appId, it)
        OtplessSDK.setResponseCallback(OtplessResponseHandler::otplessResponseCallback)
        OtplessResponseHandler.setBridgeResponseHandler(onOtplessResponse)
    }
}

actual fun start(otplessCMPRequest: OtplessCMPRequest) {
    val otplessRequest = OtplessRequest()

    if (!otplessCMPRequest.phoneNumber.isNullOrBlank() && !otplessCMPRequest.countryCode.isNullOrBlank()) {
        otplessRequest.setPhoneNumber(
            number = otplessCMPRequest.phoneNumber,
            countryCode = otplessCMPRequest.countryCode
        )
    } else if (!otplessCMPRequest.email.isNullOrBlank()) {
        otplessRequest.setEmail(
            email = otplessCMPRequest.email
        )
    } else {
        otplessRequest.setChannelType(OtplessChannelType.valueOf(otplessCMPRequest.oAuthChannel ?: "NONE"))
    }

    if (!otplessCMPRequest.deliveryChannel.isNullOrBlank()) {
        otplessRequest.setDeliveryChannel(otplessCMPRequest.deliveryChannel)
    }

    if (!otplessCMPRequest.otpExpiry.isNullOrBlank()) {
        otplessRequest.setExpiry(otplessCMPRequest.otpExpiry)
    }

    if (!otplessCMPRequest.otpLength.isNullOrBlank()) {
        otplessRequest.setOtpLength(otplessCMPRequest.otpLength)
    }

    val otp = otplessCMPRequest.otp

    if (!otp.isNullOrBlank()) {
        otplessRequest.setOtp(otp)
        job = CoroutineScope(Dispatchers.IO).launch {
            OtplessSDK.start(request = otplessRequest, OtplessResponseHandler::otplessResponseCallback)
        }
    } else {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            OtplessSDK.start(request = otplessRequest, OtplessResponseHandler::otplessResponseCallback)
        }
    }
}

object OtplessResponseHandler {
    private var bridgeResponseHandler: ((String) -> Unit)? = null
    fun otplessResponseCallback(otplessResponse: OtplessResponse) {
        OtplessSDK.commit(otplessResponse)
        bridgeResponseHandler?.invoke(
            convertOtplessResponseToJsonString(otplessResponse)
        )
    }

    private fun convertOtplessResponseToJsonString(otplessResponse: OtplessResponse): String {
        return """
        {
            "responseType": "${otplessResponse.responseType}",
            "response": ${otplessResponse.response?.toString() ?: "null"},
            "statusCode": ${otplessResponse.statusCode}
        }
    """.trimIndent()
    }

    fun setBridgeResponseHandler(responseHandler: (String) -> Unit) {
        bridgeResponseHandler = responseHandler
    }
}

actual fun cleanup() {
    OtplessSDK.cleanup()
}