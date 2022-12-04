package com.vixchotersrozzaq.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vixchotersrozzaq.newsapp.R
import com.vixchotersrozzaq.newsapp.adapters.NewsAdapter
import com.vixchotersrozzaq.newsapp.ui.MainActivity
import com.vixchotersrozzaq.newsapp.ui.NewsViewModel
import com.vixchotersrozzaq.newsapp.utils.Constants
import com.vixchotersrozzaq.newsapp.utils.Constants.Companion.SEARCH_DELAY
import com.vixchotersrozzaq.newsapp.utils.Resource
import kotlinx.android.synthetic.main.fragment_home_news.*

import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeNewsFragment : Fragment (R.layout.fragment_home_news) {

    lateinit var viewModel: NewsViewModel
    val TAG= "HomeNewsFragment"
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set viewModels to fragment activity
        //and we cast that as MainActivity so that we can have access to the view model created at MainActivity
        viewModel= (activity as MainActivity).viewModel
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle= Bundle().apply{
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_homeNewsFragment_to_articleFragment,
                bundle
            )
        }

        //implementing search delay
        var job: Job? = null
        etSearch.addTextChangedListener {editable->
            job?.cancel() //cancel current job

            //new job
            job= MainScope().launch {
                delay(SEARCH_DELAY)

                //after delay, check editable for null,
                editable?.let {
                    if (editable.toString().isNotEmpty()){
                        viewModel.homeNews(editable.toString())
                    }
                }
            }
        }

        //subscribe to live data
        viewModel.homeNews.observe(viewLifecycleOwner, Observer { response->
            when(response){
                is Resource.Success->{
                    hideProgressBar()
                    //check null
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages= newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage= viewModel.homeNewsPage == totalPages
                    }
                }
                is Resource.Error->{
                    hideProgressBar()
                    response.message?.let { message->
                        Log.e(TAG, "An error occurred: $message" )
                        Toast.makeText(activity, "An error occurred: $message", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading->{
                    showProgressBar()
                }
            }
        })
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility= View.VISIBLE
        isLoading= false
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility= View.INVISIBLE
        isLoading= false
    }

    private fun setupRecyclerView(){
        newsAdapter= NewsAdapter()
        rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@HomeNewsFragment.scrollListener)
        }
    }


    var isLoading= false
    var isLastPage= false
    var isScrolling= false
    val scrollListener= object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            //menghitung payout numbers untuk pagination
            val layoutManager= recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition= layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount= layoutManager.childCount
            val totalItemCount= layoutManager.itemCount

            val  isNotLoadingAndNotLastPage= !isLoading && !isLastPage
            val isAtLastItem= firstVisibleItemPosition+ visibleItemCount >= totalItemCount
            val isNotAtBeginning= firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible= totalItemCount >= Constants.QUERY_PAGE_SIZE

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate){
                viewModel.homeNews(etSearch.toString())
                isScrolling= false
            }else{
                rvSearchNews.setPadding(0,0,0,0)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling= true
            }
        }
    }
}