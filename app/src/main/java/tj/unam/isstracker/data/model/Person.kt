package tj.unam.isstracker.data.model

import com.squareup.moshi.Json

data class Person(
    @field:Json(name = "craft")
    var craft: String,
    @field:Json(name = "name")
    var name: String
)