package com.otpless.cmp.two

import android.app.Activity
import java.lang.ref.WeakReference

object ActivityHolder {
    var currentActivity: WeakReference<Activity>? = null
}
