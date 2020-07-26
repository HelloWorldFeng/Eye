package com.eye.eye.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.eye.eye.EyeApplication

/**
 * 弹出Toast提示。
 *
 * @param duration 显示消息的时间  Either {@link #LENGTH_SHORT} or {@link #LENGTH_LONG}
 */
fun Int.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(EyeApplication.context, this, duration).show()
}

/*
* 解析xml布局
*@param parent 父布局
* @param attachToRoot 是否依附到父布局
*  */
fun Int.inflate(parant: ViewGroup, attachToRoot: Boolean = false):View{
    return LayoutInflater.from(parant.context).inflate(this,parant,attachToRoot)
}
/*
* 获取转换后的时间样式
* */
fun Int.coversionVideoDuration(): String {
    val minute = 1 * 60
    val hour = 60 * minute
    val day = 24 * hour

    return when{
        this < day ->{
            String.format("%02d:%02d",this / minute,this % 60)
        }else ->{
            String.format("%02d:%02d:%02d",this / hour,(this % hour)/minute,this % 60 )
        }
    }
}