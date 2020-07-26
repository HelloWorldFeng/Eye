package com.eye.eye.event

/*
* EventBus通知刷新界面消息
* */
open class RefreshEvent(var activityClass:Class<*> ?= null): MessageEvent() {
}