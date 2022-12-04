package com.vixchotersrozzaq.newsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vixchotersrozzaq.newsapp.NewsApplication
import com.vixchotersrozzaq.newsapp.models.Article
import com.vixchotersrozzaq.newsapp.models.NewsResponse
import com.vixchotersrozzaq.newsapp.repository.NewsRepository
import com.vixchotersrozzaq.newsapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application,
    val newsRepository: NewsRepository //parameter
) : AndroidViewModel(app){

    //LIVEDATA OBJECT
    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val homeNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    //Pagination
    var breakingNewsPage= 1
    var homeNewsPage= 1
    var breakingNewsResponse : NewsResponse? = null
    var searchNewsResponse : NewsResponse? = null


    init {
        getBreakingNews("in")
    }


    fun getBreakingNews(countryCode: String)= viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }


    fun homeNews(searchQuery: String)= viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                if (breakingNewsResponse== null){
                    breakingNewsResponse= resultResponse //if first page save the result to the response
                }else{
                    val oldArticles= breakingNewsResponse?.articles //else, add all articles to old
                    val newArticle= resultResponse.articles //add new response to new
                    oldArticles?.addAll(newArticle) //add new articles to old articles
                }
                return  Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                homeNewsPage++
                if (searchNewsResponse== null){
                    searchNewsResponse= resultResponse //if first page save the result to the response
                }else{
                    val oldArticles= searchNewsResponse?.articles //else, add all articles to old
                    val newArticle= resultResponse.articles //add new response to new
                    oldArticles?.addAll(newArticle) //add new articles to old articles
                }
                return  Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    /*
    function untuk save articles ke db: coroutine
     */
    fun saveArticle(article: Article)= viewModelScope.launch {
        newsRepository.upsert(article)
    }

    /*
    function untuk get semua saved news articles
     */
    fun getSavedArticle()= newsRepository.getSavedNews()

    /*
    function untuk delete article dari db
     */
    fun deleteSavedArticle(article: Article)= viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    private suspend fun safeBreakingNewsCall(countryCode: String){
        breakingNews.postValue(Resource.Loading())
        try{
            if (hasInternetConnection()){
                val response= newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                //handling response
                breakingNews.postValue(handleBreakingNewsResponse(response))
            }else{
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }

        } catch (t: Throwable){
            when(t){
                is IOException-> breakingNews.postValue(Resource.Error("Network Failure"))
                else-> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(searchQuery: String){
        homeNews.postValue(Resource.Loading())
        try{
            if (hasInternetConnection()){
                val response= newsRepository.homeNews(searchQuery, homeNewsPage)
                //handling response
                homeNews.postValue(handleSearchNewsResponse(response))
            }else{
                homeNews.postValue(Resource.Error("No Internet Connection"))
            }

        } catch (t: Throwable){
            when(t){
                is IOException-> homeNews.postValue(Resource.Error("Network Failure"))
                else-> homeNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }


    private fun hasInternetConnection(): Boolean{
        val connectivityManager= getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork= connectivityManager.activeNetwork?: return false
        val capabilities= connectivityManager.getNetworkCapabilities(activeNetwork)?: return false

        return when{
            capabilities.hasTransport(TRANSPORT_WIFI)-> true
            capabilities.hasTransport(TRANSPORT_CELLULAR)-> true
            capabilities.hasTransport(TRANSPORT_ETHERNET)->true
            else -> false
        }
    }
}