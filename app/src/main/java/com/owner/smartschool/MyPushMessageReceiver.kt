package com.owner.smartschool

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import cn.bmob.push.PushConstants
import org.json.JSONException
import org.json.JSONObject

/**
 * 消息接收器
 */
class MyPushMessageReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(PushConstants.ACTION_MESSAGE)) {
            //收到广播时，发送一个通知
            val jsonStr = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING)
            var content = null
            try {
                val jsonObject = JSONObject(jsonStr)
                content = jsonObject.getString("alert") as Nothing?
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

    }
}