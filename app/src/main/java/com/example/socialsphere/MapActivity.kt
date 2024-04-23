package com.example.socialsphere

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressWarnings("MissingPermission")
class MapActivity : FragmentActivity(), OnMapReadyCallback {
    var map: FrameLayout? = null
    var gMap: GoogleMap? = null
    var currentLocation: Location? = null
    var marker: Marker? = null
    lateinit var fusedClient: FusedLocationProviderClient
    private val friendMarkers = HashMap<String, Marker>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        location

    }

    private val location: Unit
        get() {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE
                )
                return
            }
            val task: Task<Location> = fusedClient.getLastLocation()
            task.addOnSuccessListener { location ->
                Log.d("Location", "Am intrat in ultima locatie accesata")
                if (location != null) {
                    currentLocation = location
                    // Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    val supportMapFragment =
                        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
                    supportMapFragment.getMapAsync(this@MapActivity)
                }
            }
        }
    private fun calculateDistance(location1: Location, location2: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            location1.latitude, location1.longitude,
            location2.latitude, location2.longitude,
            results
        )
        return results[0]
    }


    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        val latLng = LatLng(
            currentLocation!!.latitude, currentLocation!!.longitude
        )
        Log.d("Map", "Am trecut prin harta")
        val markerOptions = MarkerOptions().position(latLng).title("My Current Location")
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))
        googleMap.addMarker(markerOptions)
        Log.d("Map", "Am terminat cu harta")

        // Salvăm latitudinea și longitudinea în baza de date pentru utilizatorul curent
        saveUserLocation(currentLocation!!.latitude, currentLocation!!.longitude)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "SocialSphere_channel"
        val channelName = "SocialSphere Notification Channel"
        val notificationId = 123

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.img_2)
            .setContentTitle("SocialSphere notification")
            .setContentText("Your location has been revealed")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(notificationId, builder.build())
        // Obținem referința către baza de date Firebase pentru relațiile de prietenie
        val userFriendsRef = FirebaseDatabase.getInstance().getReference("user_friends")

        // Obținem ID-ul utilizatorului curent
        val currentUserUid = LoginActivity.connectedUser?.id

        // Verificăm dacă utilizatorul este conectat
        GlobalScope.launch(Dispatchers.Default) {
            while (true) {
                if (currentUserUid != null) {
                    // Obținem prietenii utilizatorului curent din baza de date
                    userFriendsRef.orderByChild("userXId").equalTo(currentUserUid)
                        .addListenerForSingleValueEvent(
                            object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (friendSnapshot in dataSnapshot.children) {
                                        val userFriendData =
                                            friendSnapshot.getValue(UserFriendData::class.java)
                                        // Obținem locația prietenului
                                        if (userFriendData != null) {
                                            val friendUid = userFriendData.userYId
                                            if (friendUid != null) {
                                                FirebaseDatabase.getInstance().getReference("user")
                                                    .child(friendUid)
                                                    .addListenerForSingleValueEvent(object :
                                                        ValueEventListener {
                                                        override fun onDataChange(userDataSnapshot: DataSnapshot) {
                                                            val friendUserData =
                                                                userDataSnapshot.getValue(UserData::class.java)
                                                            if (friendUserData != null && friendUserData.latitude != null && friendUserData.longitude != null) {
                                                                val friendLatLng = LatLng(
                                                                    friendUserData.latitude!!,
                                                                    friendUserData.longitude!!
                                                                )
                                                                // Verificăm dacă există deja un marcaj pentru acest prieten
                                                                // și, dacă da, actualizăm poziția
                                                                if (friendMarkers.containsKey(friendUid)) {
                                                                    val existingMarker =
                                                                        friendMarkers[friendUid]
                                                                    existingMarker?.position = friendLatLng
                                                                } else {
                                                                    // Dacă nu există un marcaj pentru acest prieten, adăugăm unul nou
                                                                    val friendMarkerOptions =
                                                                        MarkerOptions().position(
                                                                            friendLatLng
                                                                        ).title(friendUserData.username)
                                                                    val newMarker =
                                                                        gMap?.addMarker(friendMarkerOptions)
                                                                    // Memorăm marcajul în hashmap
                                                                    if (newMarker != null) {
                                                                        friendMarkers[friendUid] = newMarker
                                                                    }
                                                                }

                                                                // Verificăm distanța și afișăm notificarea, dacă este cazul
                                                                val distanceInMeters =
                                                                    calculateDistance(
                                                                        currentLocation!!,
                                                                        friendLatLng
                                                                    )
                                                                if (distanceInMeters <= 1000) {
                                                                    val notificationManager =
                                                                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                                                    val channelId =
                                                                        "SocialSphere_channel"
                                                                    val channelName =
                                                                        "SocialSphere Notification Channel"
                                                                    val notificationId = 123

                                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                                        val channel =
                                                                            NotificationChannel(
                                                                                channelId,
                                                                                channelName,
                                                                                NotificationManager.IMPORTANCE_DEFAULT
                                                                            )
                                                                        notificationManager.createNotificationChannel(
                                                                            channel
                                                                        )
                                                                    }

                                                                    val builder =
                                                                        NotificationCompat.Builder(
                                                                            this@MapActivity,
                                                                            channelId
                                                                        )
                                                                            .setSmallIcon(R.drawable.img_2)
                                                                            .setContentTitle("SocialSphere notification")
                                                                            .setContentText("Your friend is within 1000m distance from you")
                                                                            .setPriority(
                                                                                NotificationCompat.PRIORITY_DEFAULT
                                                                            )

                                                                    notificationManager.notify(
                                                                        notificationId,
                                                                        builder.build()
                                                                    )
                                                                }
                                                            }
                                                        }

                                                        override fun onCancelled(databaseError: DatabaseError) {
                                                            Log.e(
                                                                "MapActivity",
                                                                "Error reading friend user data",
                                                                databaseError.toException()
                                                            )
                                                        }
                                                    })
                                            }
                                        }
                                    }

                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    Log.e(
                                        "MapActivity",
                                        "Error reading user friends data",
                                        databaseError.toException()
                                    )
                                }
                            }
                        )
                } else {
                    Log.e("MapActivity", "No user is currently connected")
                }
                delay(100)
            }
        }

    }

    private fun saveUserLocation(latitude: Double, longitude: Double) {
        // Obținem referința către baza de date Firebase
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference = firebaseDatabase.reference.child("user")

        // Obținem utilizatorul conectat din variabila statică connectedUser
        val connectedUser = LoginActivity.connectedUser

        // Verificăm dacă utilizatorul este conectat
        if (connectedUser != null) {
            // Actualizăm valorile latitudinii și longitudinii utilizatorului în baza de date
            databaseReference.child(connectedUser.id ?: "").child("latitude").setValue(latitude)
            databaseReference.child(connectedUser.id ?: "").child("longitude").setValue(longitude)
        } else {
            Log.e("saveUserLocation", "No user is currently connected")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                location
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 101
    }


}