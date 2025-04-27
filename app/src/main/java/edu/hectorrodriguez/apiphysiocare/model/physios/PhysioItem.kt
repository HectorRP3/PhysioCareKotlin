package edu.hectorrodriguez.apiphysiocare.model.physios


import com.google.gson.annotations.SerializedName

data class PhysioItem(
    @SerializedName("email")
    val email: String?,
    @SerializedName("_id")
    val id: String?,
    @SerializedName("licenseNumber")
    val licenseNumber: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("specialty")
    val specialty: String?,
    @SerializedName("surname")
    val surname: String?
)

data class PhysioResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("resultado") val physios: Physio,
)

data class PhysioIdResponse(
    @SerializedName("ok") val ok: Boolean,
    @SerializedName("resultado") val physio: PhysioItem,
)

