package com.rick.jetpackmvvm.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.rick.jetpackmvvm.base.BaseResponseBean
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.guava.GuavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.HttpsURLConnection

/**
 * 网络请求工具
 */
object NetUtil {
    private lateinit var host: String
    private var client: OkHttpClient? = null
    private lateinit var i: I
    private val onFailDefault = object : OnFail {
        override fun onFail(code: Int, msg: String?) = ToastUtils.showShort("$code $msg")
    }

    @JvmStatic
    fun init(host: String, client: OkHttpClient? = null, i: I) {
        NetUtil.host = host
        NetUtil.client = client
        NetUtil.i = i
    }

    interface I {
        fun onUnauthorized(msg: String)
        fun createLoading(): DialogFragment
    }

    interface Api<T : Any> {
        fun request(): Call<out BaseResponseBean<T>>
    }

    interface OnFail {
        fun onFail(code: Int, msg: String?)
    }

    interface OnSuccess<T> {
        fun onSuccess(t: T)
    }

    @JvmStatic
    fun <T> createService(serviceClass: Class<T>, api: String?): T {
        return Retrofit.Builder()
            .baseUrl(host + (api ?: ""))
            .apply { client?.let { client(it) } }
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
        loading: Boolean = true
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
        with(i.createLoading()) {
            show(fragmentManager, null)
            ThreadUtils.runOnUiThreadDelayed({
                request(this, api, object : OnSuccess<D?> {
                    override fun onSuccess(t: D?) {
                        dismissAllowingStateLoss()
                        onSuccess?.onSuccess(t)
                    }
                }, object : OnFail {
                    override fun onFail(code: Int, msg: String?) {
                        dismissAllowingStateLoss()
                        onFail?.onFail(code, msg)
                    }
                })
            }, 1)
        }
    }

    @JvmStatic
    fun <D : Any, R : BaseResponseBean<D>> request(
        owner: LifecycleOwner?,
        api: Api<D>,
        onSuccess: OnSuccess<D?>?,
        onFail: OnFail?
    ) {
        owner?.lifecycle.let { lifecycle ->
            val callback: retrofit2.Callback<R> = object : retrofit2.Callback<R> {
                override fun onResponse(call: Call<R>, response: Response<R>) {
                    try {
                        if (lifecycle?.currentState == Lifecycle.State.DESTROYED) {
                            return
                        }
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null) {
                                if (body.isSuccess()) {
                                    onSuccess?.onSuccess(body.getData())
                                } else {
                                    onFail(body.getCode(), body.getMsg())
                                }
                            } else {
                                onFail(-1, "response body is null")
                            }
                        } else {
                            val errorBody = response.errorBody()
                            val msg =
                                if (errorBody != null) errorBody.string() else "no message"
                            if (response.code() == HttpsURLConnection.HTTP_UNAUTHORIZED) { // token 失效
                                i.onUnauthorized(msg)
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
                    onFail?.onFail(code, msg)
                }
            }
            val call: Call<R> = api.request() as Call<R>
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
}