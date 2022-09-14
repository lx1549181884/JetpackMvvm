package com.rick.jetpackmvvm.base

import androidx.paging.ListenableFuturePagingSource
import androidx.paging.PagingState
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors

abstract class BasePagingSource<T : Any> : ListenableFuturePagingSource<Int, T>() {
    private val executor: Executor = Executors.newSingleThreadExecutor()
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return null
    }

    override fun loadFuture(params: LoadParams<Int>): ListenableFuture<LoadResult<Int, T>> {
        return Futures.catching(
            getPageFuture(params, executor),
            Exception::class.java,
            { LoadResult.Error(Throwable()) },
            executor
        )
    }

    protected abstract fun getPageFuture(
        params: LoadParams<Int>,
        executor: Executor,
    ): ListenableFuture<LoadResult<Int, T>>
}