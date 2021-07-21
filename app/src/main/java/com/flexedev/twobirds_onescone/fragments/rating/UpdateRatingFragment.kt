package com.flexedev.twobirds_onescone.fragments.rating

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.navigation.fragment.navArgs
import com.flexedev.twobirds_onescone.R
import com.flexedev.twobirds_onescone.TwoBirdsLiveRoomApplication
import com.flexedev.twobirds_onescone.data.entities.Photo
import com.flexedev.twobirds_onescone.data.entities.Rating
import com.flexedev.twobirds_onescone.data.entities.Scone
import com.flexedev.twobirds_onescone.data.entities.SconeWithRatings
import com.flexedev.twobirds_onescone.fragments.map.UpdateLocationFragment
import com.flexedev.twobirds_onescone.navigation.NavigationBar
import com.flexedev.twobirds_onescone.viewModel.RatingViewModel
import com.flexedev.twobirds_onescone.viewModel.SconeViewModel
import com.flexedev.twobirds_onescone.viewModel.SconeViewModelFactory
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*
import kotlin.properties.Delegates


class UpdateRatingFragment : Fragment() {
    private val REQUEST_CAMERA = 110
    private val REQUEST_GALLERY = 111
    private val sconeViewModel: SconeViewModel by activityViewModels {
        SconeViewModelFactory((activity?.application as TwoBirdsLiveRoomApplication).repository)
    }

    private val args by navArgs<UpdateRatingFragmentArgs>()
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
    private lateinit var latLong: LatLng

    private lateinit var imageViewer: ImageView
    private lateinit var update: Button
    private lateinit var addLocation: Button
    private lateinit var cameraButton: ImageButton
    private lateinit var bottomNavBar: NavigationBar

    private var imageChangedBool by Delegates.notNull<Boolean>()

    private lateinit var currentSconeSelected: SconeWithRatings

    private var fileName = ""
    private var currentImageString = ""

    private val photoDirectory
        get() = File(context?.getExternalFilesDir(null), "LeScones")
    private var newImageUri: Uri = photoDirectory.toUri()

    private val viewModel: SconeViewModel by activityViewModels {
        SconeViewModelFactory((activity?.application as TwoBirdsLiveRoomApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update_rating, container, false)

        val tempCurrentScone: SconeWithRatings? = findSconeFromScones(args.sconePos)

        if (tempCurrentScone != null) {
            currentSconeSelected = findSconeFromScones(args.sconePos)!!
        } else {
            currentSconeSelected = viewModel.scones.value!![args.sconePos]
        }

        imageChangedBool = false
        sconeName = view.findViewById(R.id.SconeName)
        sconeName.setText(currentSconeSelected.scone.sconeName)
        storeName = view.findViewById(R.id.SconeStore)
        storeName.setText(currentSconeSelected.scone.sconeBusiness)
        flavour = view.findViewById(R.id.rateFlavourBar)
        flavour.rating = currentSconeSelected.ratings[0].flavour.toFloat()
        texture = view.findViewById(R.id.rateTextureBar)
        texture.rating = currentSconeSelected.ratings[0].texture.toFloat()
        price = view.findViewById(R.id.ratePriceBar)
        price.rating = currentSconeSelected.ratings[0].value.toFloat()

        latLong = LatLng(currentSconeSelected.scone.latitude.toDouble(), currentSconeSelected.scone.longitude.toDouble())
        notes = view.findViewById(R.id.addNotesMultiLine)
        notes.setText(currentSconeSelected.ratings[0].notes)
        cameraButton = view.findViewById(R.id.cameraUButton)

        currentImageString = currentSconeSelected.scone.image
        val currentImageUri = Uri.parse(currentImageString)

        ratingViewModel.setCurrentSconeLocation(latLong)
        imageViewer = view.findViewById(R.id.updateVImage)
        Picasso.get()
            .load(currentImageUri)
            .into(imageViewer)


        update = view.findViewById(R.id.updateRating)
        addLocation = view.findViewById(R.id.location_btn)


        // Bottom navigation bar navigation
        bottomNavBar = NavigationBar(
            view.findViewById(R.id.bottomNavigationView),
            requireActivity(),
            this,
            findNavController()
        )
        bottomNavBar.setSelected(R.id.ratings_nav_icon)

        loadData()
        return view
    }

    private fun findSconeFromScones(sconeId: Int): SconeWithRatings? {
        val sconeIdLong: Long = sconeId.toLong()
        for (scone: SconeWithRatings in viewModel.scones.value!!) {
            if (scone.scone.id == sconeIdLong) {
                return scone
            }
        }
        return null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        update.setOnClickListener {
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
        UpdateLocationFragment().show(requireActivity().supportFragmentManager, null)
    }

    private fun addNewRating() {
        val sconeText = sconeName.text.toString()

        val storeText = storeName.text.toString()
        val flavourText = flavour.rating.toString()
        val textureText = texture.rating.toString()
        Log.d(newImageUri.toString(),"saveURI")
        Log.d(currentImageString, "newURI")

        fileName = if(imageChangedBool) {
            newImageUri.toString()
        } else {
            currentImageString
        }

        val latText = ratingViewModel.currentSconeLocation.value!!.latitude.toString()
        val longText = ratingViewModel.currentSconeLocation.value!!.longitude.toString()
        val noteText = notes.text.toString()

        val score = calculateOverallScore(flavour.rating, texture.rating, price.rating)

        if (inputCheck(storeText, flavourText, textureText)) {
            val scone = Scone(
                currentSconeSelected.scone.id,
                sconeText,
                storeText,
                latText,
                longText,
                fileName
            )
            val rating = Rating(
                currentSconeSelected.ratings[0].ratingId,
                currentSconeSelected.scone.id,
                score,
                flavourText,
                textureText,
                flavourText,
                noteText
            )

            viewModel.updateSconeAndRating(scone, rating)

            //viewModel.addSconeWithRatings(scone, rating)

            Toast.makeText(requireContext(), "Successfully updated scone!", Toast.LENGTH_LONG)
                .show()
            findNavController().navigate(R.id.action_updateRatingFragment_to_leaderboardFragment)

        } else {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_LONG).show()
        }
    }

    private fun calculateOverallScore(flavour: Float, texture: Float, price: Float): String {
        // Weight 1: Flavour 2:Texture 0.5: Price
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
            newImageUri = takenImage.file.toUri()
            imageViewer.setImageURI(newImageUri)
            imageChangedBool = true
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

    companion object {
        fun newInstance(): UpdateRatingFragment = UpdateRatingFragment()
    }
}