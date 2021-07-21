package com.flexedev.twobirds_onescone.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.flexedev.twobirds_onescone.R
import com.flexedev.twobirds_onescone.fragments.HomeFragment
import com.flexedev.twobirds_onescone.fragments.LeaderboardFragment
import com.flexedev.twobirds_onescone.fragments.ShareReviewsFragment
import com.flexedev.twobirds_onescone.fragments.map.MapFragment
import com.flexedev.twobirds_onescone.fragments.rating.RatingFragment
import com.flexedev.twobirds_onescone.fragments.rating.UpdateRatingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavigationBar(
    bottomNav: BottomNavigationView,
    activity: FragmentActivity,
    parentFrag: Fragment,
    nav: NavController
) {

    private val navController = nav
    private val bottomNavBar = bottomNav

    init {
        bottomNavBar.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.home_nav_icon -> {
                    when (parentFrag) {
                        is RatingFragment -> {
                            navController.navigate(R.id.action_ratingFragment_to_homeFragment)
                        }

                        is MapFragment -> {
                            navController.navigate(R.id.action_mapFragment_to_homeFragment)
                        }

                        is LeaderboardFragment -> {
                            navController.navigate(R.id.action_leaderboardFragment_to_homeFragment)
                        }

                        is ShareReviewsFragment -> {
                            navController.navigate(R.id.action_shareReviews_to_homeFragment)
                        }


                        is UpdateRatingFragment -> {
                            navController.navigate(R.id.action_updateRatingFragment_to_homeFragment)
                        }
                    }
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.map_nav_icon -> {
                    when (parentFrag) {
                        is HomeFragment -> {
                            navController.navigate(R.id.action_homeFragment_to_mapFragment)
                        }

                        is RatingFragment -> {
                            navController.navigate(R.id.action_ratingFragment_to_mapFragment)
                        }

                        is LeaderboardFragment -> {
                            navController.navigate(R.id.action_leaderboardFragment_to_mapFragment)
                        }

                        is UpdateRatingFragment -> {
                            navController.navigate(R.id.action_updateRatingFragment_to_mapFragment)
                        }

                        is ShareReviewsFragment -> {
                            navController.navigate(R.id.action_shareReviews_to_mapFragment)
                        }
                    }
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.ratings_nav_icon -> {
                    when (parentFrag) {
                        is HomeFragment -> {
                            navController.navigate(R.id.action_homeFragment_to_ratingFragment)
                        }

                        is MapFragment -> {
                            navController.navigate(R.id.action_mapFragment_to_ratingFragment)
                        }

                        is LeaderboardFragment -> {
                            navController.navigate(R.id.action_leaderboardFragment_to_ratingFragment)
                        }

                        is ShareReviewsFragment -> {
                            navController.navigate(R.id.action_shareFragment_to_ratingFragment)
                        }

                    }
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.leaderboard_nav_icon -> {
                    when (parentFrag) {
                        is HomeFragment -> {
                            navController.navigate(R.id.action_homeFragment_to_leaderboardFragment)
                        }

                        is MapFragment -> {
                            navController.navigate(R.id.action_mapFragment_to_leaderboardFragment)
                        }

                        is RatingFragment -> {
                            navController.navigate(R.id.action_ratingFragment_to_leaderboardFragment)
                        }

                        is UpdateRatingFragment -> {
                            navController.navigate(R.id.action_updateRatingFragment_to_leaderboardFragment)
                        }
                        is ShareReviewsFragment -> {
                            navController.navigate(R.id.action_shareReviews_to_leaderboardFragment)
                        }
                    }
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.share_nav_icon -> {
                    when (parentFrag) {
                        is HomeFragment -> {
                            navController.navigate(R.id.action_homeFragment_to_shareFragment)
                        }

                        is MapFragment -> {
                            navController.navigate(R.id.action_mapFragment_to_shareFragment)
                        }

                        is RatingFragment -> {
                            navController.navigate(R.id.action_ratingFragment_to_shareFragment)
                        }
                        is UpdateRatingFragment -> {
                            navController.navigate(R.id.action_updateRatingFragment_to_shareFragment)
                        }
                        is LeaderboardFragment -> {
                            navController.navigate(R.id.action_leaderboardFragment_to_shareFragment)
                        }
                    }
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    fun setSelected(icon: Int) {
        bottomNavBar.selectedItemId = icon
    }

}