package com.eye.eye.extension

import android.graphics.drawable.Drawable
import android.widget.TextView

/*
* 设置TextView图标
* @param drawable 图标
* @param iconWidth 图标宽dp：默认自动根据图标大小
* @param iconHeight 图标高dp：默认自动根据图标大小
* @param direction 图标方向，0左，1上，2右，3下 默认图标位于左侧*/
fun TextView.setDrawable(drawable: Drawable?,iconWidth:Float ?= null,iconHeight:Float ?= null,direction:Int = 0){
    if (iconWidth != null && iconHeight != null) {
        //第一个0是距左边距离，第二个0是距上边距离，iconWidth,iconHeight分别是长宽
        drawable?.setBounds(0,0,dp2px(iconWidth),dp2px(iconHeight))
    }
    when (direction) {
        0 -> setCompoundDrawables(drawable,null,null,null)
        1 -> setCompoundDrawables(null, drawable, null, null)
        2 -> setCompoundDrawables(null, null, drawable, null)
        3 -> setCompoundDrawables(null, null, null, drawable)
        else -> throw NoSuchMethodError()
    }
}