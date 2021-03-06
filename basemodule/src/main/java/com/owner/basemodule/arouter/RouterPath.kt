package com.owner.basemodule.arouter

/**
 * 用于ARouter的路径统一管理
 */
object RouterPath {

    class App {
        companion object{
            const val  PATH_MAIN = "/app/main"
        }
    }
    class UserCenter {
        companion object{
            const val PATH_LOGIN = "/usercenter/login"
            const val PATH_REGISTER = "/usercenter/register"
            const val PATH_USERMANAGER = "/usercenter/usermanager"
        }
    }
    class Restaurant{
        companion object{
            const val PATH_MAIN = "/restaurant/main"
        }
    }
}