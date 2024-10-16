package com.dicoding.asclepius.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.local.entity.NewsEntity
import com.dicoding.asclepius.databinding.ItemNewsBinding
import com.dicoding.asclepius.utils.Utils.formatCardDate
import com.dicoding.asclepius.view.MainViewModel

class NewsAdapter(private val viewModel: MainViewModel) :
    RecyclerView.Adapter<NewsAdapter.MyNewsViewHolder>() {
    private val newsList = mutableListOf<NewsEntity>()

    inner class MyNewsViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(news: NewsEntity) {
            with(binding) {
                newsTitle.text = news.title
                newsDescription.text = news.description
                newsAuthor.text = news.author
                newsDate.text = formatCardDate(news.publishedAt.toString())
                Glide.with(itemView.context)
                    .load(news.urlToImage)
                    .into(newsPhoto)
            }
        }
    }

    fun setNewsList(newsList: List<NewsEntity>) {
        val diffCallback = NewsDiffCallback(newsList, newsList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.newsList.clear()
        this.newsList.addAll(newsList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyNewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyNewsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: MyNewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.bind(news)
    }

    private class NewsDiffCallback(
        private val oldNewsList: List<NewsEntity>,
        private val newNewsList: List<NewsEntity>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldNewsList.size
        override fun getNewListSize(): Int = newNewsList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldNewsList[oldItemPosition].id == newNewsList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldNewsList[oldItemPosition] == newNewsList[newItemPosition]
        }
    }
}