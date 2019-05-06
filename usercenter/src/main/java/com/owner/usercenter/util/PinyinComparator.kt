package com.owner.usercenter.util

import com.owner.basemodule.room.entities.User
import java.util.Comparator

/**
 * 拼音比较
 * @author xiaanming
 */
class PinyinComparator : Comparator<User> {

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
