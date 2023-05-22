package com.raida.intermediatesubmission.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.raida.intermediatesubmission.api.ApiService
import com.raida.intermediatesubmission.model.ListStoryItem
import com.raida.intermediatesubmission.paging.StoriesPagingSource

class StoryRepository(private val apiService: ApiService) {

    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoriesPagingSource(apiService, token)
            }
        ).liveData
    }
}