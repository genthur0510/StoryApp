package com.genthur.storyapp.ui.detailstory

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.genthur.storyapp.R
import com.genthur.storyapp.util.ViewModelFactory
import com.genthur.storyapp.databinding.ActivityDetailStoryBinding
import com.genthur.storyapp.data.remote.response.ListStoryItem
import com.genthur.storyapp.ui.main.MainActivity
import com.genthur.storyapp.ui.main.MainViewModel

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)

        val storyItem = intent.getParcelableExtra<ListStoryItem>(MainActivity.EXTRA_DETAIL)
        storyItem?.let {
            Glide.with(binding.ivDetailStory.context)
                .load(storyItem.photoUrl)
                .into(binding.ivDetailStory)
            binding.tvTitleDescription.text = storyItem.name
            binding.tvDescriptionDetail.text = storyItem.description
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }
}