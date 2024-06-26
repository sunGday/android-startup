package com.rousetime.sample.startup

import android.content.Context
import com.rousetime.android_startup.AndroidStartup

/**
 * Created by idisfkj on 2020/8/18.
 * Email : idisfkj@gmail.com.
 */
class SampleAsyncSixStartup: AndroidStartup<String>() {

    override fun path(): String {
        return "SampleAsyncSixStartup"
    }

    override fun callCreateOnMainThread(): Boolean = false

    override fun create(context: Context): String? {
        Thread.sleep(2000)
        return "async six"
    }

    override fun waitOnMainThread(): Boolean = false

}