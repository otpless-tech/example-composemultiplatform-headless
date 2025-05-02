package com.otpless.cmp.two

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val keyboardController = LocalSoftwareKeyboardController.current

        var phoneNumber by remember { mutableStateOf("") }
        var otp by remember { mutableStateOf("") }
        var response by remember {
            mutableStateOf("")
        }


        /**
         * In your LoginScreen.kt, initialize the OtplessSDK in LaunchedEffect so that it is not initialized again on recomposition.
         *
         * */
        DisposableEffect(key1 = Unit) {
            initializeOtpless(
                appId = "OD6F3SJGCP93605DA5OM",
                onOtplessResponse = { otplessResponseString ->
                    response = otplessResponseString + "\n\n" + response
                    handleOtplessResponse(otplessResponseString, onOTPAutoRead = {
                        otp = it
                    })
                },
                loginUri = null
            )

            onDispose {
                cleanup()
            }
        }

        AuthScreen(
            phoneNumber = phoneNumber,
            otp = otp,
            onPhoneNumberUpdate = {
                phoneNumber = it
            },
            onOtpUpdate = {
                otp = it
            },
            otplessResponse = response,
            onStart = {
                keyboardController?.hide()
                start(it)
            }
        )
    }
}

fun handleOtplessResponse(responseJsonString: String, onOTPAutoRead: (String) -> Unit) {
    val parsedElement = Json.parseToJsonElement(responseJsonString).jsonObject

    val responseType = parsedElement["responseType"]?.jsonPrimitive?.contentOrNull
    val statusCode = parsedElement["statusCode"]?.jsonPrimitive?.intOrNull
    val responseJsonObject = parsedElement["response"]?.jsonObject

    when (responseType) {
        "SDK_READY" -> {
            // SDK initialized successfully, enable continue button
            AppLogger.d("SDK is ready!")
        }

        "FAILED" -> {
            AppLogger.d("SDK initialization failed!")
            if (statusCode == 5003) {
                // SDK initialization failed, retry initializing
            } else {
                // General failure handling
            }
        }

        "INITIATE" -> {
            if (statusCode != 200) {
                if (getPlatformName().lowercase() == "android") {
                    handleInitiateErrorAndroid(responseJsonObject)
                } else {
                    handleInitiateErrorIos(responseJsonObject)
                }
            } else {
                val authType = responseJsonObject?.get("authType")?.jsonPrimitive?.contentOrNull
                when (authType) {
                    "OTP" -> {
                        AppLogger.d("Authentication started using OTP")
                        // Take user to OTP verification screen
                    }

                    "SILENT_AUTH" -> {
                        AppLogger.d("Authentication started using Silent Auth")
                        // Handle Silent Authentication initiation (show loading)
                    }
                }
            }
        }

        "OTP_AUTO_READ" -> {
            // Only applicable in Android
            val otp = responseJsonObject?.get("otp")?.jsonPrimitive?.contentOrNull
            if (!otp.isNullOrBlank()) {
                // Autofill OTP in your text field
                onOTPAutoRead(otp)
            }
        }

        "VERIFY" -> {
            val authType = responseJsonObject?.get("authType")?.jsonPrimitive?.contentOrNull
            if (authType == "SILENT_AUTH") {
                if (statusCode == 9106) {
                    // Silent Auth + fallback failed → gracefully exit auth flow
                    AppLogger.d("SNA + fallback failed!")
                } else {
                    AppLogger.d("SNA failed, trying fallback!")
                }
            } else {
                if (getPlatformName().lowercase() == "android") {
                    handleVerifyErrorAndroid(responseJsonObject)
                } else {
                    handleInitiateErrorIos(responseJsonObject)
                }
            }
        }

        "DELIVERY_STATUS" -> {
            val authType = responseJsonObject?.get("authType")?.jsonPrimitive?.contentOrNull
            val deliveryChannel = responseJsonObject?.get("deliveryChannel")?.jsonPrimitive?.contentOrNull
            // Handle delivery status (authType, deliveryChannel)
            AppLogger.d("DELIVERY_STATUS: authType: $authType \b deliveryChannel: $deliveryChannel")
        }

        "FALLBACK_TRIGGERED" -> {
            val newDeliveryChannel = responseJsonObject?.get("deliveryChannel")?.jsonPrimitive?.contentOrNull
            // Handle fallback deliveryChannel
            AppLogger.d("Fallback triggered, new delivery channel: $newDeliveryChannel")
        }

        "ONETAP" -> {
            val data = responseJsonObject?.get("data")?.jsonObject
            val token = data?.get("token")?.jsonPrimitive?.contentOrNull
            if (!token.isNullOrBlank()) {
                // Process token and proceed
                AppLogger.d("Token: $token")
            }
        }
    }
}

private fun handleInitiateErrorAndroid(responseJsonObject: JsonObject?) {
    val errorCode = responseJsonObject?.get("errorCode")?.jsonPrimitive?.contentOrNull
    val errorMessage = responseJsonObject?.get("errorMessage")?.jsonPrimitive?.contentOrNull

    when (errorCode) {
        "7101" -> AppLogger.d("Android OTPless Error: $errorMessage")
        "7102" -> AppLogger.d("Android OTPless Error: $errorMessage")
        "7103" -> AppLogger.d("Android OTPless Error: $errorMessage")
        "7104" -> AppLogger.d("Android OTPless Error: $errorMessage")
        "7105" -> AppLogger.d("Android OTPless Error: $errorMessage")
        "7106" -> AppLogger.d("Android OTPless Error: $errorMessage")
        "7113" -> AppLogger.d("Android OTPless Error: $errorMessage")
        "7116" -> AppLogger.d("Android OTPless Error: $errorMessage")
        "7121" -> AppLogger.d("Android OTPless Error: $errorMessage")
        "4000" -> AppLogger.d("Android OTPless Error: $errorMessage")
        "4001" -> AppLogger.d("Android OTPless Error: $errorMessage")
        "4003" -> AppLogger.d("Android OTPless Error: $errorMessage")
        "401", "7025" -> AppLogger.d("Android OTPless Error: $errorMessage")
        "7020", "7022", "7023", "7024" -> AppLogger.d("Android OTPless Error: $errorMessage")
        "9002", "9003", "9004", "9005", "9006", "9007", "9008", "9009" -> AppLogger.d("Android OTPless Error: $errorMessage")
        "500" -> AppLogger.d("Android OTPless Error: $errorMessage")
        "9100", "9104", "9103" -> AppLogger.d("Android OTPless Error: $errorMessage")
    }
}

private fun handleVerifyErrorAndroid(responseJsonObject: JsonObject?) {
    val errorCode = responseJsonObject?.get("errorCode")?.jsonPrimitive?.contentOrNull
    val errorMessage = responseJsonObject?.get("errorMessage")?.jsonPrimitive?.contentOrNull

    when (errorCode) {
        "7112" -> {
            // Handle request error: Empty OTP
            AppLogger.d("Android OTPless Error: $errorCode \b $errorMessage")
        }

        "7115" -> {
            // Handle request error: OTP is already verified
            AppLogger.d("Android OTPless Error: $errorCode \b $errorMessage")
        }

        "7118" -> {
            // Handle request error: Incorrect OTP
            AppLogger.d("Android OTPless Error:$errorCode \b  $errorMessage")
        }

        "7303" -> {
            // Handle request error: OTP expired
            AppLogger.d("Android OTPless Error: $errorCode \b $errorMessage")
        }

        "4000" -> {
            // Handle invalid request
            AppLogger.d("Android OTPless Error:$errorCode \b  $errorMessage")
        }

        "9100" -> {
            // Handle network error: Socket timeout exception
            AppLogger.d("Android OTPless Error: $errorCode \b $errorMessage")
        }

        "9104" -> {
            // Handle network error: IO Exception occurred
            AppLogger.d("Android OTPless Error:$errorCode \b  $errorMessage")
        }

        "9103" -> {
            // Handle network error: Unknown Host Exception
            AppLogger.d("Android OTPless Error: $errorCode \b $errorMessage")
        }

        else -> {
            // Handle unknown error
            AppLogger.d("Android OTPless Error:Unknown error \b  $errorMessage")
        }
    }
}

private fun handleInitiateErrorIos(responseJsonObject: JsonObject?) {
    val errorCode = responseJsonObject?.get("errorCode")?.jsonPrimitive?.contentOrNull
    val errorMessage = responseJsonObject?.get("errorMessage")?.jsonPrimitive?.contentOrNull ?: "Unknown error"

    when (errorCode) {
        "7101" -> AppLogger.d("iOS OTPless Error: Invalid parameters values or missing parameters - $errorMessage")
        "7102" -> AppLogger.d("iOS OTPless Error: Invalid phone number - $errorMessage")
        "7103" -> AppLogger.d("iOS OTPless Error: Invalid phone number delivery channel - $errorMessage")
        "7104" -> AppLogger.d("iOS OTPless Error: Invalid email - $errorMessage")
        "7105" -> AppLogger.d("iOS OTPless Error: Invalid email channel - $errorMessage")
        "7106" -> AppLogger.d("iOS OTPless Error: Invalid phone number or email - $errorMessage")
        "7113" -> AppLogger.d("iOS OTPless Error: Invalid expiry - $errorMessage")
        "7116" -> AppLogger.d("iOS OTPless Error: OTP Length is invalid (4 or 6 only allowed) - $errorMessage")
        "7121" -> AppLogger.d("iOS OTPless Error: Invalid app hash - $errorMessage")
        "4000" -> AppLogger.d("iOS OTPless Error: Invalid request values - $errorMessage")
        "4001" -> AppLogger.d("iOS OTPless Error: Unsupported 2FA request - $errorMessage")
        "4003" -> AppLogger.d("iOS OTPless Error: Incorrect request channel - $errorMessage")
        "401", "7025" -> AppLogger.d("iOS OTPless Error: Unauthorized request or country not enabled - $errorMessage")
        "7020", "7022", "7023", "7024" -> AppLogger.d("iOS OTPless Error: Rate limiting error (Too many requests) - $errorMessage")

        // Feature not supported on older iOS versions
        "5900" -> AppLogger.d("iOS OTPless Error: The feature is not supported because it requires a newer iOS version - ${errorMessage.ifBlank { "The requested feature is only available on newer iOS versions." }}")

        // Internet-related errors
        "9100" -> AppLogger.d("iOS OTPless Error: Request Timeout - ${errorMessage.ifBlank { "The request took too long to complete and was aborted." }}")
        "9101" -> AppLogger.d("iOS OTPless Error: Network Connection Was Lost - ${errorMessage.ifBlank { "The connection was interrupted before the request could complete." }}")
        "9102" -> AppLogger.d("iOS OTPless Error: DNS Lookup Failed - ${errorMessage.ifBlank { "The domain name could not be resolved, possibly due to network issues." }}")
        "9103" -> AppLogger.d("iOS OTPless Error: Cannot Connect to Server - ${errorMessage.ifBlank { "The server is unreachable, possibly due to downtime or incorrect configurations." }}")
        "9104" -> AppLogger.d("iOS OTPless Error: No Internet Connection - ${errorMessage.ifBlank { "The device is not connected to the internet." }}")
        "9105" -> AppLogger.d("iOS OTPless Error: Secure Connection Failed - ${errorMessage.ifBlank { "A secure connection could not be established due to SSL/TLS issues." }}")
        "9110" -> AppLogger.d("iOS OTPless Error: Otpless Authentication Request Cancelled - ${errorMessage.ifBlank { "The authentication request was manually canceled by the user or the system." }}")

        else -> AppLogger.d("iOS OTPless Error: $errorMessage")
    }
}

private fun handleVerifyErrorIos(responseJsonObject: JsonObject) {
    val errorCode = responseJsonObject["errorCode"]?.jsonPrimitive?.contentOrNull
    val errorMessage = responseJsonObject["errorMessage"]?.jsonPrimitive?.contentOrNull ?: "Unknown error"

    when (errorCode) {
        "7112" -> AppLogger.d("iOS OTPless Error: OTP is empty - $errorMessage")
        "7115" -> AppLogger.d("iOS OTPless Error: OTP already verified - $errorMessage")
        "7118" -> AppLogger.d("iOS OTPless Error: Incorrect OTP - $errorMessage")
        "7303" -> AppLogger.d("iOS OTPless Error: OTP expired - $errorMessage")
        "4000" -> AppLogger.d("iOS OTPless Error: Invalid request values - $errorMessage")

        // Internet-related errors
        "9100" -> AppLogger.d("iOS OTPless Error: Request Timeout - ${errorMessage.ifBlank { "The request took too long to complete and was aborted." }}")
        "9101" -> AppLogger.d("iOS OTPless Error: Network Connection Was Lost - ${errorMessage.ifBlank { "The connection was interrupted before the request could complete." }}")
        "9102" -> AppLogger.d("iOS OTPless Error: DNS Lookup Failed - ${errorMessage.ifBlank { "The domain name could not be resolved, possibly due to network issues." }}")
        "9103" -> AppLogger.d("iOS OTPless Error: Cannot Connect to Server - ${errorMessage.ifBlank { "The server is unreachable, possibly due to downtime or incorrect configurations." }}")
        "9104" -> AppLogger.d("iOS OTPless Error: No Internet Connection - ${errorMessage.ifBlank { "The device is not connected to the internet." }}")
        "9105" -> AppLogger.d("iOS OTPless Error: Secure Connection Failed - ${errorMessage.ifBlank { "A secure connection could not be established due to SSL/TLS issues." }}")
        "9110" -> AppLogger.d("iOS OTPless Error: Otpless Authentication Request Cancelled - ${errorMessage.ifBlank { "The authentication request was manually canceled by the user or the system." }}")

        else -> AppLogger.d("iOS OTPless Error: $errorMessage")
    }
}


