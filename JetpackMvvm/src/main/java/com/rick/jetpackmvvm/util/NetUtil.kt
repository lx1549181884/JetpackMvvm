package com.rick.jetpackmvvm.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.*
import com.rick.jetpackmvvm.base.BaseResponseBean
import com.rick.jetpackmvvm.commom.DownloadService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.guava.GuavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

/**
 * 网络请求工具
 */
object NetUtil {
    private lateinit var config: Config
    private val onFailDefault = object : OnFail {
        override fun onFail(code: Int, msg: String?) = ToastUtils.showShort("$code $msg")
    }

    @JvmStatic
    fun init(config: Config) {
        NetUtil.config = config
    }

    interface Config {
        fun getHost(): String
        fun getHeaders(host: String): Map<String, String>?
        fun onUnauthorized(msg: String)
        fun createLoading(): DialogFragment
    }

    interface Api<D : Any> {
        fun request(): Call<out BaseResponseBean<D>>
    }

    interface OnFail {
        fun onFail(code: Int, msg: String?)
    }

    interface OnSuccess<D> {
        fun onSuccess(data: D)
    }

    @JvmStatic
    fun <S> createService(serviceClass: Class<S>, api: String?): S {
        return Retrofit.Builder()
            .baseUrl(config.getHost() + (api ?: ""))
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor {
                        LogUtils.d("NetUtil $it")
                    }.apply { setLevel(if (DownloadService::class.java.isAssignableFrom(serviceClass)) HttpLoggingInterceptor.Level.HEADERS else HttpLoggingInterceptor.Level.BODY) })
                    .addInterceptor(Interceptor {
                        it.proceed(
                            it.request().newBuilder().apply {
                                config.getHeaders(it.request().url.host)?.forEach { (k, v) ->
                                    run {
                                        LogUtils.d("NetUtil $k $v")
                                        addHeader(k, v)
                                    }
                                }
                            }.build()
                        )
                    })
                    .build()
            )
            .callbackExecutor(Executors.newSingleThreadExecutor())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(GuavaCallAdapterFactory.create()) // 支持将 Call 转为 ListenableFuture, PagingResource 使用数据类型
            .build().create(serviceClass)
    }

    @JvmStatic
    fun <D : Any> request(
        fragment: Fragment,
        api: Api<D>,
        onSuccess: OnSuccess<D?>?
    ) {
        request(fragment, api, onSuccess, onFailDefault, true)
    }

    @JvmStatic
    fun <D : Any> request(
        fragment: Fragment,
        api: Api<D>,
        onSuccess: OnSuccess<D?>?,
        onFail: OnFail?,
        loading: Boolean
    ) {
        if (loading) {
            requestWithLoading(fragment.childFragmentManager, api, onSuccess, onFail)
        } else {
            request(fragment.viewLifecycleOwner, api, onSuccess, onFail)
        }
    }

    @JvmStatic
    fun <D : Any> request(
        activity: AppCompatActivity,
        api: Api<D>,
        onSuccess: OnSuccess<D?>?
    ) {
        request(activity, api, onSuccess, onFailDefault, true)
    }

    @JvmStatic
    fun <D : Any> request(
        activity: AppCompatActivity,
        api: Api<D>,
        onSuccess: OnSuccess<D?>?,
        onFail: OnFail? = onFailDefault,
        loading: Boolean
    ) {
        if (loading) {
            requestWithLoading(activity.supportFragmentManager, api, onSuccess, onFail)
        } else {
            request(activity, api, onSuccess, onFail)
        }
    }

    @JvmStatic
    fun <D : Any> requestWithLoading(
        fragmentManager: FragmentManager,
        api: Api<D>,
        onSuccess: OnSuccess<D?>?,
        onFail: OnFail?
    ) {
        if (!fragmentManager.isStateSaved) {
            with(config.createLoading()) {
                show(fragmentManager, null)
                request(this, api, object : OnSuccess<D?> {
                    override fun onSuccess(data: D?) {
                        if (isAdded) {
                            dismissAllowingStateLoss()
                            onSuccess?.onSuccess(data)
                        }
                    }
                }, object : OnFail {
                    override fun onFail(code: Int, msg: String?) {
                        if (isAdded) {
                            dismissAllowingStateLoss()
                            onFail?.onFail(code, msg)
                        }
                    }
                })
            }
        }
    }

    @JvmStatic
    fun <D : Any, R : BaseResponseBean<D>> request(
        owner: LifecycleOwner?,
        api: Api<D>,
        onSuccess: OnSuccess<D?>?,
        onFail: OnFail?
    ) {
        request(
            owner,
            { api.request() as Call<R> },
            {
                ThreadUtils.runOnUiThread {
                    if (it.isSuccess()) {
                        onSuccess?.onSuccess(it.getData())
                    } else {
                        onFail?.onFail(it.getCode(), it.getMsg())
                    }
                }
            },
            object : OnFail {
                override fun onFail(code: Int, msg: String?) {
                    onFail?.onFail(code, msg)
                }
            }
        )
    }

    @JvmStatic
    private fun <R : Any> request(
        owner: LifecycleOwner?,
        api: () -> Call<R>,
        onResponseBody: (R) -> Unit,
        onFail: OnFail?
    ) {
        owner?.lifecycle.let { lifecycle ->
            val callback: Callback<R> = object : Callback<R> {
                override fun onResponse(call: Call<R>, response: Response<R>) {
                    try {
                        if (lifecycle?.currentState == Lifecycle.State.DESTROYED) {
                            return
                        }
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null) {
                                onResponseBody.invoke(body)
                            } else {
                                onFail(-1, "response body is null")
                            }
                        } else {
                            val msg = response.errorBody()?.string() ?: "no message"
                            if (response.code() == HttpsURLConnection.HTTP_UNAUTHORIZED) { // token 失效
                                ThreadUtils.runOnUiThread { config.onUnauthorized(msg) }
                            } else {
                                onFail(-1, msg)
                            }
                        }
                    } catch (e: Exception) {
                        onFail(-1, e.message)
                    }
                }

                override fun onFailure(call: Call<R>, t: Throwable) {
                    if (lifecycle?.currentState == Lifecycle.State.DESTROYED) {
                        return
                    }
                    onFail(-1, t.message)
                }

                private fun onFail(code: Int, msg: String?) {
                    ThreadUtils.runOnUiThread { onFail?.onFail(code, msg) }
                }
            }
            val call: Call<R> = api.invoke()
            val observer = arrayOfNulls<LifecycleEventObserver>(1)
            observer[0] = LifecycleEventObserver { _: LifecycleOwner?, event: Lifecycle.Event ->
                when (event.targetState) {
                    Lifecycle.State.STARTED, Lifecycle.State.RESUMED -> if (!call.isExecuted) {
                        call.enqueue(callback)
                    }
                    Lifecycle.State.DESTROYED -> {
                        lifecycle?.removeObserver(observer[0]!!)
                        if (call.isExecuted) {
                            call.cancel()
                        }
                    }
                    else -> {}
                }
            }
            lifecycle?.addObserver(observer[0]!!)
            when (lifecycle?.currentState) {
                null, Lifecycle.State.STARTED, Lifecycle.State.RESUMED -> if (!call.isExecuted) {
                    call.enqueue(callback)
                }
                else -> {}
            }
        }
    }

    @JvmStatic
    fun download(
        owner: LifecycleOwner?,
        url: String,
        filePath: String,
        onSuccess: OnSuccess<Any?>?,
        onFail: OnFail?,
        onProgress: FileIOUtils.OnProgressUpdateListener?
    ) {
        request(
            owner,
            { DownloadService.INSTANCE.download(url) },
            { body ->
                FileUtils.createOrExistsFile(filePath)
                val totalSize = body.contentLength().toDouble()
                val inputStream = body.byteStream()
                var os: OutputStream? = null
                try {
                    os = BufferedOutputStream(FileOutputStream(File(filePath), false), 1024)
                    var curSize = 0
                    ThreadUtils.runOnUiThread { onProgress?.onProgressUpdate(0.0) }
                    val data = ByteArray(1024)
                    var len: Int
                    while (inputStream.read(data).also { len = it } != -1) {
                        os.write(data, 0, len)
                        curSize += len
                        ThreadUtils.runOnUiThread { onProgress?.onProgressUpdate(curSize / totalSize) }
                    }
                    ThreadUtils.runOnUiThread { onSuccess?.onSuccess(null) }
                } catch (e: Exception) {
                    ThreadUtils.runOnUiThread { onFail?.onFail(-1, e.message) }
                } finally {
                    inputStream.close()
                    os?.close()
                }
            },
            onFail
        )
    }
}