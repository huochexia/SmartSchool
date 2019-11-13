package com.owner.smartschool

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
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
            var content = ""
            try {
                val jsonObject = JSONObject(jsonStr)
                content = jsonObject.getString("alert")
                postNotification(context, content = content)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private fun postNotification(context: Context, content: String) {
        val channelId = 0x22222
        val builder: NotificationCompat.Builder
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //NotificationManagerCompat managerCompat= (NotificationManagerCompat) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0系统之上
            val channel = NotificationChannel(
                channelId.toString(),
                "chanel_name",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.setSound(null, null)
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            manager.createNotificationChannel(channel)
            builder = NotificationCompat.Builder(context, channelId.toString())
            //或者
            //builder = Notification.Builder(context);
            //builder.setChannelId(String.valueOf(channelId)); //创建通知时指定channelId
        } else {
            builder = NotificationCompat.Builder(context)
        }
        // 需要跳转指定的页面
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setTicker("新订单消息")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("河北省税务干部学校餐厅订单")
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)
            .setContentText(content)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)

        manager.notify(channelId, builder.build())

    }
}