package com.dicoding.asclepius.view.news

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.Result
import com.dicoding.asclepius.databinding.ActivityNewsBinding
import com.dicoding.asclepius.utils.Utils.displayToast
import com.dicoding.asclepius.view.MainViewModel
import com.dicoding.asclepius.view.ViewModelFactory
import com.dicoding.asclepius.view.adapter.NewsAdapter

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var rvNews: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarNews)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        rvNews = binding.rvNewsActivity
        rvNews.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        newsAdapter = NewsAdapter(viewModel)
        rvNews.setHasFixedSize(true)
        rvNews.adapter = newsAdapter

        viewModel.getNews().observe(this, { newsList ->
            if (newsList != null) {
                when (newsList) {
                    Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        newsAdapter.setNewsList(newsList.data)
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        displayToast(this, newsList.error)
                    }

                    else -> {
                        binding.progressBar.visibility = View.GONE
                        displayToast(this, getString(R.string.unknown_error))
                    }
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}