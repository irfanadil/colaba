package com.rnsoft.colabademo

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class LoginWebServiceTest {
    private val mockWebServer = MockWebServer()

    private val client = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.SECONDS)
        .readTimeout(1, TimeUnit.SECONDS)
        .writeTimeout(1, TimeUnit.SECONDS)
        .build()

    private val api = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ServerApi::class.java)

    private val sut = LoginDataSource(api)

    val context = ApplicationProvider.getApplicationContext<Context>()

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun onSuccessFulLogin() {
        val mockedResponse = MockResponse()
        mockedResponse.setResponseCode(200)
        var expected = "LoginResponse(status=Success, data=Data(isLoggedIn=false, refreshToken=wJ/m8xa6W+BW+jayDGj8IsVwuNS1oAteU/LM5ME09vU=, refreshTokenValidTo=2022-03-26T10:11:31.0133977Z, token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJVc2VySWQiOiI0MTA4NCIsImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL25hbWUiOiJtdWJhc2hpci5tY3VAbWFpbGluYXRvci5jb20iLCJGaXJzdE5hbWUiOiJBbGl5YSIsIkxhc3ROYW1lIjoiUHJhc2xhIiwiVGVuYW50Q29kZSI6ImxlbmRvdmEiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3JvbGUiOlsiTUNVIiwiTG9hbiBPZmZpY2VyIl0sImV4cCI6MTYyNjkwNTQ5MSwiaXNzIjoicmFpbnNvZnRmbiIsImF1ZCI6InJlYWRlcnMifQ.ZED9J3CS9F65kuMSiaoY2PQkLZwYSZ-hLyyR2Ik7vRo, userName=mubashir.mcu@mailinator.com, firstName=Aliya, lastName=Prasla, userProfileId=41084, validFrom=0001-01-01T00:00:00, validTo=2021-07-21T22:11:31Z, tokenType=0, tokenTypeName=AccessToken), message=null, code=200)"
        mockedResponse.setBody(expected)
        mockWebServer.enqueue(mockedResponse)
        runBlocking {
            val actual = sut.login(userEmail = "mubashir.mcu@mailinator.com", password ="test123" , dontAskTwoFaIdentifier="")
            if(actual is Result.Success)
                Truth.assertThat(actual.data).isEqualTo(expected)
        }
    }

    @Test
    fun onFailedLogin() {
        val mockedResponse = MockResponse()
        mockedResponse.setResponseCode(400)
        mockWebServer.enqueue(mockedResponse)
        runBlocking {
            val actual = sut.login(userEmail = "mubashir.mcu@mailinator.com", password ="test123" , dontAskTwoFaIdentifier="")
            if(actual is Result.Error)
                Truth.assertThat(actual).isEqualTo(null)
        }
    }

    val mDispatcher:Dispatcher  = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            if (request.path!!.contains("/request1")) {
                return MockResponse().setBody("reponse1");
            }

            if (request.path!!.contains("/request2")) {
                return MockResponse().setBody("reponse2");
            }
            return MockResponse().setResponseCode(404);
        }


    }
    //mMockServer.setDispatcher(mDispatcher)
}