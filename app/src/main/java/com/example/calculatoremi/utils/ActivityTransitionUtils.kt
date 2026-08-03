package com.example.calculatoremi.utils

import android.app.Activity
import android.os.Build
import com.example.calculatoremi.R

object ActivityTransitionUtils {

    @Suppress("DEPRECATION")
    fun applySlideInTransition(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activity.overrideActivityTransition(
                Activity.OVERRIDE_TRANSITION_OPEN,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        } else {
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    @Suppress("DEPRECATION")
    fun applySlideOutTransition(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activity.overrideActivityTransition(
                Activity.OVERRIDE_TRANSITION_CLOSE,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
        } else {
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}
