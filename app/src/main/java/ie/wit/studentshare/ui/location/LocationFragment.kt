package ie.wit.studentshare.ui.location

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ie.wit.studentshare.R
import ie.wit.studentshare.databinding.FragmentLocationBinding

class LocationFragment : Fragment(), GoogleMap.OnMarkerDragListener,
    GoogleMap.OnMarkerClickListener {
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<LocationFragmentArgs>()
    private lateinit var geocoder: Geocoder
    private lateinit var marker: Marker
    private val callback = OnMapReadyCallback { googleMap ->
        val loc = LatLng(args.letting!!.lat, args.letting!!.lng)
        val options = MarkerOptions()
            .title(args.letting!!.uid)
            .snippet("Not correct? Use search or drag")
            .draggable(true)
            .position(loc)
        marker = googleMap.addMarker(options)!!
        marker.showInfoWindow()
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14f))
        googleMap.setOnMarkerDragListener(this)
        googleMap.setOnMarkerClickListener(this)
        setUpSearch(googleMap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        geocoder = Geocoder(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setUpFab()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onMarkerDragStart(marker: Marker) {
        marker.hideInfoWindow()
    }

    override fun onMarkerDrag(marker: Marker) {
        marker.hideInfoWindow()
    }

    override fun onMarkerDragEnd(marker: Marker) {
        marker.title = "Dragged Location"
        marker.showInfoWindow()
        args.letting!!.lat = marker.position.latitude
        args.letting!!.lng = marker.position.longitude
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        return false
    }

    private fun setUpSearch(map: GoogleMap) {
        binding.searchBox.setEndIconOnClickListener {
            val search = geocoder.getFromLocationName(binding.search.text.toString(), 1)
            if (search != null) {
                if (search.size > 0) {
                    marker.remove()
                    val response = search[0]
                    val loc = LatLng(response.latitude, response.longitude)
                    val options = MarkerOptions()
                        .title("Searched Location")
                        .snippet("Not correct? Use search or drag")
                        .draggable(true)
                        .position(loc)
                    marker = map.addMarker(options)!!
                    marker.showInfoWindow()
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14f))
                    args.letting!!.lat = response.latitude
                    args.letting!!.lng = response.longitude
                }
            }
        }
    }

    private fun setUpFab() {
        binding.fab.setOnClickListener {
            val letting = args.letting
            letting!!.uid = "Saved location"
            val action = LocationFragmentDirections.locationToAdd(letting)
            findNavController().navigate(action)
        }
    }

}