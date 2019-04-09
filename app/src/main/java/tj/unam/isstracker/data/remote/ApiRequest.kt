package tj.unam.isstracker.data.remote

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import tj.unam.isstracker.data.model.Astros
import tj.unam.isstracker.data.model.ISSLocationNow

interface ApiRequest {
    @GET(ApiEndPoint.API_ASTROS)
    fun getAstros(): Deferred<Astros>

    @GET(ApiEndPoint.API_ISS_NOW)
    fun getISSNow(): Deferred<ISSLocationNow>

}