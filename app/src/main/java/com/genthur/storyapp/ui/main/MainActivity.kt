package com.genthur.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.genthur.storyapp.R
import com.genthur.storyapp.util.ViewModelFactory
import com.genthur.storyapp.databinding.ActivityMainBinding
import com.genthur.storyapp.data.remote.response.ListStoryItem
import com.genthur.storyapp.ui.addstory.AddStoryActivity
import com.genthur.storyapp.ui.detailstory.DetailStoryActivity
import com.genthur.storyapp.ui.landing.LandingActivity
import com.genthur.storyapp.ui.login.LoginViewModel
import com.genthur.storyapp.ui.map.MapsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryListAdapter
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mainViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        loginViewModel.getSession().observe(this) { user ->
            user?.let {
                if (it.isLoggedIn) {
                    token = it.token
                    Log.d("MainActivity", "Token: $token")
                    setupRecyclerView()
                    mainViewModel.getStories("Bearer $token").observe(this) {
                        adapter.submitData(lifecycle, it)
                        showLoading(false)
                    }
                } else {
                    val intent = Intent(this@MainActivity, LandingActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } ?: run {
                val intent = Intent(this@MainActivity, LandingActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.fabAddStory.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                loginViewModel.logout()
                true
            } R.id.menu_map -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        if (token.isNotEmpty()) {
            mainViewModel.getStories("Bearer $token").observe(this) {
                adapter.submitData(lifecycle, it)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = StoryListAdapter()
        binding.rvListStory.layoutManager = LinearLayoutManager(this)
        binding.rvListStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        adapter.setOnItemClickCallback(object : StoryListAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: ListStoryItem) {
                val intent = Intent(this@MainActivity, DetailStoryActivity::class.java)
                intent.putExtra(EXTRA_DETAIL, data)
                startActivity(intent)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }
}
