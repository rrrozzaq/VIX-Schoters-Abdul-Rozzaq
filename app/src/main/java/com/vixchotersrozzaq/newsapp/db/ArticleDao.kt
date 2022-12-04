package com.vixchotersrozzaq.newsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vixchotersrozzaq.newsapp.models.Article

/*
Data Access Object
untuk mengakses database
Di sini saya mendefinisikan fungsi untuk mengakses database lokal
simpan artikel, baca artikel, hapus artikel
 */
@Dao //annotate to let room know that this is the interface that defines the function

interface ArticleDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long // function(parameter):return --> here we return ID



    @Query("SELECT* FROM articles")
    fun getAllArticles():LiveData<List<Article>>


    /*
    Delete function
     */
    @Delete
    suspend fun deleteArticle(article: Article)
}