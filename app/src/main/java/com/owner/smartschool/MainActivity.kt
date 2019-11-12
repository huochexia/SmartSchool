package com.owner.smartschool

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.owner.basemodule.arouter.RouterPath

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ARouter.getInstance().build(RouterPath.UserCenter.PATH_LOGIN).navigation()
        finish()
    }


}
