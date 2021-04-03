package com.owner.basemodule.util

import com.owner.basemodule.room.entities.User
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import java.util.Comparator


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
/**
 * 拼音比较
 * @author xiaanming
 */
object PinyinComparator : Comparator<User> {

    override fun compare(o1: User, o2: User): Int {
        return if (o1.letters == "@" || o2.letters == "#") {
            1
        } else if (o1.letters == "#" || o2.letters == "@") {
            -1
        } else {
            o1.letters.compareTo(o2.letters)
        }
    }

}