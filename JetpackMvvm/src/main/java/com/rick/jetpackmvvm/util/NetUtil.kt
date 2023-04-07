package com.rick.jetpackmvvm.util

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.*
import com.rick.jetpackmvvm.other.DownloadService
import com.rick.jetpackmvvm.view.LoadingDialog
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

/**
 * 网络请求工具
 * 使用前必须先设置 [NetUtil.envList]
 */
object NetUtil {

    /**
     * 网络环境列表
     */
    @JvmStatic
    lateinit var envList: List<Env>

    /**
     * 加载框
     */
    @JvmStatic
    var loading: (() -> DialogFragment) = { LoadingDialog() }

    /**
     * 请求头
     */
    @JvmStatic
    var headers: ((isAppHost: Boolean) -> Map<String, String>)? = null

    /**
     * 默认失败回调
     */
    @JvmStatic
    var onFailDefault: ((code: Int, msg: String?) -> Unit)? =
        { _, msg -> ToastUtils.showShort(msg) }

    /**
     * token 失效回调
     */
    @JvmStatic
    var onUnauthorized: (() -> Unit)? = null

    private const val KEY = "com.rick.jetpackmvvm.util.NetUtil"

    private var clickCount = 0

    /**
     * 多次点击可切换环境
     */
    @JvmStatic
    fun click() {
        ++clickCount
        if (clickCount >= 5) {
            var index = envList.indexOfFirst { env.name == it.name }
            AlertDialog.Builder(ActivityUtils.getTopActivity())
                .setTitle("请选择网络环境")
                .setSingleChoiceItems(
                    envList.map { it.name }.toTypedArray(),
                    index
                ) { _, which -> index = which }
                .setPositiveButton("确定") { _, _ ->
                    SPStaticUtils.put(KEY, GsonUtils.toJson(envList[index]), true)
                    AppUtils.relaunchApp(true)
                }
                .show()
        }
    }

    /**
     * 网络环境类
     */
    data class Env(val name: String, val host: String)

    /**
     * 响应数据类
     */
    interface BaseResponse<T> {
        fun isSuccess(): Boolean = getCode() == 0
        fun getCode(): Int = 0
        fun getMsg(): String? = null
        fun getData(): T?
    }

    /**
     * 当前网络环境
     */
    @JvmStatic
    val env: Env
        get() {
            GsonUtils.fromJson(SPStaticUtils.getString(KEY), Env::class.java)?.let { spEnv ->
                envList.forEach { if (spEnv.name == it.name) return it } // 链接可能修改，缓存可能是旧的，需及返回新的
            }
            return envList[0]
        }

    /**
     * 创建网络请求服务
     */
    @JvmStatic
    fun <S> createService(serviceClass: Class<S>, host: String = env.host, api: String? = null): S {
        return Retrofit.Builder()
            .baseUrl(host + (api ?: ""))
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor {
                        LogUtils.d("NetUtil $it")
                    }.apply { setLevel(if (DownloadService::class.java.isAssignableFrom(serviceClass)) HttpLoggingInterceptor.Level.HEADERS else HttpLoggingInterceptor.Level.BODY) })
                    .addInterceptor(Interceptor { chain ->
                        chain.proceed(chain.request().let { request ->
                            request.newBuilder().apply {
                                headers?.let {
                                    it(request.url.host.contains(host)).forEach { (k, v) ->
                                        LogUtils.d("NetUtil $k $v")
                                        addHeader(k, v)
                                    }
                                }
                            }.build()
                        })
                    })
                    .build()
            )
            .callbackExecutor(Executors.newSingleThreadExecutor())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(serviceClass)
    }

    /**
     * Fragment 网络请求
     */
    @JvmStatic
    fun <D : Any> request(
        fragment: Fragment,
        call: Call<out BaseResponse<D>>,
        onSuccess: (data: D?) -> Unit,
        onFail: ((code: Int, msg: String?) -> Unit)? = onFailDefault,
        loading: Boolean = true
    ) {
        if (loading) {
            request2(fragment.childFragmentManager, call, onSuccess, onFail)
        } else {
            request1(fragment.viewLifecycleOwner, call, {}, onSuccess, onFail)
        }
    }

    /**
     * Activity 网络请求
     */
    @JvmStatic
    fun <D : Any> request(
        activity: AppCompatActivity,
        call: Call<out BaseResponse<D>>,
        onSuccess: (data: D?) -> Unit,
        onFail: ((code: Int, msg: String?) -> Unit)? = onFailDefault,
        loading: Boolean = true
    ) {
        if (loading) {
            request2(activity.supportFragmentManager, call, onSuccess, onFail)
        } else {
            request1(activity, call, {}, onSuccess, onFail)
        }
    }

    /**
     * 网络请求
     * 基于 [request1]，增加了加载框
     */
    private fun <D : Any> request2(
        fragmentManager: FragmentManager,
        call: Call<out BaseResponse<D>>,
        onSuccess: (data: D?) -> Unit,
        onFail: ((code: Int, msg: String?) -> Unit)? = onFailDefault
    ) {
        if (!fragmentManager.isStateSaved && !fragmentManager.isDestroyed) {
            with(loading()) {
                request1(this, call, { show(fragmentManager, null) }, {
                    if (isAdded) {
                        dismissAllowingStateLoss()
                        onSuccess(it)
                    }
                }, { code, msg ->
                    if (isAdded) {
                        dismissAllowingStateLoss()
                        onFail?.invoke(code, msg)
                    }
                })
            }
        }
    }

    /**
     * 网络请求
     * 基于 [request0]，解析了 [BaseResponse]，回调转至主线程
     */
    private fun <D : Any> request1(
        owner: LifecycleOwner,
        call: Call<out BaseResponse<D>>,
        onStart: () -> Unit,
        onSuccess: (data: D?) -> Unit,
        onFail: ((code: Int, msg: String?) -> Unit)?
    ) {
        request0(owner, call, onStart, { resp ->
            ThreadUtils.runOnUiThread {
                if (resp.isSuccess()) {
                    onSuccess(resp.getData())
                } else {
                    onFail?.invoke(resp.getCode(), resp.getMsg())
                }
            }
        }, onFail)
    }

    /**
     * 网络请求
     * 最原始的方法
     */
    private fun <R : Any> request0(
        owner: LifecycleOwner,
        call: Call<R>,
        onStart: () -> Unit,
        onRespBody: (R) -> Unit,
        onFail: ((code: Int, msg: String?) -> Unit)?
    ) {
        owner.lifecycle.let { lifecycle ->
            // 若生命周期已结束，则不请求
            if (lifecycle.currentState == Lifecycle.State.DESTROYED) return
            // 监听生命周期
            lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    // 若生命周期结束时，若还在请求，则取消
                    if (call.isExecuted) call.cancel()
                    lifecycle.removeObserver(this)
                    super.onDestroy(owner)
                }
            })
            // 请求回调
            val callback: Callback<R> = object : Callback<R> {

                override fun onResponse(call: Call<R>, response: Response<R>) {
                    try {
                        // 请求成功
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null) {
                                // body 正常
                                onSuccess(body)
                            } else {
                                // body 为 null 失败
                                onFailure(call, Exception("response body is null"))
                            }
                        } else {
                            // code 异常失败
                            onFail(response.code(), response.errorBody()?.string() ?: "no message")
                            // token 失效回调
                            if (response.code() == HttpsURLConnection.HTTP_UNAUTHORIZED) onUnauthorized?.invoke()
                        }
                    } catch (e: Exception) {
                        // 异常失败
                        onFailure(call, e)
                    }
                }

                override fun onFailure(call: Call<R>, t: Throwable) = onFail(-1, t.message)

                /**
                 * 成功
                 */
                private fun onSuccess(body: R) {
                    // 若生命周期已结束，则不回调
                    if (lifecycle.currentState == Lifecycle.State.DESTROYED) return
                    /**
                     * 因为可能是下载耗时，需要在子线程，所以不在主线程回调，后续有需要再转主线程
                     */
                    onRespBody.invoke(body)
                }

                /**
                 * 失败
                 */
                private fun onFail(code: Int, msg: String?) {
                    // 若生命周期已结束，则不回调
                    if (lifecycle.currentState == Lifecycle.State.DESTROYED) return
                    ThreadUtils.runOnUiThread { onFail?.invoke(code, msg) }
                }
            }
            onStart()
            // 发起请求
            call.enqueue(callback)
        }
    }

    /**
     * 下载
     */
    @JvmStatic
    fun download(
        owner: LifecycleOwner,
        url: String,
        filePath: String,
        onSuccess: () -> Unit,
        onFail: ((code: Int, msg: String?) -> Unit)?,
        onProgress: FileIOUtils.OnProgressUpdateListener?
    ) {
        // 初始化进度
        onProgress?.onProgressUpdate(0.0)
        request0(
            owner,
            DownloadService.INSTANCE.download(url),
            {},
            { body ->
                // 创建文件
                FileUtils.createOrExistsFile(filePath)
                // 文件大小
                val totalSize = body.contentLength().toDouble()
                // 读写文件
                val inputStream = body.byteStream()
                var os: OutputStream? = null
                try {
                    os = BufferedOutputStream(FileOutputStream(File(filePath), false), 1024)
                    var curSize = 0
                    val data = ByteArray(1024)
                    var len: Int
                    while (inputStream.read(data).also { len = it } != -1) {
                        os.write(data, 0, len)
                        curSize += len
                        // 更新进度
                        ThreadUtils.runOnUiThread { onProgress?.onProgressUpdate(curSize / totalSize) }
                    }
                    // 下载成功
                    ThreadUtils.runOnUiThread { onSuccess() }
                } catch (e: Exception) {
                    // 异常失败
                    ThreadUtils.runOnUiThread { onFail?.invoke(-1, e.message) }
                } finally {
                    inputStream.close()
                    os?.close()
                }
            },
            onFail
        )
    }
}