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
     * 获取当前时间的后天时间
     * @param cl
     * *
     * @return
     */
    fun getDayAfterTomorrow(): Calendar {
        val today = Date()
        val c1 = Calendar.getInstance()//获取日历对象
        c1.time = today//将日历设定为今日
        c1.add(Calendar.DAY_OF_MONTH, 2)//将日历的天数加2
        return c1
    }


    /**
     * 获得当前日期与本周日相差的天数
     */
    fun getMondayPlus(gmtCreate: Date): Int {
        val cd = Calendar.getInstance()
        cd.setTime(gmtCreate)
        val dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1
        println(dayOfWeek)
        if (dayOfWeek == 1) {
            return 0
        } else {
            return 1 - dayOfWeek
        }
    }

    /**
     *  获得下周星期一的日期
     */

    fun getNextMonday(gmtCreate: Date): Date {
        val mondayPlus = getMondayPlus(gmtCreate)
        val currenDate = GregorianCalendar()
        currenDate.add(GregorianCalendar.DATE, mondayPlus+7)
        val monday = currenDate.time
        return monday
    }

    /**
     * 获取下周的所有日期
     */
    fun getNextWeek(gmtCreate: Date):List<Date>{
        val nextWeek = mutableListOf<Date>()
        for (day in 7..13) {
            val mondayPlus = getMondayPlus(gmtCreate)
            val currenDate = GregorianCalendar()
            currenDate.add(GregorianCalendar.DATE, mondayPlus + day)
            val monday = currenDate.time
            nextWeek.add(monday)
        }
        return nextWeek
    }

    /**
     * 获取下周的日期字符串,格式“2020-01-02”
     */
    fun getNextWeekToString(gmtCreate: Date): List<String> {
        val nextWeekString = mutableListOf<String>()

        for (day in 7..13) {
            val mondayPlus = getMondayPlus(gmtCreate)
            val currentDate = GregorianCalendar()
            currentDate.add(GregorianCalendar.DATE, mondayPlus + day)
            val date = currentDate.time
            nextWeekString.add(getDateString(date))
        }
        return nextWeekString
    }
}