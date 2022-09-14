package com.rick.jetpackmvvm.base

import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executor

abstract class BasePageSizeSource<Bean : Any, Response> : BasePagingSource<Bean>() {
    override fun getPageFuture(
        params: LoadParams<Int>,
        executor: Executor,
    ): ListenableFuture<LoadResult<Int, Bean>> {
        val key = params.key
        val page = if (key == null) 1 else key + 1
        val pageSize = params.loadSize
        return Futures.transform(getInput(page, pageSize),
            { response: Response ->
                val data = getData(response)
                LoadResult.Page(data,
                    null,
                    if (data.size < pageSize) null else page,
                    LoadResult.Page.COUNT_UNDEFINED,
                    LoadResult.Page.COUNT_UNDEFINED)
            },
            executor)
    }

    protected abstract fun getData(response: Response): List<Bean>
    protected abstract fun getInput(page: Int, size: Int): ListenableFuture<Response>?
}