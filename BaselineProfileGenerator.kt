package com.pearldrift.handsets

import androidx.benchmark.macro.ExperimentalBaselineProfilesApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import org.junit.Rule
import org.junit.Test


class BaselineProfileGenerator {
    @OptIn(ExperimentalBaselineProfilesApi::class)
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @OptIn(ExperimentalBaselineProfilesApi::class)
    @Test
    fun startup() =
        baselineProfileRule.collectBaselineProfile(packageName = "com.pearldrift.bas") {
            pressHome()
            // This block defines the app's critical user journey. Here we are interested in
            // optimizing for app startup. But you can also navigate and scroll
            // through your most important UI.
            startActivityAndWait()
        }
}