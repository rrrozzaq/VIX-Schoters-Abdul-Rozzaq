package com.vixchotersrozzaq.newsapp.db

import android.content.Context
import androidx.room.*
import com.vixchotersrozzaq.newsapp.models.Article


@Database(
    entities = [Article::class],
    version = 1
)

@TypeConverters(Converters::class)

abstract class ArticleDatabase : RoomDatabase(){

    //function to return ArticleDao
    abstract fun getArticleDao(): ArticleDao

    //companion object untuk membuat database
    companion object{

        @Volatile
        private var instance: ArticleDatabase? =null
        //variabel LOCK untuk menyinkronkan instance, untuk memastikan hanya ada satu instance ArticleDatabase sekaligus
        private val LOCK= Any()

        //sinkronisasi instance jika null
        operator fun invoke(context: Context) = instance?: synchronized(LOCK){
            // null check "to make sure that there is not another thread that sets the instance to something while we already set it
            instance ?: createDatabase(context).also{ instance = it }

        }

        private fun createDatabase(context: Context)=
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()

    }
}