package com.eye.eye.extension

import android.widget.Toast
import com.eye.eye.EyeApplication
import com.eyepetizer.android.ui.common.ui.vassonic.SonicRuntimeImpl
import com.tencent.sonic.sdk.SonicConfig
import com.tencent.sonic.sdk.SonicEngine
import com.tencent.sonic.sdk.SonicSessionConfig


/*
* 弹出Toast提示
* */
fun CharSequence.showToas(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(EyeApplication.context,this,duration).show()
}

/*
* VasSonic预加载session
* @param CharSequence预加载url
* */
fun CharSequence.preCreateSession(): Boolean {
    if (!SonicEngine.isGetInstanceAllowed()) {
        SonicEngine.createInstance(SonicRuntimeImpl(EyeApplication.context), SonicConfig.Builder().build())
    }
    val sessionConfigBuilder = SonicSessionConfig.Builder().apply { setSupportLocalServer(true) }
    val preloadSuccess = SonicEngine.getInstance().preCreateSession(this.toString(),sessionConfigBuilder.build())
    return preloadSuccess
}