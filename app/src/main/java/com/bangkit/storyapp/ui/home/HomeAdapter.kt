package com.bangkit.storyapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.storyapp.data.model.Story
import com.bangkit.storyapp.databinding.ItemStoryBinding
import com.bumptech.glide.Glide

class HomeAdapter(
    private val onItemClickListener: OnItemClickListener
) : ListAdapter<Story, HomeAdapter.ItemViewHolder>(
    object : DiffUtil.ItemCallback<Story>() {
        override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStoryBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(
        private val itemBinding: ItemStoryBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(data: Story) {
            itemBinding.tvItemName.text = data.name
            Glide.with(itemView)
                .load(data.photoUrl)
                .fitCenter()
                .into(itemBinding.ivItemPhoto)

            itemBinding.root.setOnClickListener {
                onItemClickListener.onItemClick(data.id)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(storyId: String)
    }
}