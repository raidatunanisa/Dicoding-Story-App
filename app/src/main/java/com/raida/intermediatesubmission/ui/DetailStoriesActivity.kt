package com.raida.intermediatesubmission.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.raida.intermediatesubmission.*
import com.raida.intermediatesubmission.databinding.ActivityDetailStoriesBinding
import com.raida.intermediatesubmission.helper.formatDate
import com.raida.intermediatesubmission.model.Story
import com.raida.intermediatesubmission.preference.UserPreference
import com.raida.intermediatesubmission.viewmodel.DetailViewModel
import com.raida.intermediatesubmission.viewmodel.ViewModelFactory

class DetailStoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoriesBinding
    private lateinit var detailViewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewModel()
        supportActionBar?.title = getString(R.string.detail_title)
    }

    private fun setViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        detailViewModel = ViewModelProvider(this@DetailStoriesActivity,
            ViewModelFactory(pref))[DetailViewModel::class.java]

        detailViewModel.apply {
            getUser().observe(this@DetailStoriesActivity) { preference ->
                val id = intent.getStringExtra(EXTRA_ID)
                val token = getString(R.string.bearer) + preference.token
                if (id != null) {
                    getDetailStory(token, id)
                }
            }

            detailStory.observe(this@DetailStoriesActivity) { Story ->
                setDetailStory(Story)
            }

            isLoading.observe(this@DetailStoriesActivity) {
                showLoading(it)
            }
        }
    }

    private fun setDetailStory(story: Story) {
        binding.apply {
            Glide.with(imgDetail)
                .load(story.photoUrl)
                .into(imgDetail)
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
            tvCreateDate.text = getString(R.string.date_text, formatDate(story.createdAt))
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}