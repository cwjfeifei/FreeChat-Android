package com.ti4n.freechat.util

import android.util.Log
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask
import com.alibaba.sdk.android.oss.model.CannedAccessControlList
import com.alibaba.sdk.android.oss.model.CreateBucketRequest
import com.alibaba.sdk.android.oss.model.CreateBucketResult
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


suspend fun OSSClient.createBucket(bucketName: String) = suspendCancellableCoroutine {
    val createBucketRequest = CreateBucketRequest(bucketName)
    createBucketRequest.bucketACL = CannedAccessControlList.PublicReadWrite
//    createBucketRequest.locationConstraint = "oss-cn-hangzhou"
    val createTask: OSSAsyncTask<*> = asyncCreateBucket(
        createBucketRequest,
        object : OSSCompletedCallback<CreateBucketRequest, CreateBucketResult?> {
            override fun onSuccess(request: CreateBucketRequest, result: CreateBucketResult?) {
                Log.e("create bucket", "onSuccess: $request")
                Log.e("create bucket", "onSuccess: ${result?.bucketLocation}")
                it.resume(request.bucketName)
            }

            override fun onFailure(
                request: CreateBucketRequest,
                clientException: ClientException?,
                serviceException: ServiceException?
            ) {
                // 请求异常。
                if (clientException != null) {
                    clientException.printStackTrace()
                }
                if (serviceException != null) {
                    serviceException.printStackTrace()
                }
            }
        })
    it.invokeOnCancellation {
        createTask.cancel()
    }
}