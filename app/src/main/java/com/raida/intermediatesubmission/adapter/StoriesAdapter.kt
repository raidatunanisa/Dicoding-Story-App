package com.raida.intermediatesubmission.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.raida.intermediatesubmission.databinding.ItemStoriesBinding
import com.raida.intermediatesubmission.model.ListStoryItem
import com.raida.intermediatesubmission.ui.DetailStoriesActivity

class StoriesAdapter : PagingDataAdapter<ListStoryItem, StoriesAdapter.StoriesViewHolder>(
    DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoriesViewHolder {
        val binding = ItemStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoriesViewHolder(binding)
    }


    override fun onBindViewHolder(holder: StoriesViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }


    inner class StoriesViewHolder(private val binding: ItemStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listStories: ListStoryItem) {
            with(binding) {
                tvItemName.text = listStories.name
                tvItemDescription.text = listStories.description
                Glide.with(imgItemPhoto)
                    .load(listStories.photoUrl)
                    .into(imgItemPhoto)
                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailStoriesActivity::class.java)
                    intent.putExtra(EXTRA_ID, listStories.id)
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(imgItemPhoto, "image"),
                            Pair(tvItemName, "name"),
                            Pair(tvItemDescription, "description"),
                        )
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem,
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}