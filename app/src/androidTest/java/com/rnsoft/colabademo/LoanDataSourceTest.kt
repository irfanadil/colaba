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
class LoanDataSourceTest {
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

    private val sut = LoanDataSource(api)

    val context = ApplicationProvider.getApplicationContext<Context>()

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun fetchLoansTest() {
        val mockedResponse = MockResponse()
        mockedResponse.setResponseCode(200)
        //mockedResponse.setBody("{}") // sample JSON
        val jsonFileString = getJsonDataFromAsset(context, "test-loans.json")
        jsonFileString?.let {
            mockedResponse.setBody("[LoanItem(activityTime=2021-06-15T08:11:35.6210342Z, cellNumber=33333333333, coBorrowerCount=1, detail=Detail(address=Address(city=null, countryId=null, countryName=null, stateId=null, stateName=null, street=null, unit=null, zipCode=null), loanAmount=5000, propertyValue=6000), documents=5, email=qumber@gmail.com, firstName=Qumber, lastName=Kazmi, loanApplicationId=2175, loanPurpose=Purchase, milestone=Processing, recycleCardState=false), LoanItem(activityTime=2021-06-18T08:27:29.7625446Z, cellNumber=2142715414, coBorrowerCount=1, detail=Detail(address=Address(city=Texas City, countryId=1, countryName=United States, stateId=0, stateName=None, street=st, unit=, zipCode=11111), loanAmount=239999, propertyValue=299999), documents=null, email=khatri03@gmail.com, firstName=Sohail, lastName=Ahmed, loanApplicationId=20018, loanPurpose=Purchase, milestone=Application Started, recycleCardState=false), LoanItem(activityTime=2021-06-20T20:31:29.4074677Z, cellNumber=2142715414, coBorrowerCount=0, detail=Detail(address=Address(city=Texas City, countryId=1, countryName=United States, stateId=0, stateName=None, street=st, unit=, zipCode=11111), loanAmount=355555, propertyValue=444444), documents=null, email=khatri03@gmail.com, firstName=Sohail, lastName=Ahmed, loanApplicationId=20019, loanPurpose=Purchase, milestone=Application Started, recycleCardState=false), LoanItem(activityTime=2021-06-21T09:18:09.6327092Z, cellNumber=2142715414, coBorrowerCount=0, detail=Detail(address=Address(city=Texas City, countryId=1, countryName=United States, stateId=0, stateName=None, street=st, unit=, zipCode=11111), loanAmount=280000, propertyValue=350000), documents=null, email=khatri03@gmail.com, firstName=Sohail, lastName=Ahmed, loanApplicationId=20022, loanPurpose=Purchase, milestone=Application Started, recycleCardState=false), LoanItem(activityTime=2021-06-21T10:44:15.9836984Z, cellNumber=2222222222, coBorrowerCount=0, detail=Detail(address=Address(city=null, countryId=null, countryName=null, stateId=null, stateName=null, street=null, unit=null, zipCode=null), loanAmount=null, propertyValue=null), documents=null, email=xywixiv@mailinator.com, firstName=Quincy, lastName=Bird, loanApplicationId=20024, loanPurpose=Purchase, milestone=Application Started, recycleCardState=false)]")
            mockWebServer.enqueue(mockedResponse)
            runBlocking {
                val actual = sut.loadAllLoans("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJVc2VySWQiOiI0MTA4NCIsImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL25hbWUiOiJtdWJhc2hpci5tY3VAbWFpbGluYXRvci5jb20iLCJGaXJzdE5hbWUiOiJBbGl5YSIsIkxhc3ROYW1lIjoiUHJhc2xhIiwiVGVuYW50Q29kZSI6ImxlbmRvdmEiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3JvbGUiOlsiTUNVIiwiTG9hbiBPZmZpY2VyIl0sImV4cCI6MTYyNjU3NzUyNSwiaXNzIjoicmFpbnNvZnRmbiIsImF1ZCI6InJlYWRlcnMifQ.9ROHOvm-KPUfYRScq_pm9aDatA7f8RAfUikJcAka3RI",
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date()).toString(),
                    pageNumber = 1,
                    pageSize = 20,
                    loanFilter = 0,
                    orderBy = 0,
                    assignedToMe = true
                )

                val expected = it
                if(actual is Result.Success)
                    Truth.assertThat(actual.data).isEqualTo(expected)


            }
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

    fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }
    //mMockServer.setDispatcher(mDispatcher)
}