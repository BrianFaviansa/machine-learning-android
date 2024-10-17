package com.dicoding.asclepius.view.history

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.Result
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.utils.Utils.displayToast
import com.dicoding.asclepius.view.HistoryViewModel
import com.dicoding.asclepius.view.HistoryViewModelFactory
import com.dicoding.asclepius.view.MainViewModel
import com.dicoding.asclepius.view.ViewModelFactory
import com.dicoding.asclepius.view.adapter.HistoryAdapter

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private val viewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory.getInstance(this)
    }

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var rvHistory: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarHistory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupRecyclerView() {
        rvHistory = binding.rvHistoryActivity
        rvHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        historyAdapter = HistoryAdapter(viewModel)
        rvHistory.setHasFixedSize(true)
        rvHistory.adapter = historyAdapter

        viewModel.getHistories().observe(this) { histories ->
            if (histories != null) {
                when (histories) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        historyAdapter.setHistoryList(histories.data)
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        displayToast(this, histories.error)
                    }

                    is Result.SuccessMessage -> {
                        binding.progressBar.visibility = View.GONE
                        displayToast(this, histories.message)
                    }
                }
            }
        }
    }
}