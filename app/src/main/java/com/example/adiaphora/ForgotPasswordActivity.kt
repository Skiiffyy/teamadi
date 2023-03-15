    package com.example.adiaphora

    import android.annotation.SuppressLint
    import android.content.Intent
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.util.Patterns
    import android.widget.Toast
    import androidx.core.content.ContextCompat
    import androidx.viewbinding.ViewBindings
    import com.example.adiaphora.R
    import com.example.adiaphora.databinding.ActivityForgotPasswordBinding
    import com.google.firebase.auth.FirebaseAuth
    import com.jakewharton.rxbinding2.widget.RxTextView
    @SuppressLint("CheckResult")
    class ForgotPasswordActivity : AppCompatActivity() {

        private lateinit var binding: ActivityForgotPasswordBinding
        private lateinit var auth: FirebaseAuth
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
            setContentView(binding.root)
    //auth
            auth = FirebaseAuth.getInstance()

    // email validation

            val emailStream = RxTextView.textChanges(binding.enterEmail1)
                .skipInitialValue()
                .map {
                        email ->
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                }
            emailStream.subscribe {
        showEmailValidAlert(it)
            }

            binding.btn4.setOnClickListener {
                val email =binding.enterEmail1.text.toString().trim()

                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(this){ reset ->
                        if (reset.isSuccessful){
                            Intent(this, Loginpage::class.java).also {
                                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(it)
                                Toast.makeText(this, "check email for password reset", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this,reset.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            }

    //click

            binding.tvBackLogin.setOnClickListener {
                startActivity(Intent(this, Loginpage::class.java))
            }

        }


        private fun showEmailValidAlert(isNotValid: Boolean){
        if (isNotValid){
            binding.enterEmail1.error = "invalid email!"
            binding.btn4.isEnabled = false
            binding.btn4.backgroundTintList =
                ContextCompat.getColorStateList(this, android.R.color.darker_gray)
        } else {
            binding.enterEmail1.error = null
            binding.btn4.isEnabled = true
            binding.btn4.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.primary_color)
        }

        }

    }