package com.raida.intermediatesubmission.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.raida.intermediatesubmission.R
import com.raida.intermediatesubmission.viewmodel.RegisterViewModel
import com.raida.intermediatesubmission.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setViewModel()
        setRegister()
        playAnimation()

    }

    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.imgRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val name = ObjectAnimator.ofFloat(binding.nameEditTextLayout,View.ALPHA, 1f ).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.emailEditTextLayout,View.ALPHA, 1f ).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.tvRegisterTitle, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, name, email, password, register)
            startDelay = 500
            start()
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

    private fun setViewModel(){
        registerViewModel = ViewModelProvider(this@RegisterActivity)[RegisterViewModel::class.java]
        registerViewModel.apply {
            status.observe(this@RegisterActivity){ error ->
                if (!error){
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                    Toast.makeText(this@RegisterActivity, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@RegisterActivity, getString(R.string.register_failed), Toast.LENGTH_SHORT).show()
                }
            }

            isLoading.observe(this@RegisterActivity){
                showLoading(it)
            }
        }
    }

    private fun setRegister(){
        binding.apply {
            binding.btnRegister.setOnClickListener{
                val name = edRegisterName.text.toString().trim()
                val email = edRegisterEmail.text.toString().trim()
                val password = edRegisterPassword.text.toString().trim()

                when{
                    name.isEmpty() ->{
                        binding.edRegisterName.error = getString(R.string.name_error)
                    }
                    email.isEmpty() -> {
                        binding.edRegisterEmail.error = getString(R.string.email_error)
                    }
                    password.isEmpty() -> {
                        binding.edRegisterPassword.error = getString(R.string.password_empty_error)
                    }
                    password.length < 8 -> {
                        Toast.makeText(this@RegisterActivity, getString(R.string.password_error), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        registerViewModel.register(name, email, password)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}