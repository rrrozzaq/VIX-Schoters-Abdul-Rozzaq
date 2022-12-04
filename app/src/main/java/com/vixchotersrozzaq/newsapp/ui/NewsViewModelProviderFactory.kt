package com.vixchotersrozzaq.newsapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vixchotersrozzaq.newsapp.repository.NewsRepository
/*
Untuk menentukan bagaimana model tampilan yang harus dibuat
 */
class NewsViewModelProviderFactory(
    val app: Application,
    val newsRepository: NewsRepository
    ) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(app, newsRepository) as T
    }
}