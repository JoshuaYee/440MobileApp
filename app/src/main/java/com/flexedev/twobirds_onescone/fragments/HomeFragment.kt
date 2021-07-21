package com.flexedev.twobirds_onescone.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.flexedev.twobirds_onescone.helper.NotificationHelper
import com.flexedev.twobirds_onescone.R
import com.flexedev.twobirds_onescone.navigation.NavigationBar


class HomeFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var bottomNavBar: NavigationBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        loadData()
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.homeToReviewBtn)?.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_ratingFragment, null)
        )

        // Bottom navigation bar navigation
        bottomNavBar = NavigationBar(view.findViewById(R.id.bottomNavigationView), requireActivity(), this, findNavController())
        bottomNavBar.setSelected(R.id.home_nav_icon)

        NotificationHelper().scheduleNotification(requireContext(), sharedPreferences)
    }

    private fun loadData(): String? {
        sharedPreferences =
                requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("STRING_KEY", null)
    }



}