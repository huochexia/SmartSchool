package com.owner.basemodule.util

import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType


/**
 * 拼音首字母
 */
fun firstLetters( str: String?): String {
    if (str == null || str == "") {
        return "#"
    }
    val ch = str[0]
    if (ch in 'a'..'z') {
        return (ch - 'a' + 'A'.toInt()).toChar() + ""
    }
    if (ch in 'A'..'Z') {
        return ch.toString() + ""
    }
    try {
        val defaultFormat = HanyuPinyinOutputFormat()
        // 设置大小写格式
        defaultFormat.caseType = HanyuPinyinCaseType.UPPERCASE
        // 设置声调格式：
        defaultFormat.toneType = HanyuPinyinToneType.WITHOUT_TONE
        if (ch.toString().matches(Regex("[\\u4E00-\\u9FA5]+"))) {
            val array = PinyinHelper.toHanyuPinyinStringArray(ch, defaultFormat)
            if (array != null) {
                return array[0][0].toString() + ""
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return "#"
}
