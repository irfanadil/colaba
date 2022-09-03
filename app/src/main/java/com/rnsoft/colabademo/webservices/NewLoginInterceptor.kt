package com.rnsoft.colabademo.webservices

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class NewLoginInterceptor:Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        //TODO("Not yet implemented")
        val original: Request = chain.request()
        // Request customization: add request headers
        // Request customization: add request headers
        val requestBuilder: Request.Builder =
            original.newBuilder().method(original.method, original.body)
        val request: Request = requestBuilder.build()
        return chain.proceed(request)
    }

}