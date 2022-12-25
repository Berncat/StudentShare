package ie.wit.studentshare.ui.listings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.wit.studentshare.firebase.FirebaseDBManager
import ie.wit.studentshare.models.StudentShareModel
import timber.log.Timber
import java.lang.Exception

class ListingsViewModel : ViewModel() {

    private val lettingsList =
        MutableLiveData<List<StudentShareModel>>()
    var favourites = MutableLiveData(false)

    val observableLettingsList: LiveData<List<StudentShareModel>>
        get() = lettingsList

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()

    init { load() }

    fun load() {
        try {
            FirebaseDBManager.findAll(lettingsList)
            Timber.i("Report Load Success")
        }
        catch (e: Exception) {
            Timber.i("Report Load Error : $e.message")
        }
    }

    fun findFavourties(id: String, ) {
        try {
            FirebaseDBManager.findFavourites(id, lettingsList)
            Timber.i("Report Load Success")
        }
        catch (e: Exception) {
            Timber.i("Report Load Error : $e.message")
        }
    }

    fun delete(id: String) {
        try {
            FirebaseDBManager.delete(id)
            Timber.i("Report Delete Success")
        }
        catch (e: Exception) {
            Timber.i("Report Delete Error : $e.message")
        }
    }
}