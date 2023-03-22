package com.example.adiaphora

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Observable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.adiaphora.databinding.ActivityMainBinding
import com.example.adiaphora.databinding.ActivitySignupactivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView
import java.util.stream.Stream

@SuppressLint("CheckResult")

class Signupactivity : AppCompatActivity() {
    private lateinit var binding:ActivitySignupactivityBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


//Auth
       auth = FirebaseAuth.getInstance()


// fullname Validation

        val nameStream = RxTextView.textChanges(binding.enterFullname)
            .skipInitialValue()
            .map { name ->
                name.isEmpty()
            }
        nameStream.subscribe {
            showNameExistAlert(it)
        }

        val emailStream = RxTextView.textChanges(binding.enterEmail1)
            .skipInitialValue()
            .map {
                email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailStream.subscribe {
            showEmailValidAlert(it)
        }
        val passwordStream = RxTextView.textChanges(binding.enterPass)
            .skipInitialValue()
            .map {
                password ->
                password.length <6
            }
        passwordStream.subscribe {
            showTextMinimalAlert(it, "Password")
        }
        val passwordConfirmStream = io.reactivex.Observable.merge(
            RxTextView.textChanges(binding.enterPass)
                .skipInitialValue()
                .map {
                    password ->
                    password.toString() != binding.enterRepass.text.toString()
                },
            RxTextView.textChanges(binding.enterRepass)
                .skipInitialValue()
                .map {
                    confirmpassword ->
                    confirmpassword.toString() !=binding.enterPass.text.toString()
                })
passwordConfirmStream.subscribe {
    showPasswordConfirmAlert(it)
}
        val invalidFieldStream = io.reactivex.Observable.combineLatest(
            nameStream,
            emailStream,
            passwordStream,
            passwordConfirmStream,
            { nameInvalid: Boolean, emailInvalid: Boolean, passwordInvalid: Boolean, passwordConfirmInvalid: Boolean ->
                !nameInvalid && !emailInvalid && !passwordInvalid && !passwordConfirmInvalid
            })
        invalidFieldStream.subscribe { isValid ->
            if (isValid) {
                binding.btn3.isEnabled = true
                binding.btn3.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.primary_color)
            } else {
                binding.btn3.isEnabled = false
                binding.btn3.backgroundTintList =
                    ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }
        }
//click
        binding.btn3.setOnClickListener {
           val email = binding.enterEmail1.text.toString().trim()
            val password =binding.enterPass.text.toString().trim()
            registerUser(email, password)
        }
    }


    private fun showNameExistAlert(isNotValid: Boolean){
        binding.enterFullname.error = if(isNotValid) "Name cannot be empty!" else null
    }



    private fun showTextMinimalAlert(isNotValid: Boolean, text: String){
        if (text == "email")
            binding.enterPass.error = if (isNotValid) "$text Must be more than 6 characters" else null
    }

    private fun showEmailValidAlert(isNotValid: Boolean){
        binding.enterEmail1.error = if(isNotValid) "Invalid email!" else null
    }
private fun showPasswordConfirmAlert(isNotValid: Boolean){
    binding.enterRepass.error = if(isNotValid) "Invalid Password" else null
    }


    private fun registerUser(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                        startActivity(Intent(this, Loginpage::class.java))
                    Toast.makeText(this, "REGISTERED SUCCESSFULLY!", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
}