package ie.wit.studentshare.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.studentshare.firebase.FirebaseDBManager
import ie.wit.studentshare.models.StudentShareModel
import timber.log.Timber

class DetailViewModel : ViewModel() {
    private val letting = MutableLiveData<StudentShareModel>()

    var observableLetting: LiveData<StudentShareModel>
        get() = letting
        set(value) {letting.value = value.value}

    fun getLetting(id: String) {
        try {
            FirebaseDBManager.findById(id, letting)
            Timber.i("getLetting Success : ${
                letting.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("getLetting Error : $e.message")
        }
    }
}