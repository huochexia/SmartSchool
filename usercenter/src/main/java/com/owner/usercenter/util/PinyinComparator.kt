package com.owner.usercenter.util

import com.owner.usercenter.http.entities.LoginResp
import java.util.Comparator

/**
 * 拼音比较
 * @author xiaanming
 */
class PinyinComparator : Comparator<LoginResp> {

    override fun compare(o1: LoginResp, o2: LoginResp): Int {
        return if (o1.letters == "@" || o2.letters == "#") {
            1
        } else if (o1.letters == "#" || o2.letters == "@") {
            -1
        } else {
            o1.letters.compareTo(o2.letters)
        }
    }

}
