package com.major.qr.ui

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.major.qr.R
import com.major.qr.databinding.FragmentProfileBinding
import com.major.qr.models.User
import com.major.qr.viewmodels.ProfileViewModel
import java.util.Locale

class ProfileFragment : Fragment() {
    var binding: FragmentProfileBinding? = null
    var viewModel: ProfileViewModel? = null
    var editMode = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        viewModel!!.userDetails()
            .observe(viewLifecycleOwner) { (firstName, lastName, phoneNumber, _, country1, email): User ->
                binding!!.firstName.setText(
                    firstName
                )
                binding!!.lastName.setText(lastName)
                binding!!.profileEmail.setText(email)
                binding!!.country.setText(country1)
                binding!!.phoneNumber.setText(phoneNumber)
                val country = country1!!.lowercase(Locale.getDefault())
                when (country) {
                    "india" -> binding!!.countryFlag.text = "🇮🇳"
                    "america" -> binding!!.countryFlag.text = "🇺🇸"
                    "canada" -> binding!!.countryFlag.text = "🇨🇦"
                    "pakistan" -> binding!!.countryFlag.text = "🇵🇰"
                    else -> binding!!.countryFlag.text = "🏳"
                }
            }
        val list: ArrayList<EditText> = object : ArrayList<EditText>() {
            init {
                add(binding!!.firstName)
                add(binding!!.lastName)
                add(binding!!.profileEmail)
                add(binding!!.country)
                add(binding!!.phoneNumber)
            }
        }
        viewMode(list)
        binding!!.editProfileButton.setOnClickListener { view: View? ->
            if (editMode) {
                val user = User()
                user.firstName = binding!!.firstName.text.toString()
                user.lastName = binding!!.lastName.text.toString()
                user.email = binding!!.profileEmail.text.toString()
                user.country = binding!!.country.text.toString()
                user.phoneNumber = binding!!.phoneNumber.text.toString()
                user.state = "state"
                viewModel!!.updateUserDetails(user)
                    .observe(viewLifecycleOwner, Observer<Boolean?> { response: Boolean? ->
                        viewMode(list)
                        if (response == null) {
                            Toast.makeText(
                                requireContext(),
                                "Failed to update.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Observer
                        }
                        Toast.makeText(
                            requireContext(),
                            "Successfully updated!",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                return@setOnClickListener
            }
            editMode(list)
        }
        binding!!.logoutButton.setOnClickListener { v: View? ->
            if (editMode) {
                viewMode(list)
                return@setOnClickListener
            }
            val preferences = requireActivity().getSharedPreferences(
                requireActivity().packageName, Context.MODE_PRIVATE
            )
            preferences.edit().clear().apply()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        return binding!!.root
    }

    private fun viewMode(list: ArrayList<EditText>) {
        editMode = false
        binding!!.editProfileButton.text = "Edit Profile"
        binding!!.logoutButton.text = "Logout"
        binding!!.spacer.visibility = View.GONE
        for (editText in list) {
            editText.isEnabled = false
            editText.setBackgroundResource(android.R.color.transparent)
        }
    }

    private fun editMode(list: ArrayList<EditText>) {
        editMode = true
        binding!!.editProfileButton.text = "Save"
        binding!!.logoutButton.text = "Cancel"
        binding!!.spacer.visibility = View.VISIBLE
        for (editText in list) {
            editText.isEnabled = true
            editText.setBackgroundResource(R.drawable.input_background)
            editText.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.secondary_blue)
            )
        }
    }
}