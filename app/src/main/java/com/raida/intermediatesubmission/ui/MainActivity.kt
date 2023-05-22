package com.raida.intermediatesubmission.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.raida.intermediatesubmission.R
import com.raida.intermediatesubmission.adapter.LoadingStateAdapter
import com.raida.intermediatesubmission.adapter.StoriesAdapter
import com.raida.intermediatesubmission.api.ApiConfig
import com.raida.intermediatesubmission.databinding.ActivityMainBinding
import com.raida.intermediatesubmission.preference.UserPreference
import com.raida.intermediatesubmission.repository.StoryRepository
import com.raida.intermediatesubmission.viewmodel.MainViewModel
import com.raida.intermediatesubmission.viewmodel.MainViewModelFactory


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storiesAdapter: StoriesAdapter
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }
        setViewModel()
        setRecyclerView()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                mainViewModel.logout()
                return true
            }
            R.id.menu_maps -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                return true
            }
            else -> true
        }
    }

    private fun setRecyclerView() {
        storiesAdapter = StoriesAdapter()
        binding.rvListStories.adapter = storiesAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storiesAdapter.retry()
            }
        )
        binding.rvListStories.setHasFixedSize(true)
        binding.rvListStories.layoutManager = LinearLayoutManager(this@MainActivity)
    }

    private fun setViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        val apiService = ApiConfig.getApiService()
        val storyRepository = StoryRepository(apiService)
        mainViewModel = ViewModelProvider(this,
            MainViewModelFactory(pref, storyRepository))[MainViewModel::class.java]
        mainViewModel.apply {
            getUser().observe(this@MainActivity) { preference ->
                if (preference.name.isNotEmpty() && preference.token.isNotEmpty() && preference.userId.isNotEmpty()) {
                    val token = getString(R.string.bearer) + preference.token
                    listStories(token).observe(this@MainActivity) { listStories ->
                        storiesAdapter.submitData(lifecycle, listStories)
                        Log.d("tes", "$listStories")
                    }
                } else {
                    val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }

    }

}