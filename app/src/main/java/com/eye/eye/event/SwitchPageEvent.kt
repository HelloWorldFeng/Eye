package com.eye.eye.event

/*
* EventBus通知Tab页切换界面*/
open class SwitchPageEvent(var activityClass: Class<*>): MessageEvent() {
}