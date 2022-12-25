package ie.wit.studentshare.ui.listings

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ie.wit.studentshare.utils.SwipeToDeleteCallback
import ie.wit.studentshare.utils.SwipeToEditCallback
import ie.wit.studentshare.adapters.Adapter
import ie.wit.studentshare.adapters.LettingsClickListener
import ie.wit.studentshare.databinding.FragmentListingsBinding
import ie.wit.studentshare.models.StudentShareModel
import ie.wit.studentshare.ui.auth.LoggedInViewModel
import ie.wit.studentshare.utils.createLoader
import ie.wit.studentshare.utils.hideLoader
import ie.wit.studentshare.utils.showLoader

class ListingsFragment : Fragment(), LettingsClickListener {
    private var _binding: FragmentListingsBinding? = null
    private val binding get() = _binding!!
    private val listingsViewModel: ListingsViewModel by activityViewModels()
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()
    lateinit var loader: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        loader = createLoader(requireActivity())

        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
/*        binding.fab.setOnClickListener {
            val action = ListingsFragmentDirections.actionNavListingsToNavAdd(letting = null)
            findNavController().navigate(action)
        }*/
        showLoader(loader, "Downloading Lettings")
        listingsViewModel.observableLettingsList.observe(viewLifecycleOwner, Observer { lettings ->
            lettings?.let {
                render(lettings as ArrayList<StudentShareModel>)
                hideLoader(loader)
                checkSwipeRefresh()
            }
        })

        setSwipeRefresh()

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showLoader(loader, "Deleting Letting")
                val adapter = binding.recyclerView.adapter as Adapter
                adapter.removeAt(viewHolder.adapterPosition)
                listingsViewModel.delete((viewHolder.itemView.tag as StudentShareModel).uid!!)
                hideLoader(loader)
            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(binding.recyclerView)

        val swipeEditHandler = object : SwipeToEditCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                onLettingsClick(viewHolder.itemView.tag as StudentShareModel)
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(binding.recyclerView)

        return root
    }

    private fun render(lettingsList: ArrayList<StudentShareModel>) {
        binding.recyclerView.adapter =
            Adapter(lettingsList, this, listingsViewModel.favourites.value!!)
        if (lettingsList.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.noListings.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.noListings.visibility = View.GONE
        }
    }

    override fun onLettingsClick(letting: StudentShareModel) {
/*        val action = letting.uid?.let {
            ListingsFragmentDirections.actionNavListingsToDetailFragment(
                it
            )
        }
        if (!listingsViewModel.favourites.value!!)
            action?.let { findNavController().navigate(it) }*/
    }

    private fun setSwipeRefresh() {
        binding.swiperefresh.setOnRefreshListener {
            binding.swiperefresh.isRefreshing = true
            showLoader(loader, "Downloading lettings")
            if (listingsViewModel.favourites.value!!)
                loggedInViewModel.liveFirebaseUser.value?.let { listingsViewModel.findFavourties(it.uid) }
            else
                listingsViewModel.load()
        }
    }

    private fun checkSwipeRefresh() {
        if (binding.swiperefresh.isRefreshing)
            binding.swiperefresh.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        showLoader(loader, "Downloading lettings")
        loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner, Observer { firebaseUser ->
            if (firebaseUser != null) {
                listingsViewModel.liveFirebaseUser.value = firebaseUser
                listingsViewModel.load()
            }
        })
        //hideLoader(loader)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}