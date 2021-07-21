package com.flexedev.twobirds_onescone.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.flexedev.twobirds_onescone.R
import com.flexedev.twobirds_onescone.TwoBirdsLiveRoomApplication
import com.flexedev.twobirds_onescone.data.entities.SconeWithRatings
import com.flexedev.twobirds_onescone.navigation.NavigationBar
import com.flexedev.twobirds_onescone.viewModel.SconeViewModel
import com.flexedev.twobirds_onescone.viewModel.SconeViewModelFactory
import com.google.android.gms.maps.model.LatLng

class ShareReviewsFragment : Fragment() {
    private lateinit var bottomNavBar: NavigationBar
    private lateinit var sharingButton: ImageButton
    private var stringList: ArrayList<String> = arrayListOf()
    private var bigString: String = ""

    private val viewModel: SconeViewModel by activityViewModels {
        SconeViewModelFactory((activity?.application as TwoBirdsLiveRoomApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_share_reviews, container, false)
        setBottomNavigation(view)

        sharingButton = view.findViewById(R.id.shareReviewsBtn)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharingButton.setOnClickListener {
            generateSconesList()
        }
    }

    private fun generateSconesList() {
        var sconesList: List<SconeWithRatings>

        viewModel.scones.observe(viewLifecycleOwner) { newScones ->
            sconesList = newScones
            populateShareWithScones(sconesList)
            doTheShare()
        }

    }

    private fun populateShareWithScones(sconesList: List<SconeWithRatings>) {
        for (scone in sconesList) {
            val name = scone.scone.sconeName
            val place =
                if (scone.scone.sconeBusiness.isNotBlank()) scone.scone.sconeBusiness else "a secret location"
            val location = LatLng(scone.scone.latitude.toDouble(), scone.scone.longitude.toDouble())
            val score = scone.ratings[0].score
            val notes =
                if (scone.ratings[0].notes.isNotBlank()) scone.ratings[0].notes else "One does not simply just describe a scone"

            stringList.add(":fork_and_knife: $name, from $place , scored $score. Located at: $location. Notes: $notes")
            bigString += (":fork_and_knife: $name, from $place , scored $score. Located at: $location. Notes: $notes \n")
        }
    }

    private fun doTheShare() {
        val message =
            bigString
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun setBottomNavigation(view: View) {
        //Bottom Navigation Bar
        bottomNavBar = NavigationBar(
            view.findViewById(R.id.bottomNavigationView),
            requireActivity(),
            this,
            findNavController()
        )
        bottomNavBar.setSelected(R.id.share_nav_icon)
    }
}