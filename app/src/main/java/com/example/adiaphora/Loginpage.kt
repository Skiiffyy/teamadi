package com.example.adiaphora

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.adiaphora.databinding.ActivityLoginpageBinding
import com.example.adiaphora.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView

@SuppressLint("CheckResult")

class Loginpage : AppCompatActivity() {
    private lateinit var binding: ActivityLoginpageBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginpageBinding.inflate(layoutInflater)
        setContentView(binding.root)


//Auth
        auth = FirebaseAuth.getInstance()

        val emailStream = RxTextView.textChanges(binding.enterEmail)
            .skipInitialValue()
            .map {
                    email ->
               email.isEmpty()
            }
        emailStream.subscribe {
            showTextMinimalAlert(it, "email")
        }

        val passwordStream = RxTextView.textChanges(binding.enterPass)
            .skipInitialValue()
            .map {
                    password ->
                password.isEmpty()
            }
        passwordStream.subscribe {
            showTextMinimalAlert(it, "Password")
        }

        val invalidFieldStream = io.reactivex.Observable.combineLatest(
            emailStream,
            passwordStream,
            {  emailInvalid: Boolean, passwordInvalid: Boolean,  ->
                 !emailInvalid && !passwordInvalid
            })
        invalidFieldStream.subscribe { isValid ->
            if (isValid) {
                binding.btn2.isEnabled = true
                binding.btn2.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.primary_color)
            } else {
                binding.btn2.isEnabled = false
                binding.btn2.backgroundTintList =
                    ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }
        }
        binding.btn2.setOnClickListener {
            val email = binding.enterEmail.text.toString().trim()
            val password =binding.enterPass.text.toString().trim()
            loginUser (email,password)

        }
        binding.btn3.setOnClickListener {
            startActivity(Intent(this,Signupactivity::class.java))
        }

binding.forgotPw.setOnClickListener {
    startActivity(Intent(this, ForgotPasswordActivity::class.java))
}
        }


private fun showTextMinimalAlert(isNotValid: Boolean, text: String){
    if (text == "email")
        binding.enterEmail.error = if (isNotValid) "$text Cannot be empty" else null
    else if (text == "Password")
        binding.enterPass.error = if (isNotValid) "$text Cannot be empty" else null


}




    private fun loginUser (email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){login ->
                if (login.isSuccessful){
                    Intent(this, Homeactivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                        Toast.makeText(this, "Login Successfully!", Toast.LENGTH_SHORT).show()
                    }

                } else{
                    Toast.makeText(this, login.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

}