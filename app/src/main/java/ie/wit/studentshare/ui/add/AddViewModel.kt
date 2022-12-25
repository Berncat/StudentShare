package ie.wit.studentshare.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.wit.studentshare.firebase.FirebaseDBManager
import ie.wit.studentshare.models.StudentShareModel

class AddViewModel : ViewModel() {

    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addLetting(
        firebaseUser: MutableLiveData<FirebaseUser>,
        letting: StudentShareModel
    ) {
        status.value = try {
            FirebaseDBManager.create(firebaseUser, letting)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}