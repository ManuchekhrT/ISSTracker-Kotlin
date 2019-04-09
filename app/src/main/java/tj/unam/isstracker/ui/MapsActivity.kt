package tj.unam.isstracker.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import tj.unam.isstracker.R
import kotlinx.coroutines.*
import retrofit2.HttpException
import tj.unam.isstracker.data.model.WhereAbouts
import tj.unam.isstracker.data.remote.ApiClient
import tj.unam.isstracker.utils.CommonUtils
import android.os.Handler
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import tj.unam.isstracker.utils.ImageUtils
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val apiService = ApiClient.getClient()
    private lateinit var timer: Timer
    private var timerTask: TimerTask? = null
    private val handler = Handler()
    private var mMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val isParsingSuccess = mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this,
                R.raw.style_json
            )
        )
        if (!isParsingSuccess)
            Log.d("isParsingSuccess", "Style parsing failed")

        startTimerToGetIssLocation()
    }


    private fun initISSLocation(it: WhereAbouts) {
        Log.d("ISS_LOCATION", it.latitude + it.longitude)
        val longitudeStr: String = it.longitude
        val latitudeStr: String = it.latitude
        val iss = LatLng(CommonUtils.stringConvertDouble(latitudeStr), CommonUtils.stringConvertDouble(longitudeStr))
        val markerOptions = MarkerOptions().position(iss).title("ISS Location Now")
        if (mMarker == null) {
            mMarker = mMap.addMarker(
                markerOptions.icon(
                    ImageUtils.getBitmapDescriptor(
                        R.drawable.space_station, this
                    )
                )
            )
        } else {
            mMap.clear()
            mMap.addMarker(
                markerOptions.icon(
                    ImageUtils.getBitmapDescriptor(
                        R.drawable.space_station, this
                    )
                )
            )

        }
        mMap.animateCamera(CameraUpdateFactory.newLatLng(iss))
        circleAroundLocation(iss)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.refresh_page -> {
                startTimerToGetIssLocation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun circleAroundLocation(latLng: LatLng) {
        val circle = mMap.addCircle(
            CircleOptions()
                .center(latLng)
                .radius(10000.00)
                .strokeColor(Color.RED)
                .fillColor(Color.RED)
        )

        circle.center = latLng
    }

    //To start timer
    private fun startTimerToGetIssLocation() {
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                handler.post(Runnable {
                    CoroutineScope(Dispatchers.IO).launch {
                        val request = apiService.getISSNow()
                        try {
                            val response = request.await()
                            withContext(Dispatchers.Main) {
                                if (response.message == "success") {
                                    initISSLocation(response.whereAbouts)
                                }
                            }
                        } catch (e: HttpException) {
                            Log.d("REQUEST", "Exception ${e.message}")
                        } catch (e: Throwable) {
                            Log.d("REQUEST", "Ooops: Something else went wrong")
                        }

                    }
                })
            }
        }
        timer.schedule(timerTask, 5000, 5000)
    }

    @SuppressLint("InflateParams")
    fun fabClickHandler(v: View) {
        val mBottomSheetDialog = BottomSheetDialog(this@MapsActivity)
        val mBottomSheetView = layoutInflater.inflate(R.layout.astros_bottom_sheet, null)
        val mAstrosRv: RecyclerView = mBottomSheetView.findViewById(R.id.astrosRv)
        val mAstrosCurrentNumberTv: TextView = mBottomSheetView.findViewById(R.id.astrosCurrentNumberTv)

        popuateRecyclerView(mAstrosRv, mAstrosCurrentNumberTv)

        mBottomSheetDialog.setContentView(mBottomSheetView)
        mBottomSheetDialog.show()


    }

    @SuppressLint("SetTextI18n")
    private fun popuateRecyclerView(
        mAstrosRv: RecyclerView,
        mAstrosCurrentNumberTv: TextView
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = apiService.getAstros()
            try {
                val response = request.await()
                withContext(Dispatchers.Main) {
                    if (response.message == "success") {
                        mAstrosRv.hasFixedSize()
                        val linearLayoutManager = LinearLayoutManager(applicationContext)
                        mAstrosRv.layoutManager = linearLayoutManager
                        val mAdapter = AstrosAdapter(response.peopleList, applicationContext)
                        Log.d("MAPS_SUCCESS_REQUEST", response.message)
                        mAstrosRv.adapter = mAdapter

                        mAstrosCurrentNumberTv.text = "There are currently ${response.peopleList.size} humans in space"
                    }
                }
            } catch (e: HttpException) {
                Log.d("REQUEST", "Exception ${e.message}")
            } catch (e: Throwable) {
                Log.d("REQUEST", "Ooops: Something else went wrong")
            }
        }
    }


    //To stop timer
    private fun stopTimer() {
        timer.cancel()
        timer.purge()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

}
