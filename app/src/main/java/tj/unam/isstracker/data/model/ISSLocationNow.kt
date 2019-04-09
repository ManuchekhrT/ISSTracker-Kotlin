package tj.unam.isstracker.data.model

import com.squareup.moshi.Json


data class ISSLocationNow(
    @field:Json(name = "timestamp")
    var timestamp: String,
    @field:Json(name = "message")
    var message: String,
    @field:Json(name = "iss_position")
    var whereAbouts: WhereAbouts
)