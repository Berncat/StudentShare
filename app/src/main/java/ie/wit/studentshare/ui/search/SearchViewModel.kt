package ie.wit.studentshare.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.wit.studentshare.firebase.FirebaseDBManager
import ie.wit.studentshare.models.InstitutionModel
import timber.log.Timber
import java.lang.Exception

class SearchViewModel : ViewModel() {
    private val institutionsList =
        MutableLiveData<List<InstitutionModel>>()

    val observableInstitutionsList: LiveData<List<InstitutionModel>>
        get() = institutionsList

    init { load() }

    private fun load() {
        try {
            FirebaseDBManager.findAllInstitutions(institutionsList)
            Timber.i("Institutions Load Success")
            Timber.i(institutionsList.value.toString())
        }
        catch (e: Exception) {
            Timber.i("Institutions Load Error : $e.message")
        }
    }
}