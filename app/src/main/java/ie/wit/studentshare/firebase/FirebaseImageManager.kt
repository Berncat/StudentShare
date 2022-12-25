package ie.wit.studentshare.firebase

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask


object FirebaseImageManager {

    var storage = FirebaseStorage.getInstance().reference
    var imageUri = ""

    fun uploadImageToFirebase(uri: Uri) : String {
        // Get the data from an ImageView as bytes
        val id = System.currentTimeMillis().toString()
        println(id)
        val imageRef = storage.child("project").child("test.jpg")
        lateinit var uploadTask: UploadTask

        imageRef.metadata.addOnSuccessListener {
            uploadTask = imageRef.putFile(uri)
            uploadTask.addOnSuccessListener { ut ->
                ut.metadata!!.reference!!.downloadUrl.addOnCompleteListener { task ->
                    imageUri = task.result.toString()
                }
            }
        }
        println("HERE"+imageUri)
        return imageUri
    }
}

