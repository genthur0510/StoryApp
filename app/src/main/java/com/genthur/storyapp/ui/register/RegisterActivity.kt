package com.genthur.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.genthur.storyapp.R
import com.genthur.storyapp.util.ViewModelFactory
import com.genthur.storyapp.databinding.ActivityRegisterBinding
import com.genthur.storyapp.ui.login.LoginActivity
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.hide()
        playAnimation()

        registerViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        binding.btnSubmitRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString().trim()
            val email = binding.edRegisterEmail.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString().trim()

            lifecycleScope.launch {
                try {
                    val message = registerViewModel.registerUser(name, email, password)
                    Log.d(message.toString(), "message: ")
                } catch (e: HttpException) {
                    Toast.makeText(this@RegisterActivity, "Register failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        registerViewModel.registerResponse.observe(this) { response ->
            if (response.error == false) {
                Toast.makeText(this@RegisterActivity, "Registration success!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            } else {
                Toast.makeText(this@RegisterActivity, "Registration failed: ${response.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.tvRegisterTitle, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(binding.textInputLayoutNameRegister, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.textInputLayoutEmailRegister, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.textInputLayoutPasswordRegister, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.btnSubmitRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, name, email, password, register)
            start()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}