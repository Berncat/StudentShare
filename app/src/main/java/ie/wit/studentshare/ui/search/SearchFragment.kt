package ie.wit.studentshare.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import ie.wit.studentshare.databinding.FragmentSearchBinding
import ie.wit.studentshare.models.InstitutionModel
import timber.log.Timber.Forest.e
import java.io.*

class SearchFragment : Fragment() {
    private lateinit var institution: InstitutionModel
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel: SearchViewModel by activityViewModels()
    // private lateinit var institutions: ArrayList<InstitutionModel>
    // private val listType: Type = object : TypeToken<java.util.ArrayList<InstitutionModel>>() {}.type

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        searchViewModel.observableInstitutionsList.observe(
            viewLifecycleOwner,
            Observer { results ->
                results?.let {
                    render(results as ArrayList<InstitutionModel>)
                }
            })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(institutionsList: ArrayList<InstitutionModel>) {
        setUpSpinner(institutionsList)
        setUpAmountPicker()
        setUpSearchButton()
    }

    private fun setUpSpinner(institutions: ArrayList<InstitutionModel>) {
        val spinner = binding.spinner
        val arrayAdapter =
            activity?.let {
                ArrayAdapter(
                    it,
                    android.R.layout.simple_spinner_dropdown_item,
                    institutions
                )
            }
        spinner.adapter = arrayAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                institution = institutions[position]
                binding.amountPickerText.isVisible = position != 0
                binding.amountPicker.isVisible = position != 0
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                institution = institutions[0]
            }
        }
    }

    private fun setUpAmountPicker() {
        val distances = resources.getStringArray(ie.wit.studentshare.R.array.search_distance)
        val amountPicker = binding.amountPicker
        amountPicker.displayedValues = distances
        amountPicker.minValue = 1
        amountPicker.maxValue = distances.size
    }

    private fun setUpSearchButton() {
        val button = binding.searchButton
        val amountPicker = binding.amountPicker
        val zoomMap = mapOf(
            1 to Pair(14f, 1000.0),
            2 to Pair(13f, 2000.0),
            3 to Pair(12.4f, 3000.0),
            4 to Pair(12f, 4000.0),
            5 to Pair(11.6f, 5000.0),
            6 to Pair(10.6f, 10000.0),
            7 to Pair(10f, 15000.0),
            8 to Pair(9.6f, 20000.0),
            9 to Pair(9.3f, 25000.0),
            10 to Pair(9f, 30000.0),
            11 to Pair(8.6f, 40000.0),
            12 to Pair(8.3f, 50000.0)
        )

        button.setOnClickListener {
            var (zoom, radius) = zoomMap[amountPicker.value]!!
            if (institution.title == "All") {
                zoom = 6.5f
                radius = 0.0
            }
            val search =
                InstitutionModel(institution.title, institution.lat, institution.lng, zoom, radius)
            val action = SearchFragmentDirections.searchToMap(search)
            findNavController().navigate(action)
        }
    }


    // Back up solution get institutions from asset folder rather than firebase
    private fun getInstitutions() {
        var str = ""
        val inputStream = resources.assets.open("institutions.json")
        try {
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val partialStr = StringBuilder()
            var done = false
            while (!done) {
                val line = bufferedReader.readLine()
                done = (line == null)
                if (line != null) partialStr.append(line)
            }
            inputStream.close()
            str = partialStr.toString()
        } catch (e: FileNotFoundException) {
            e("file not found: %s", e.toString())
        } catch (e: IOException) {
            e("cannot read file: %s", e.toString())
        }
        // institutions = Gson().fromJson(str, listType)
    }
}