package ie.wit.studentshare.models

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface StudentShareStore {
    fun findAllInstitutions(institutionsList: MutableLiveData<List<InstitutionModel>>)
    fun findAll(lettingsList: MutableLiveData<List<StudentShareModel>>)
    fun findFavourites(userid:String, lettingsList: MutableLiveData<List<StudentShareModel>>)
    fun findById(uid: String, letting: MutableLiveData<StudentShareModel>)
    fun findUsersLettings(userid:String, lettingsList: MutableLiveData<List<StudentShareModel>>)
    fun create(firebaseUser: MutableLiveData<FirebaseUser>, letting: StudentShareModel)
    fun update (LettingId: String, letting: StudentShareModel)
    fun delete (lettingId: String)

}