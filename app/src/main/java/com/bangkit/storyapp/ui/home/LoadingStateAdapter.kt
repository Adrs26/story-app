package com.bangkit.storyapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.storyapp.R
import com.bangkit.storyapp.databinding.ItemLoadingBinding

class LoadingStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<LoadingStateAdapter.LoadingStateViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLoadingBinding.inflate(inflater, parent, false)
        return LoadingStateViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class LoadingStateViewHolder(
        private val itemBinding: ItemLoadingBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        init {
            itemBinding.ibRetry.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                val context = itemBinding.root.context
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.no_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
            itemBinding.pbHome.isVisible = loadState is LoadState.Loading
            itemBinding.ibRetry.isVisible = loadState is LoadState.Error
        }
    }
}