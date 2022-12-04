package com.vixchotersrozzaq.newsapp.api

import com.vixchotersrozzaq.newsapp.utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
   Retrofit Instance class ini untuk create request dari kode yang dibuat

 */
class RetrofitInstance {
    companion object{
        private val  retrofit by lazy {
            //lazy berarti hanya menginisialiasasi disini

            val logging= HttpLoggingInterceptor()
            /* untuk mencatat respons retrofit
             dan akan berguna dalam men-debug kode
             */
            //attaching ke retrofit object
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)  //see the body of the response
            //network client
            val client= OkHttpClient.Builder().addInterceptor(logging).build()

            //pass client ke retrofit instance
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
        //addConverterFactory digunakan untuk menentukan bagaimana respons harus diinterpretasi dan dikonversi ke kotlin object
                .client(client)
                .build()


        }

        //get api instance dari retrofit builder
        //api object
        // untuk membuat network request
        val api by lazy {
            retrofit.create(NewsApi::class.java)
        }
    }
}