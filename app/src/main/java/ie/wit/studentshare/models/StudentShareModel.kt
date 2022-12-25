package ie.wit.studentshare.models

import android.net.Uri
import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class StudentShareModel(
    var uid: String? = "",
    var street: String = "",
    var cost: String = "",
    var details: String = "",
    var phone: String = "",
    var lat: Double = 53.1424,
    var lng: Double = -7.84,
    var favourites: MutableList<String?> = emptyList<String?>().toMutableList(),
    var userId: String? = "",
    var images:  MutableList<Uri> = emptyList<Uri>().toMutableList(),
    var cloudImage1: String = "https://via.placeholder.com/900x600/FFFFFF/000000/?text=StudentShare",
    var cloudImage2: String = "https://via.placeholder.com/900x600/FFFFFF/000000/?text=StudentShare",
    var cloudImage3: String = "https://via.placeholder.com/900x600/FFFFFF/000000/?text=StudentShare",
    var cloudImage4: String = "https://via.placeholder.com/900x600/FFFFFF/000000/?text=StudentShare",

) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "street" to street,
            "cost" to cost,
            "details" to details,
            "phone" to phone,
            "lat" to lat,
            "lng" to lng,
            "userId" to userId,
            "cloudImage1" to cloudImage1,
            "cloudImage2" to cloudImage2,
            "cloudImage3" to cloudImage3,
            "cloudImage4" to cloudImage4
        )
    }
}

@Parcelize
data class InstitutionModel(
    var title: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var zoom: Float = 6.5f,
    var radius: Double = 0.0,
) : Parcelable {
    override fun toString(): String {
        return title
    }
}
