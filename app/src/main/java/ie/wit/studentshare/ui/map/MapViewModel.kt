package ie.wit.studentshare.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.studentshare.firebase.FirebaseDBManager
import ie.wit.studentshare.models.InstitutionModel
import ie.wit.studentshare.models.StudentShareModel
import timber.log.Timber
import java.lang.Exception

class MapViewModel : ViewModel() {

    private val lettingsList =
        MutableLiveData<List<StudentShareModel>>()

    val observableLettingsList: LiveData<List<StudentShareModel>>
        get() = lettingsList

    init { load() }

    fun load() {
        try {
            FirebaseDBManager.findAll(lettingsList)
            Timber.i("Lettings Load Success")
            println("HELP"+lettingsList)
        }
        catch (e: Exception) {
            Timber.i("Lettings Load Error : $e.message")
        }
    }
}