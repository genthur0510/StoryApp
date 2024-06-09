package com.genthur.storyapp.ui.login

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
import com.genthur.storyapp.util.User
import com.genthur.storyapp.util.ViewModelFactory
import com.genthur.storyapp.databinding.ActivityLoginBinding
import com.genthur.storyapp.data.local.datastore.UserPreference
import com.genthur.storyapp.data.local.datastore.dataStore
import com.genthur.storyapp.ui.main.MainActivity
import com.genthur.storyapp.ui.register.RegisterActivity
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }
    private lateinit var pref: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.hide()
        pref = UserPreference(applicationContext.dataStore)
        val session = loginViewModel.getSession().value

        binding.tvToRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        loginViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        loginViewModel.loginResponse.observe(this) { response ->
            if (response.error == false) {
                Toast.makeText(this, "Login success!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            } else {
                Toast.makeText(this, "Login failed: ${response.message}", Toast.LENGTH_SHORT).show()
            }

            saveSession(
                User(
                    username = response.loginResult?.name.toString(),
                    token = AUTH_KEY + response.loginResult?.token.toString(),
                    isLoggedIn = true
                )
            )
        }

        binding.btnSubmitLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString().trim()

            lifecycleScope.launch {
                try {
                    val message = loginViewModel.login(email, password)
                    Log.d(message.toString(), "message: ")
                    loginViewModel.loginSession()
                } catch (e: HttpException) {
                    Toast.makeText(this@LoginActivity, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (session != null && session.isLoggedIn) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            return
        }

        playAnimation()
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.tvLoginTitle, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.textInputLayoutEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.textInputLayoutPassword, View.ALPHA, 1f).setDuration(500)
        val toRegister = ObjectAnimator.ofFloat(binding.tvToRegister, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.tvRegisterText, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnSubmitLogin, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(toRegister, register)
        }

        AnimatorSet().apply {
            playSequentially(title, email, password, together, login)
            start()
        }
    }

    private fun saveSession(user: User) {
        lifecycleScope.launch {
            loginViewModel.saveSession(user)
        }
        Log.d("SESSION", "Login session saved")
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val AUTH_KEY = " "
    }
}