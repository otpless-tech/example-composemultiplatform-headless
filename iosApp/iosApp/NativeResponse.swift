//
//  IOSNativeResponseFactory.swift
//  iosApp
//
//  Created by Sparsh on 30/04/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import composeApp
import OtplessBM
import ComposeApp

class IOSNativeResponseFactory: NSObject, NativeResponseFactory, OtplessResponseDelegate {
    var responseCallback: ((String) -> Void)? = nil
    func onResponse(_ response: OtplessResponse) {
        print("Response: \(response)")
        
        // Safely unwrap response.response (empty dictionary if nil)
        let responseData = response.response ?? [:]
        
        // Build dictionary cleanly
        let responseDict: [String: Any] = [
            "response": responseData,
            "responseType": response.responseType.rawValue,
            "statusCode": response.statusCode
        ]
        
        // Convert dictionary to JSON string
        if let jsonData = try? JSONSerialization.data(withJSONObject: responseDict, options: [.prettyPrinted]),
           let jsonString = String(data: jsonData, encoding: .utf8) {
            responseCallback?(jsonString)
        } else {
            responseCallback?("{\"error\": \"Failed to encode response\"}")
        }
    }
    
    func initialize(appId: String, responseCallback: @escaping (String) -> Void) {
        Otpless.shared.initialise(withAppId: appId, vc: getRootViewController()!)
        Otpless.shared.setResponseDelegate(self)
        self.responseCallback = responseCallback
    }
    
    func start(phoneNumber: String, countryCode: String, otp: String) {
        Task {
            let request = OtplessRequest()
            request.set(phoneNumber: phoneNumber, withCountryCode: countryCode)
            if otp.isEmpty == false {
                request.set(otp: otp)
            }
            await Otpless.shared.start(withRequest: request)
        }
    }
}

 func getRootViewController() -> UIViewController? {
        guard let windowScene = UIApplication.shared.connectedScenes
                .first(where: { $0.activationState == .foregroundActive }) as? UIWindowScene,
              let rootVC = windowScene.windows
                .first(where: { $0.isKeyWindow })?.rootViewController else {
            return UIApplication.shared.windows.first?.rootViewController
        }
        return rootVC
    }
