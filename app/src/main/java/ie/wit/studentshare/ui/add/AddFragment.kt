package ie.wit.studentshare.ui.add

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import ie.wit.studentshare.R
import ie.wit.studentshare.databinding.FragmentAddBinding
import ie.wit.studentshare.utils.checkLocationPermissions
import ie.wit.studentshare.models.StudentShareModel
import ie.wit.studentshare.ui.auth.LoggedInViewModel
import timber.log.Timber
import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import ie.wit.studentshare.firebase.FirebaseImageManager
import ie.wit.studentshare.ui.location.LocationFragmentArgs
import ie.wit.studentshare.utils.showImagePicker
import java.util.*

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var addViewModel: AddViewModel
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()
    private lateinit var locationService: FusedLocationProviderClient
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var imageIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var intentLauncher: ActivityResultLauncher<Intent>
    private val args by navArgs<LocationFragmentArgs>()
    private var letting = StudentShareModel()
    private var permission = false
    private var location = false
    private var edit = false
    private var image = Pair("ADD", 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationService = LocationServices.getFusedLocationProviderClient(requireContext())
        if (checkLocationPermissions(requireActivity())) {
            createPermissionLauncher()
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            permission = true
        }
        registerImagePickerCallback()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        val root: View = binding.root

        addViewModel = ViewModelProvider(this).get(AddViewModel::class.java)
        addViewModel.observableStatus.observe(viewLifecycleOwner, Observer { status ->
            status?.let { render(status) }
        })

        if (args.letting != null) {
            edit = true
            location = true
            letting = args.letting!!
        }
        checkIfEdit()
        setUpAddButton(binding)
        setUpLocationButton(binding)
        setUpImages(binding)
        setUpImageButton(binding)
        setUpUploadButton(binding)
        return root
    }

    override fun onResume() {
        super.onResume()
        setUpImages(binding)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(status: Boolean) {
        when (status) {
            true -> {
                view?.let {
                    Timber.i("Rendered")
                }
            }
            false -> Toast.makeText(context, "Could not add your letting", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun setUpAddButton(layout: FragmentAddBinding) {
        layout.addButton.setOnClickListener {
            if (validateFields(layout)) {
                letting.street = layout.street.text.toString()
                letting.cost = layout.cost.text.toString()
                letting.details = layout.details.text.toString()
                letting.phone = layout.phone.text.toString()
            }
            addViewModel.addLetting(loggedInViewModel.liveFirebaseUser, letting)
        }
    }

    private fun setUpLocationButton(layout: FragmentAddBinding) {
        layout.locationButton.setOnClickListener {
            if (permission && (!edit or !location)) {
                letting.uid = "Current location"
                setCurrentLocation()
            }
            if (validateFields(layout)) {
                letting.street = layout.street.text.toString()
                letting.cost = layout.cost.text.toString()
                letting.details = layout.details.text.toString()
                letting.phone = layout.phone.text.toString()
                val action = AddFragmentDirections.addToLocation()
                findNavController().navigate(action)
            }

        }
    }

    private fun setUpImageButton(layout: FragmentAddBinding) {
        layout.addImage.setOnClickListener {
            if (letting.images.size < 4) {
                image = Pair("ADD", 0)
                showImagePicker(imageIntentLauncher)
            } else {
                Toast.makeText(context, "Only 4 images allowed, delete or edit", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun setUpImages(layout: FragmentAddBinding) {
        val imageViews = listOf(layout.Image1, layout.Image2, layout.Image3, layout.Image4)
        if (letting.images.size > 0) {
            letting.images.forEachIndexed { index, uri ->
                val imageView = imageViews[index]
                imageView.isVisible = true
                imageView.setImageURI(letting.images.getOrNull(index))
                imageView.setImageURI(uri)
                imageView.setOnClickListener {
                    image = Pair("REPLACE", index)
                    showImagePicker(imageIntentLauncher)
                }
                imageView.setOnLongClickListener {
                    imageView.isVisible = false
                    letting.images.removeAt(index)
                    true
                }
            }
        }
    }

    private fun setUpUploadButton(layout: FragmentAddBinding) {
        val list = mutableListOf<Pair<Int, String>>()
        layout.uploadButton.setOnClickListener {
            letting.images.forEachIndexed { index, uri ->
                list.add(Pair(index, FirebaseImageManager.uploadImageToFirebase(uri)))
            }
        }
    }

    private fun validateFields(layout: FragmentAddBinding): Boolean {
        resetErrors(layout)
        var count = 0
        if (layout.street.length() == 0) {
            layout.streetBox.error = "Required field"
            count += 1
        }
        if (layout.cost.length() == 0) {
            layout.costBox.error = "Required field"
            count += 1
        }
        if (layout.details.length() == 0) {
            layout.detailsBox.error = "Required field"
            count += 1
        }
        if (layout.phone.length() == 0) {
            layout.phoneBox.error = "Required field"
            count += 1
        }
        if (count > 0) {
            return false
        }
        return true
    }

    private fun resetErrors(layout: FragmentAddBinding) {
        layout.streetBox.error = null
        layout.costBox.error = null
        layout.detailsBox.error = null
        layout.phoneBox.error = null
    }

    @SuppressLint("MissingPermission")
    private fun setCurrentLocation() {
        Timber.i("Set current location")
        locationService.lastLocation.addOnSuccessListener {
            letting.lat = it.latitude
            letting.lng = it.longitude
        }
    }

    private fun createPermissionLauncher() {
        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                permission = isGranted
            }
    }

    private fun checkIfEdit() {
        if (edit) {
            binding.street.setText(letting.street)
            binding.cost.setText(letting.cost)
            binding.details.setText(letting.details)
            binding.phone.setText(letting.phone)
            binding.locationButton.text = "Update Location"
        }
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        if (result.data != null) {
                            val (function, index) = image
                            if (function == "ADD") {
                                letting.images.add((result.data!!.data!!))
                            } else {
                                letting.images[index] = (result.data!!.data!!)
                            }
                        }
                    }
                    AppCompatActivity.RESULT_CANCELED -> {}
                    else -> {}
                }
            }
    }
}

