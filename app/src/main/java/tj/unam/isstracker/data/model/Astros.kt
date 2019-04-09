package tj.unam.isstracker.data.model

import com.squareup.moshi.Json

data class Astros(
    @field:Json(name = "message")
    var message: String,
    @field:Json(name = "number")
    var number: Int,
    @field:Json(name = "people")
    var peopleList: MutableList<Person>
)