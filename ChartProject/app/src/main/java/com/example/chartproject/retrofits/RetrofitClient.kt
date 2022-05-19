package com.example.chartproject.retrofits

import com.example.chartproject.utils.isJsonArray
import com.example.chartproject.utils.isJsonObject
import okhttp3.logging.HttpLoggingInterceptor

import android.os.Handler
import android.os.Looper
import android.util.Log

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.concurrent.TimeUnit

//싱글턴
object RetrofitClient {
    //레트로핏클라이언트선언
    private var retrofitClient: Retrofit?=null
    //레트로핏클라이언트 가져오기
    fun getClient(baseUrl:String): Retrofit? {
        Log.d("TAG", "getClient: called")

        //okhttp인스턴스생성
        val client= OkHttpClient.Builder()
        //로그찍기위해
        //로깅 인터셉터 설정
        val loggingInterceptor= HttpLoggingInterceptor { message ->
            Log.d("TAG", "log called msg:$message")
            when {
                message.isJsonObject() -> {
                    Log.d("TAG", JSONObject(message).toString(4))
                }
                message.isJsonArray() -> {
                    Log.d("TAG", JSONObject(message).toString(4))
                }
                else -> {
                    try {
                        Log.d("TAG", JSONObject(message).toString(4))
                    } catch (e: Exception) {
                        Log.d("TAG", message)
                    }
                }
            }
        }
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
        //위에서 설정한 로깅 인터셉터를 okhttp클라이언트에 추가한다
        client.addInterceptor(loggingInterceptor)


        //기본 파라미터 인터셉터 추가
        val baseParameterInterceptor: Interceptor =(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                Log.d("TAG","인터셉터 호출")
                //오리지널 리퀘스트
                val originalRequest=chain.request()
                //?client_id=adssd
                //쿼리파라미터추가하기
//                val finalRequest=originalRequest.newBuilder()
//                        .method(originalRequest.method,originalRequest.body)
//                        .build()
                //return chain.proceed(finalRequest)
                val response=chain.proceed(originalRequest)
                if(response.code !=200){
                    Handler(Looper.getMainLooper()).post{
                    }
                }

                return response
            }

        })
        //위에서 설정한 기본파라미터 인터셉터를 okhttp클라이언트에 추가한다
        client.addInterceptor(baseParameterInterceptor)
        //커넥션 타임아웃
        client.connectTimeout(100, TimeUnit.SECONDS)
        client.readTimeout(100, TimeUnit.SECONDS)
        client.writeTimeout(100, TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true)

        if(retrofitClient==null){
            //레트로핏 빌더(생성패턴)를통해 인스턴스생성
            retrofitClient= Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                //위에서 설정한 클라이언트로 레트로핏클라이언트설정
                .client(client.build())
                .build()
            Log.d("TAG", "getClient: 빌드성공")
        }
        return retrofitClient
    }

}