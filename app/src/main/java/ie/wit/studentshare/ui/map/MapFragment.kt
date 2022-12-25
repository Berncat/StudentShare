package ie.wit.studentshare.ui.map

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import ie.wit.studentshare.R
import ie.wit.studentshare.databinding.FragmentMapBinding
import ie.wit.studentshare.utils.createLoader
import ie.wit.studentshare.utils.hideLoader
import ie.wit.studentshare.utils.showLoader

class MapFragment : Fragment(), GoogleMap.OnMarkerClickListener,
    GoogleMap.OnInfoWindowClickListener {
    private val mapViewModel: MapViewModel by activityViewModels()
    lateinit var loader: AlertDialog
    private var callback = render()
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<MapFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater,container, false)
        val root: View = binding.root
        loader = createLoader(requireActivity())
        callback = render()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onResume() {
        super.onResume()
        callback = render()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        return false
    }

    override fun onInfoWindowClick(marker: Marker) {

        if (marker.tag != null) {
            val uid = marker.tag.toString()
            val action = MapFragmentDirections.mapToDetail(uid)
            findNavController().navigate(action)
        }
    }

    private fun render(): OnMapReadyCallback {
        return OnMapReadyCallback { googleMap ->
            val iLoc = LatLng(args.search.lat, args.search.lng)

            if (args.search.title != "All") {
                val iMarker = MarkerOptions()
                    .title(args.search.title)
                    .position(iLoc)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                googleMap.addMarker(iMarker)?.showInfoWindow()
            }

            val iCircle = CircleOptions()
                .center(iLoc)
                .radius(args.search.radius)
            googleMap.addCircle(iCircle)

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(iLoc, args.search.zoom))

            showLoader(loader,"Downloading Lettings")
            mapViewModel.observableLettingsList.observe(
                viewLifecycleOwner,
                Observer { results ->
                    results?.let {
                        results.forEach {
                            val loc = LatLng(it.lat, it.lng)
                            val marker = MarkerOptions()
                                .title(it.street)
                                .snippet("€" + it.cost)
                                .position(loc)
                            googleMap.addMarker(marker)?.tag = it.uid
                        }
                    }
                })
            hideLoader(loader)

            googleMap.setOnMarkerClickListener(this)
            googleMap.setOnInfoWindowClickListener(this)
        }
    }
}
