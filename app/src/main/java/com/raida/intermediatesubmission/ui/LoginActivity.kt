package com.raida.intermediatesubmission.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.raida.intermediatesubmission.*
import com.raida.intermediatesubmission.databinding.ActivityLoginBinding
import com.raida.intermediatesubmission.preference.UserPreference
import com.raida.intermediatesubmission.viewmodel.LoginViewModel
import com.raida.intermediatesubmission.viewmodel.ViewModelFactory

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edLoginPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }

        })

        setLogin()
        setupView()
        setViewModel()
        playAnimation()
    }

    private fun setMyButtonEnable(){
        val result = binding.edLoginPassword.text
        binding.btnLogin.isEnabled = result != null && result.toString().isNotEmpty()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imgLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val email =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.tvLoginTitle, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, email, password, login)
            startDelay = 500
            start()
        }
    }

    private fun setViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        loginViewModel = ViewModelProvider(this, ViewModelFactory(pref))[LoginViewModel::class.java]

        loginViewModel.apply {
            status.observe(this@LoginActivity) { error ->
                if (!error) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                    Toast.makeText(this@LoginActivity,
                        getString(R.string.login_success_text),
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@LoginActivity,
                        getString(R.string.login_failure_text),
                        Toast.LENGTH_SHORT).show()
                }
            }
            isLoading.observe(this@LoginActivity) {
                showLoading(it)
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setLogin() {
        binding.apply {

            btnLogin.setOnClickListener {
                val email = edLoginEmail.text.toString().trim()
                val password = edLoginPassword.text.toString().trim()
                when {
                    email.isEmpty() -> {
                        edLoginEmail.error = getString(R.string.email_error)
                    }
                    password.isEmpty() -> {
                        edLoginPassword.error = getString(R.string.password_empty_error)
                    }
                    password.length < 8 -> {
                        Toast.makeText(this@LoginActivity, getString(R.string.password_error), Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        loginViewModel.login(email, password)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}