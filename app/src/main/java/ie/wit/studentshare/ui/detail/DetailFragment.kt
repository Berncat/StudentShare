package ie.wit.studentshare.ui.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import ie.wit.studentshare.databinding.FragmentDetailBinding
import ie.wit.studentshare.models.InstitutionModel
import ie.wit.studentshare.models.StudentShareModel
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

class DetailFragment : Fragment() {

    private lateinit var detailViewModel: DetailViewModel
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<DetailFragmentArgs>()
    private var list = mutableListOf<CarouselItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        detailViewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        detailViewModel.observableLetting.observe(viewLifecycleOwner, Observer
        { results ->
            results?.let {
                render(it)
            }
        })
        return root
    }

    override fun onResume() {
        super.onResume()
        detailViewModel.getLetting(args.lettingId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(letting: StudentShareModel) {
        list.clear()
        list.add(
            CarouselItem(
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/studentshareireland.appspot.com/o/images%2F-NK4SFUQJFIN3y9Pb1JO%2FcloudImage1?alt=media&token=af046d57-07a2-48ac-88ad-61b6965b27ce"
            )
        )
        list.add(
            CarouselItem(
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/studentshareireland.appspot.com/o/images%2F-NK4QF5OPDMmqtGttv5w%2FcloudImage2?alt=media&token=158c4762-87c6-4df4-8be9-e77abc37017e"
            )
        )
        list.add(
            CarouselItem(
                imageUrl = "https://picsum.photos/300/200"
            )
        )
        list.add(
            CarouselItem(
                imageUrl = "https://picsum.photos/300/200"
            )
        )
        binding.carousel.registerLifecycle(lifecycle)
        binding.carousel.setData(list)
        binding.lettingvm = detailViewModel
    }
}