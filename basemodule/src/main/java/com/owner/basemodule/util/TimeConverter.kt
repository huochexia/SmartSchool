package com.owner.basemodule.util

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间转换工具
 * Created by Liuyong on 2019-03-26.It's smartschool
 *@description:
 */
object TimeConverter {

    /**
     * 2019-03-26T08:51:43Z -> 2 days ago
     */
    fun tramsTimeAgo(time:String?):String =
            transTimeStamp(time).let{
                DateUtils.getRelativeTimeSpanString(it).toString()
            }

    /**
     * 2019-03-26T08:51:43Z -> time stamp
     */
    fun transTimeStamp(time:String?):Long =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CHINA)
                .let {
                    it.timeZone = TimeZone.getTimeZone("GMT+1")
                    it.parse(time).time
                }

}