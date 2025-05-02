package com.otpless.cmp.two


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthScreen(
    phoneNumber: String,
    onPhoneNumberUpdate: (String) -> Unit,
    otp: String,
    onOtpUpdate: (String) -> Unit,
    otplessResponse: String,
    onStart: (OtplessCMPRequest) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        RequestParamsTextFields(
            phoneNumber = phoneNumber,
            onPhoneNumberUpdate = onPhoneNumberUpdate,
            otp = otp,
            onOtpUpdate = onOtpUpdate
        )

        Button(
            onClick = {
                onStart(
                    OtplessCMPRequest(
                        phoneNumber = phoneNumber.ifBlank { null },
                        countryCode = "91",
                        otp = otp.ifBlank { null },
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(10.dp)),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Blue,
            )
        ) {
            Text("Start", color = Color.White)
        }

        Text(
            text = "Response:",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = otplessResponse,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun RequestParamsTextFields(
    phoneNumber: String,
    onPhoneNumberUpdate: (String) -> Unit,
    otp: String,
    onOtpUpdate: (String) -> Unit,
) {
    OtplessTextField(
        value = phoneNumber,
        onValueChange = onPhoneNumberUpdate,
        keyboardType = KeyboardType.Number,
        hint = "Phone Number"
    )

    OtplessTextField(
        value = otp,
        onValueChange = onOtpUpdate,
        keyboardType = KeyboardType.Number,
        hint = "OTP"
    )
}

@Composable
fun OtplessTextField(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    hint: String
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(hint) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White
        )
    )
}
