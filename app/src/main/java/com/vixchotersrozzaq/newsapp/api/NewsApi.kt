package com.vixchotersrozzaq.newsapp.api

import com.vixchotersrozzaq.newsapp.models.NewsResponse
import com.vixchotersrozzaq.newsapp.utils.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")

    //function
    //async
    //coroutine
    suspend fun getBreakingNews(
        //request parameters ke function
    @Query("country")
    countryCode: String = "us", //pake kode negara us

    @Query("page")  //paginate request
    pageNumber: Int= 1,

    @Query("apiKey")
    apiKey: String= API_KEY

    ):Response<NewsResponse> //return response


    @GET("v2/everything")

    //function
    //async
    //coroutine
    suspend fun searchForNews(
        //request parameters to function
        @Query("q")
        searchQuery: String,
        @Query("page")  // paginate request
        pageNumber: Int= 1,
        @Query("apiKey")
        apiKey: String= API_KEY
    ):Response<NewsResponse> //return response
}