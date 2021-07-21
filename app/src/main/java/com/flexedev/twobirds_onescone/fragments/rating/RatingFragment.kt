package com.flexedev.twobirds_onescone.fragments.rating

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.flexedev.twobirds_onescone.R
import com.flexedev.twobirds_onescone.viewModel.RatingViewModel
import com.flexedev.twobirds_onescone.TwoBirdsLiveRoomApplication
import com.flexedev.twobirds_onescone.fragments.map.AddLocationFragment
import com.flexedev.twobirds_onescone.navigation.NavigationBar
import com.flexedev.twobirds_onescone.data.entities.Photo
import com.flexedev.twobirds_onescone.data.entities.Rating
import com.flexedev.twobirds_onescone.data.entities.Scone
import com.flexedev.twobirds_onescone.viewModel.SconeViewModel
import com.flexedev.twobirds_onescone.viewModel.SconeViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import java.io.File
import java.lang.Exception
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [RatingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class RatingFragment : Fragment() {

    private val REQUEST_CAMERA = 110
    private val REQUEST_GALLERY = 111
    val hasLocationPermissions get() = requireContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var sharedPreferences: SharedPreferences
    private val ratingViewModel: RatingViewModel by activityViewModels()

    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var time: Int = 0

    private lateinit var sconeName: AutoCompleteTextView
    private lateinit var storeName: AutoCompleteTextView
    private lateinit var flavour: RatingBar
    private lateinit var texture: RatingBar
    private lateinit var price: RatingBar
    private lateinit var notes: EditText

    private lateinit var imageViewer: ImageView
    private lateinit var submit: Button
    private lateinit var addLocation: Button
    private lateinit var cameraButton: ImageButton
    private lateinit var bottomNavBar: NavigationBar
    private lateinit var newLocation: LatLng

    private val photoDirectory
        get() = File(context?.getExternalFilesDir(null), "LeScones")
    private var saveUri: Uri = photoDirectory.toUri()

    private val viewModel: SconeViewModel by activityViewModels {
        SconeViewModelFactory((activity?.application as TwoBirdsLiveRoomApplication).repository)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_rating, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        sconeName = view.findViewById(R.id.SconeName)
        storeName = view.findViewById(R.id.SconeStore)
        flavour = view.findViewById(R.id.rateFlavourBar)
        texture = view.findViewById(R.id.rateTextureBar)
        price = view.findViewById(R.id.ratePriceBar)
        notes = view.findViewById(R.id.addNotesMultiLine)

        imageViewer = view.findViewById(R.id.viewImage)
        cameraButton = view.findViewById(R.id.cameraButton)

        submit = view.findViewById(R.id.submitReview)
        addLocation = view.findViewById(R.id.location_btn)

        setBottomNavigation(view)
        loadData()


        newLocation = LatLng(Double.NaN, Double.NaN)
        @SuppressLint("MissingPermission")
        if (hasLocationPermissions) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    newLocation = updateLocationHelper(location)
                }
            }
        } else {
            val currentLat = sharedPreferences.getString("defaultLat", null)
            val currentLng = sharedPreferences.getString("defaultLng", null)

            newLocation = if (currentLat != null && currentLng != null) {
                LatLng(currentLat.toDouble(), currentLng.toDouble())
            } else {
                ratingViewModel.defaultPinLocation.value!!
            }

        }
        return view
    }

    private fun updateLocationHelper(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        submit.setOnClickListener {
            addNewRating()
        }

        addLocation.setOnClickListener {
            openMapDialog()
        }
        cameraButton.setOnClickListener {
            promptForAdd()
        }
    }

    private fun openMapDialog() {
        AddLocationFragment().show(requireActivity().supportFragmentManager, null)
    }

    private fun addNewRating() {
        val sconeText = sconeName.text.toString()
        val storeText = storeName.text.toString()
        val flavourText = flavour.rating.toString()
        val textureText = texture.rating.toString()
        val valueText = price.rating.toString()
        val fileName = saveUri.toString()

        var latText:String?
        var longText:String?

        try {
            latText = ratingViewModel.currentPinLocation.value!!.latitude.toString()
            longText = ratingViewModel.currentPinLocation.value!!.longitude.toString()
        } catch (exception: Exception) {
            latText = newLocation.latitude.toString()
            longText = newLocation.longitude.toString()
        }

        val noteText = notes.text.toString()


        val score = calculateOverallScore(flavour.rating, texture.rating, price.rating)

        if (inputCheck(sconeText, flavourText, textureText)) {
            val scone = Scone(0, sconeText, storeText, latText!!, longText!!, fileName)
            val rating = Rating(0, 0, score, valueText, textureText, flavourText, noteText)
            viewModel.addSconeWithRatings(scone, rating)
            saveData()
            ratingViewModel.setCurrentPinLocation(null)
            Toast.makeText(requireContext(), "Successfully created scone!", Toast.LENGTH_LONG)
                .show()
            findNavController().navigate(R.id.action_ratingFragment_to_homeFragment)

        } else {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_LONG).show()
        }
    }

    private fun calculateOverallScore(flavour: Float, texture: Float, price: Float): String {
        return ((flavour + texture + price) / 3).toString()
    }

    private fun inputCheck(store: String, flavour: String, texture: String): Boolean {
        return !(TextUtils.isEmpty(store) && (TextUtils.isEmpty(flavour)) || TextUtils.isEmpty(
            texture
        ))
    }

    private fun promptForAdd() {
        val builder = AlertDialog.Builder(activity).apply {
            setTitle("Choose source")
            setMessage("Where is the photo?")
            setPositiveButton("Camera") { _, _ ->
                takePictureFromCamera()
            }
            setNegativeButton("Gallery") { _, _ ->
                takePictureFromGallery()
            }
        }
        builder.show()
    }

    private fun takePictureFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireContext().packageManager)?.let {
            val uri = dayUri()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(intent, REQUEST_CAMERA)
        }
    }

    private fun takePictureFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    private fun loadDayPhotos() {
        if (photoDirectory.exists()) {

            val photos = photoDirectory
                .listFiles { file, _ -> file.isDirectory }
                .map {
                    Photo(File(it, String.format("%02d_%02d_%02d.jpg", month, day, time)))
                }
                .filter { it.file.exists() }
            val takenImage = photos[0]
            saveUri = takenImage.file.toUri()

            Picasso.get()
                .load(saveUri)
                .into(imageViewer)
        }
    }

    private fun dayUri(): Uri {
        year = Calendar.getInstance().get(Calendar.YEAR)
        month = Calendar.getInstance().get(Calendar.MONTH)
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        time = Calendar.getInstance().get(Calendar.MILLISECOND)

        val file = dayFile()
        return FileProvider.getUriForFile(
            requireContext(),
            "com.flexedev.twobirds_onescone.fileprovider",
            file
        )
    }

    private fun dayFile(): File {
        val file = File(photoDirectory, String.format("$year/%02d_%02d_%02d.jpg", month, day, time))
        file.parentFile?.mkdirs()
        return file
    }

    private fun copyUriToUri(from: Uri, to: Uri) {
        requireContext().contentResolver.openInputStream(from).use { input ->
            requireContext().contentResolver.openOutputStream(to).use { output ->
                try {
                    input?.copyTo(output!!)
                } catch (e: NullPointerException) {
                    Log.d("UPDATE FRAGMENT", e.toString())
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    loadDayPhotos()
                }
            }
            REQUEST_GALLERY -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        copyUriToUri(uri, dayUri())
                        loadDayPhotos()
                    }
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun loadData(): String? {
        sharedPreferences =
            requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("STRING_KEY", null)
    }

    private fun saveData() {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.apply {
            putString("lastSconeReviewedTime", System.currentTimeMillis().toString())
        }.apply()
    }

    private fun setBottomNavigation(view: View) {
        //Bottom Navigation Bar
        bottomNavBar = NavigationBar(
            view.findViewById(R.id.bottomNavigationView),
            requireActivity(),
            this,
            findNavController()
        )
        bottomNavBar.setSelected(R.id.ratings_nav_icon)
    }


    companion object {
        fun newInstance(): RatingFragment = RatingFragment()
    }
}