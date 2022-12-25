package ie.wit.studentshare.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import ie.wit.studentshare.models.*
import timber.log.Timber

object FirebaseDBManager : StudentShareStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun findAllInstitutions (institutionsList: MutableLiveData<List<InstitutionModel>>) {
        database.child("institutions")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Institutions error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<InstitutionModel>()
                    val children = snapshot.children
                    children.forEach {
                        val institution = it.getValue(InstitutionModel::class.java)
                        localList.add(institution!!)
                    }
                    database.child("institutions")
                        .removeEventListener(this)

                    institutionsList.value = localList
                }
            })
    }

    override fun findAll (lettingsList: MutableLiveData<List<StudentShareModel>>) {
        database.child("lettings")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Lettings error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<StudentShareModel>()
                    val children = snapshot.children
                    children.forEach {
                        val letting = it.getValue(StudentShareModel::class.java)
                        localList.add(letting!!)
                    }
                    database.child("lettings")
                        .removeEventListener(this)
                    lettingsList.value = localList
                }
            })
    }

    override fun findUsersLettings (userid: String, lettingsList: MutableLiveData<List<StudentShareModel>>) {
        database.child("lettings")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Lettings error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<StudentShareModel>()
                    val children = snapshot.children
                    children.forEach {
                        val letting = it.getValue(StudentShareModel::class.java)
                        localList.add(letting!!)
                    }
                    database.child("lettings")
                        .removeEventListener(this)

                    lettingsList.value = localList.filterNot { it.userId == userid }
                }
            })
    }

    override fun findFavourites (userid: String, lettingsList: MutableLiveData<List<StudentShareModel>>) {
        database.child("lettings")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Lettings error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<StudentShareModel>()
                    val children = snapshot.children
                    children.forEach {
                        val letting = it.getValue(StudentShareModel::class.java)
                        localList.add(letting!!)
                    }
                    database.child("lettings")
                        .removeEventListener(this)

                    lettingsList.value = localList.filterNot { it.favourites.contains(userid) }
                }
            })
    }

    override fun findById(uid: String, letting: MutableLiveData<StudentShareModel>) {
        database.child("lettings")
            .child(uid).get().addOnSuccessListener {
                letting.value = it.getValue(StudentShareModel::class.java)
                Timber.i("firebase Got value ${it.value}")
            }.addOnFailureListener{
                Timber.e("firebase Error getting data $it")
            }
    }

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, letting: StudentShareModel) {
        Timber.i("Firebase DB Reference : $database")

        val uid = firebaseUser.value!!.uid
        val key = database.child("lettings").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        letting.uid = key
        letting.userId = uid
        val lettingValues = letting.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/lettings/$key"] = lettingValues

        database.updateChildren(childAdd)
    }

    override fun delete(lettingId: String) {

        val childDelete : MutableMap<String, Any?> = HashMap()
        childDelete["/lettings/$lettingId"] = null

        database.updateChildren(childDelete)
    }

    override fun update(lettingId: String, letting: StudentShareModel) {

        val lettingValues = letting.toMap()

        val childUpdate : MutableMap<String, Any?> = HashMap()
        childUpdate["lettings/$lettingId"] = lettingValues

        database.updateChildren(childUpdate)
    }
}