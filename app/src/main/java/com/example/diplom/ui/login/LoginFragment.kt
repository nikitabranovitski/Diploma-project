package com.example.diplom.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.example.diplom.R
import com.example.diplom.databinding.FragmentLoginBinding
import com.example.diplom.ui.MainActivity
import com.example.diplom.ui.home.HomeActivity
import com.example.diplom.ui.profile.MyProfileFragmentDirections
import com.example.diplom.ui.registration.RegistrationFragment
import com.example.diplom.ui.resetpassword.ResetPasswordFragment
import com.example.diplom.util.passwordPattern
import com.example.diplom.util.FLAG
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel by viewModels<LoginViewModel>()

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var navController: NavController

    private var login: String = ""
    private var isLoginEntered = false
    private var pass: String = ""
    private var isPassEntered = false

    companion object {
        private const val RS_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN"
    }

    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        signInWithGoogle()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.apply {
            entry = {
                if (it) {
                    requireActivity().run {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.registrationTextView.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, RegistrationFragment()).commit()
        }

        binding.loginEditText.addTextChangedListener(ValidationTextWatcher {
            it.trim()
            if (Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                isLoginEntered = true
                login = it
                binding.loginWarning.visibility = View.GONE
            } else {
                isLoginEntered = false
                binding.loginWarning.visibility = View.VISIBLE
            }
            binding.signInButton.isEnabled = isLoginEntered && isPassEntered
        })

        binding.passwordEditText.addTextChangedListener(ValidationTextWatcher {
            it.trim()
            if (passwordPattern.matcher(it).matches()) {
                isPassEntered = true
                pass = it
                binding.passwordWarning.visibility = View.GONE
            } else {
                isPassEntered = false
                binding.passwordWarning.visibility = View.VISIBLE
            }
            binding.signInButton.isEnabled = isLoginEntered && isPassEntered

        })
        binding.signInButton.setOnClickListener {
            viewModel.login(login, pass)

        }

        binding.googleSignInButton.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RS_SIGN_IN)
        }

        binding.resetPasswordTextView.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, ResetPasswordFragment()).commit()
        }
        viewModel.checkUser()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RS_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                val account = task.getResult(ApiException::class.java)!!
                viewModel.loginWithGoogle(account.idToken!!)
                startActivity(Intent(requireContext(),HomeActivity::class.java))
            } else {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                Log.w(TAG, "Google sign in failed")
            }
        }
    }
}