package com.vixchotersrozzaq.newsapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "articles"
)
data class Article(
    @PrimaryKey(autoGenerate = true) //menambahkan id sebagai kunci utama ke tabel dan class
    var id: Int?= null, //tidak semua artikel memiliki id, jadi setel ke null
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String?,
    val urlToImage: String?
) : Serializable
//serialisasi untuk mengirim seluruh class dengan bundle