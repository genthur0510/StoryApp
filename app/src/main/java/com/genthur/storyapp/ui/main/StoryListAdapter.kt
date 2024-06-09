package com.genthur.storyapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.genthur.storyapp.databinding.ItemCardBinding
import com.genthur.storyapp.data.remote.response.ListStoryItem

class StoryListAdapter: PagingDataAdapter<ListStoryItem, StoryListAdapter.StoryListViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallBack

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallBack) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoryListViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryListViewHolder, position: Int) {
        val stories = getItem(position)
        stories?.let { holder.bind(it) }

        holder.itemView.setOnClickListener {
            if (stories != null) {
                onItemClickCallback.onItemClicked(stories)
            }
        }
    }

    class StoryListViewHolder(private val binding: ItemCardBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(stories: ListStoryItem) {
            Glide.with(binding.ivStories.context)
                .load(stories.photoUrl)
                .into(binding.ivStories)
            binding.tvTitle.text = stories.name
            binding.tvDescription.text = stories.description
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    interface OnItemClickCallBack {
        fun onItemClicked(data: ListStoryItem)
    }
}