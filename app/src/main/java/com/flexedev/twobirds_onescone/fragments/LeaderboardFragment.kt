package com.flexedev.twobirds_onescone.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flexedev.twobirds_onescone.R
import com.flexedev.twobirds_onescone.RecyclerViewAdapter
import com.flexedev.twobirds_onescone.helper.SwipeToDelete
import com.flexedev.twobirds_onescone.TwoBirdsLiveRoomApplication
import com.flexedev.twobirds_onescone.data.entities.SconeWithRatings
import com.flexedev.twobirds_onescone.navigation.NavigationBar
import com.flexedev.twobirds_onescone.viewModel.SconeViewModel
import com.flexedev.twobirds_onescone.viewModel.SconeViewModelFactory

class LeaderboardFragment : Fragment(), RecyclerViewAdapter.OnSconeListener {
    private val viewModel: SconeViewModel by activityViewModels {
        SconeViewModelFactory((activity?.application as TwoBirdsLiveRoomApplication).repository)
    }
    private lateinit var bottomNavBar: NavigationBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)

        //Recycler View
        val sconeAdapter = RecyclerViewAdapter(listOf(), this)
        viewModel.scones.observe(viewLifecycleOwner) { newScones ->
            sconeAdapter.setScone(newScones)
        }
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.adapter = sconeAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        val itemTouchHelper = ItemTouchHelper(SwipeToDelete(sconeAdapter, viewModel))
        itemTouchHelper.attachToRecyclerView(recyclerView)

        setHasOptionsMenu(true)

        // Bottom navigation bar navigation
        bottomNavBar = NavigationBar(
            view.findViewById(R.id.bottomNavigationView),
            requireActivity(),
            this,
            findNavController()
        )
        bottomNavBar.setSelected(R.id.leaderboard_nav_icon)

        return view
    }

    companion object {
        fun newInstance(): LeaderboardFragment = LeaderboardFragment()
    }

    override fun onSconeClick(position: Int) {
        val currentSconeSelected = viewModel.scones.value!![position]
        Log.d(currentSconeSelected.scone.sconeBusiness, "hello")
        val action =
            LeaderboardFragmentDirections.actionLeaderboardFragmentToUpdateRatingFragment(position)
        findNavController().navigate(action)

    }

    override fun onSconeSwipe(scone: SconeWithRatings) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Scone")
        builder.setMessage("Are you sure you want to delete this scone")
        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            viewModel.deleteSconeWithRating(scone)
            Toast.makeText(
                activity,
                "Scone deleted", Toast.LENGTH_SHORT
            ).show()
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, which ->
            Toast.makeText(
                activity,
                "Cancelled", Toast.LENGTH_SHORT
            ).show()
        }
        builder.show()

    }


    override fun onShareClick(position: Int) {
        val scone = viewModel.scones.value!![position]
        val score = scone.ratings[0].score.format(2)

        Log.d("FORMATTED SCORE", score)
        val message =
            "I just rated this scone ${scone.scone.sconeName} a $score " +
                    "have we ever discussed my passion for scones?"
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)

    }
    override fun onDirectionClick(position: Int) {
        val currentSconeSelected = viewModel.scones.value!![position]
        val currentLat = currentSconeSelected.scone.latitude
        val currentLon = currentSconeSelected.scone.longitude
        val uri = "geo:0,0?q=" + currentLat + "," + currentLon
        val intentUri = Uri.parse(uri)
        val mapIntent = Intent(Intent.ACTION_VIEW, intentUri)
        if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        } else {
            val toast = Toast.makeText(requireActivity().applicationContext,  context?.getString(R.string.no_directions), Toast.LENGTH_SHORT)
            toast.show()
        }


    }

}