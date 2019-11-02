package com.owner.smartschool

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import cn.bmob.push.PushConstants

/**
 * 消息接收器
 */
class MyPushMessageReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(PushConstants.ACTION_MESSAGE)) {
            Log.d("bmob", "客户端接收推送内容" + intent.getStringExtra("msg"))
        }
    }
}