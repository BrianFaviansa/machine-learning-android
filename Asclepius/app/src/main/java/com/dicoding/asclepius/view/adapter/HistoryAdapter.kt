package com.dicoding.asclepius.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.databinding.ItemHistoryBinding
import com.dicoding.asclepius.utils.Utils.formatHistoryCardDate
import com.dicoding.asclepius.utils.Utils.formatPercent
import com.dicoding.asclepius.view.HistoryViewModel

class HistoryAdapter(private val viewModel: HistoryViewModel) :
    RecyclerView.Adapter<HistoryAdapter.MyHistoryViewHolder>() {
    private val historyList = mutableListOf<HistoryEntity>()

    inner class MyHistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryEntity) {
            with(binding) {
                historyCategory.text = history.category
                historyScore.text = formatPercent(history.confidenceScore)
                historyTimeStamp.text = formatHistoryCardDate(history.timestamp)
                Glide.with(itemView.context)
                    .load(history.imageUri)
                    .into(historyPhoto)
            }
        }
    }

    fun setHistoryList(historyList: List<HistoryEntity>) {
        val diffCallback = HistoryDiffCallback(this.historyList, historyList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.historyList.clear()
        this.historyList.addAll(historyList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyHistoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: MyHistoryViewHolder, position: Int) {
        val history = historyList[position]
        holder.bind(history)
    }

    private class HistoryDiffCallback(
        private val oldHistoryList: List<HistoryEntity>,
        private val newHistoryList: List<HistoryEntity>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldHistoryList.size
        }

        override fun getNewListSize(): Int {
            return newHistoryList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldHistoryList[oldItemPosition].id == newHistoryList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldHistoryList[oldItemPosition] == newHistoryList[newItemPosition]
        }
    }
}