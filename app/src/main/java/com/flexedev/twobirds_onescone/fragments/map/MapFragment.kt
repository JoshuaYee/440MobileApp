package com.flexedev.twobirds_onescone.fragments.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.flexedev.twobirds_onescone.R
import com.flexedev.twobirds_onescone.navigation.NavigationBar


class MapFragment : Fragment() {

    private lateinit var bottomNavBar: NavigationBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val inflated =  inflater.inflate(R.layout.fragment_map, container, false)

        // Inflate the fragment container with the map.
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frag_container, GoogleMapFragment.newInstance())
        transaction.addToBackStack(null)
        transaction.commit()

        return inflated
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavBar = NavigationBar(view.findViewById(R.id.bottomNavigationView), requireActivity(), this, findNavController())
        bottomNavBar.setSelected(R.id.map_nav_icon)

    }


}