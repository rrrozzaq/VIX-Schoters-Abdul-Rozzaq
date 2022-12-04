package com.vixchotersrozzaq.newsapp.repository

import com.vixchotersrozzaq.newsapp.api.RetrofitInstance
import com.vixchotersrozzaq.newsapp.db.ArticleDatabase
import com.vixchotersrozzaq.newsapp.models.Article

/*
get data dari database dan dari remote data source (retrofit api)
 */
class NewsRepository(
    val db: ArticleDatabase //parameter
) {

    /*
    function untuk queries api page breaking news
     */
    suspend fun getBreakingNews(countryCode:String, pageNumber:Int)=
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    /*
    function untuk queries api page home news
     */
    suspend fun homeNews(searchQuery: String, pageNumber: Int)=
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    /*
    function untuk insert article ke db
     */
    suspend fun upsert(article: Article)=
        db.getArticleDao().upsert(article)

    /*
    function untuk get saved news dari db
     */
    fun getSavedNews()=
        db.getArticleDao().getAllArticles()

    /*
    function untuk delete articles dari db
     */
    suspend fun deleteArticle(article: Article)=
        db.getArticleDao().deleteArticle(article)
}