package tj.unam.isstracker.data.model

import com.squareup.moshi.Json

data class WhereAbouts(
    @field:Json(name = "longitude")
    var longitude: String,
    @field:Json(name = "latitude")
    var latitude: String
)