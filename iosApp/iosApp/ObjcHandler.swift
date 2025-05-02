//
//  ObjcHandler.swift
//  iosApp
//
//  Created by Sparsh on 01/05/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import composeApp
import Foundation
import ComposeApp

@objc public final class MyObjcHandler: NSObject {
    @MainActor @objc static func provideDep() {
        let nativeResponseFactory = IOSNativeResponseFactory()
        OtplessAuthHandler_iosArm64Kt.setNativeResponseFactory(factory: nativeResponseFactory)
    }
}
