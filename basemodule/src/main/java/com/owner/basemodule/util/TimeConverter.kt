package com.owner.basemodule.util

import android.text.format.DateUtils
import java.text.ParseException
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
    fun tramsTimeAgo(time: String?): String =
        transTimeStamp(time).let {
            DateUtils.getRelativeTimeSpanString(it).toString()
        }

    /**
     * 2019-03-26T08:51:43Z -> time stamp
     */
    fun transTimeStamp(time: String?): Long =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CHINA)
            .let {
                it.timeZone = TimeZone.getTimeZone("GMT+1")
                it.parse(time).time
            }

    /**
     * 获取系统当前日期字符串
     */
    fun getCurrentDateString(): String {
        val formatter = SimpleDateFormat("yyyy-M-d")
        val curDate = Date(System.currentTimeMillis())//获取当日期
        return formatter.format(curDate)
    }

    /**
     * 将日期转换成字符串形式
     */
    fun getDateString(date: Date): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        return formatter.format(date)
    }

    /**
     * 将字符串转换成日期
     */
    fun strToDate(str: String): Date {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date = try {
            format.parse("$str 00:00:00")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date as Date
    }
    /**
     * 获取当前时间的前一天时间
     * @param cl
     * *
     * @return
     */
    fun getBeforeDay(cl: Calendar): Calendar {
        val day = cl.get(Calendar.DATE)
        cl.set(Calendar.DATE, day - 1)
        return cl
    }

    /**
     * 获取当前时间的后一天时间
     * @param cl
     * *
     * @return
     */
    fun getAfterDay(cl: Calendar): Calendar {
        val day = cl.get(Calendar.DATE)
        cl.set(Calendar.DATE, day + 10)
        return cl
    }
}