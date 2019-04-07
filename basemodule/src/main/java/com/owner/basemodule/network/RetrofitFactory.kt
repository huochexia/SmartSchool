/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.owner.basemodule.network

import com.owner.basemodule.base.BaseApplication
import com.owner.basemodule.base.BaseApplication.Companion.BASE_URL
import com.owner.basemodule.base.BaseApplication.Companion.BMOB_APP_ID
import com.owner.basemodule.base.BaseApplication.Companion.BMOB_REST_API_KEY
import com.owner.basemodule.ext.ensureDis
import com.owner.basemodule.util.SingletonHolderNoArg
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


const val HTTP_CLIENT_MODULE_INTERCEPTOR_LOG_TAG = "http_client_module_interceptor_log_tag"

const val TIME_OUT_SECONDS = 10L


/**
 * 创建Retrofit工厂
 * Created by Liuyong on 2019-04-03.It's smartschool
 *@description:
 */
class RetrofitFactory private constructor() {

    private val interceptor: Interceptor
    private val retrofit: Retrofit

//    private val cacheFile by lazy {
//        File(BaseApplication.getInstance().cacheDir, "webServiceApi").apply {
//            ensureDis()
//        }
//    }

    init {
        /*
         * 网络拦截器。将所有REST API的错误状态中包含body的，转换成为成功状态body，统一管理
         * 主要是针对Bmob的状态码404
         *
         */
        interceptor = Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("X-Bmob-Application-Id", BMOB_APP_ID)
                .addHeader("X-Bmob-REST-API-Key", BMOB_REST_API_KEY)
                .addHeader("Content_Type", "application/json")
                .build()
            val response = chain.proceed(request)
            if ( response.code() == 400) {
                val mediaType = response.body()?.contentType()
                val content = response.body()?.string()
                response.newBuilder().code(200)
                    .body(ResponseBody.create(mediaType, content))
                    .build()
            } else {
                response
            }
        }
        //创建Retrofit实例
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(initClient())
            .build()

    }

    /**
     * 日志拦截器
     */
    private fun logInterceptor(): Interceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }


    /**
     * OKHttp3
     */
    private fun initClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(logInterceptor())

            .connectTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    /**
     * 实例化网络接口
     */
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }

    /**
     * 生成单例
     */
    companion object :
        SingletonHolderNoArg<RetrofitFactory>(::RetrofitFactory)
}