package com.rick.jetpackmvvm.other

import com.rick.jetpackmvvm.util.NetUtil.createService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * 下载服务
 */
interface DownloadService {
    companion object {
        val INSTANCE = createService(DownloadService::class.java, null)
    }

    @Streaming
    @GET
    fun download(@Url url: String): Call<ResponseBody>
}