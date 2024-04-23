package com.example.socialsphere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.socialsphere.MapActivity
import com.example.socialsphere.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainButton = findViewById<Button>(R.id.showMap)
        mainButton.setOnClickListener {
            // Crearea unui Intent pentru a porni MapActivity
            val intent = Intent(this, MapActivity::class.java)
            //   val intent = Intent(this, MapActivity::class)

            // Pornirea MapActivity folosind Intent-ul definit
            startActivity(intent)
        }

        val addFriendButton = findViewById<Button>(R.id.addFriend)
        addFriendButton.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_friend, null)
            val editTextPhoneNumber = dialogView.findViewById<EditText>(R.id.editTextPhoneNumber)

            val dialog = AlertDialog.Builder(this)
                .setTitle("Add Friend's Phone Number")
                .setView(dialogView)
                .setPositiveButton("Add") { dialog, _ ->
                    val phoneNumber = editTextPhoneNumber.text.toString()

                    // Verificarea dacă există utilizatorul cu numărul de telefon dat în baza de date Firebase
                    val database = FirebaseDatabase.getInstance()
                    val usersRef = database.getReference("user")

                    usersRef.orderByChild("phoneNumber").equalTo(phoneNumber).addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // Utilizatorul cu numărul de telefon dat există în baza de date
                                    val user = dataSnapshot.children.first().getValue(UserData::class.java)
                                    val id = usersRef.push().key
                                    // Adăugarea utilizatorului în lista de prieteni (presupunând că userXId este ID-ul utilizatorului curent)
                                    val currentUserUid = LoginActivity.connectedUser?.id

                                    val userFriendData = UserFriendData(id, userXId = currentUserUid, userYId = user?.id)
                                    val userFriendsRef = database.getReference("user_friends")

                                    userFriendsRef.orderByChild("userXId").equalTo(currentUserUid).addListenerForSingleValueEvent(
                                        object : ValueEventListener {
                                            override fun onDataChange(dataSnapshotX: DataSnapshot) {
                                                var isFriend = false
                                                for (friendSnapshot in dataSnapshotX.children) {
                                                    val userFriendData = friendSnapshot.getValue(UserFriendData::class.java)
                                                    if (userFriendData?.userYId == user?.id) {
                                                        isFriend = true
                                                        break
                                                    }
                                                }

                                                if (isFriend) {
                                                    Toast.makeText(applicationContext, "You are already friends with this user!", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    // Verifică relațiile unde utilizatorul curent este userY
                                                    userFriendsRef.orderByChild("userYId").equalTo(currentUserUid).addListenerForSingleValueEvent(
                                                        object : ValueEventListener {
                                                            override fun onDataChange(dataSnapshotY: DataSnapshot) {
                                                                var isFriend = false
                                                                for (friendSnapshot in dataSnapshotY.children) {
                                                                    val userFriendData = friendSnapshot.getValue(UserFriendData::class.java)
                                                                    if (userFriendData?.userXId == user?.id) {
                                                                        isFriend = true
                                                                        break
                                                                    }
                                                                }

                                                                if (isFriend) {
                                                                    Toast.makeText(applicationContext, "You are already friends with this user!", Toast.LENGTH_SHORT).show()
                                                                } else {
                                                                    // Adăugă relația de prietenie
                                                                    val id = usersRef.push().key
                                                                    val userFriendData = UserFriendData(id, userXId = user?.id, userYId = currentUserUid)
                                                                    userFriendsRef.push().setValue(userFriendData)
                                                                    Toast.makeText(applicationContext, "Friend added successfully!", Toast.LENGTH_SHORT).show()
                                                                }
                                                            }

                                                            override fun onCancelled(databaseError: DatabaseError) {
                                                                Log.e("Cancel", "Error reading user data", databaseError.toException())
                                                            }
                                                        }
                                                    )

                                                }
                                            }

                                            override fun onCancelled(databaseError: DatabaseError) {
                                                Log.e("Cancel", "Error reading user data", databaseError.toException())
                                            }
                                        }
                                    )


//                                    userFriendsRef.push().setValue(userFriendData)
//
//                                    Toast.makeText(applicationContext, "Friend added successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Utilizatorul nu există în baza de date
                                    Toast.makeText(applicationContext, "User with this phone number does not exist!", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Manejarea erorii în caz de eșec al citirii din baza de date
                                Log.e("Cancel", "Error reading user data", databaseError.toException())
                            }
                        }
                    )
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            dialog.show()
        }

        val removeFriendButton = findViewById<Button>(R.id.removeFriend)
        removeFriendButton.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_remove_friend, null)
            val editTextPhoneNumber = dialogView.findViewById<EditText>(R.id.editTextPhoneNumber)

            val dialog = AlertDialog.Builder(this)
                .setTitle("Remove Friend")
                .setMessage("Enter the phone number of the friend you want to remove:")
                .setView(dialogView)
                .setPositiveButton("Remove") { dialog, _ ->
                    val phoneNumber = editTextPhoneNumber.text.toString()

                    // Obține ID-ul prietenului pe baza numărului de telefon
                    val database = FirebaseDatabase.getInstance()
                    val usersRef = database.getReference("user")

                    usersRef.orderByChild("phoneNumber").equalTo(phoneNumber).addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    val user = dataSnapshot.children.first().getValue(UserData::class.java)
                                    val friendId = user?.id

                                    // Șterge prietenul din lista de prieteni (userXId este ID-ul utilizatorului curent)
                                    val currentUserUid = LoginActivity.connectedUser?.id
                                    val userFriendsRef = database.getReference("user_friends")

                                    userFriendsRef.orderByChild("userXId").equalTo(currentUserUid).addListenerForSingleValueEvent(
                                        object : ValueEventListener {
                                            override fun onDataChange(dataSnapshotX: DataSnapshot) {
                                                dataSnapshotX.children.forEach { friendSnapshot ->
                                                    val userFriendData = friendSnapshot.getValue(UserFriendData::class.java)
                                                    if (userFriendData?.userYId == friendId) {
                                                        friendSnapshot.ref.removeValue()
                                                        Toast.makeText(applicationContext, "Friend removed successfully!", Toast.LENGTH_SHORT).show()
                                                        return
                                                    }
                                                }
                                                Toast.makeText(applicationContext, "Friend not found in your list!", Toast.LENGTH_SHORT).show()
                                            }

                                            override fun onCancelled(databaseError: DatabaseError) {
                                                Log.e("Cancel", "Error reading user data", databaseError.toException())
                                            }
                                        }
                                    )
                                } else {
                                    // Prietenul nu există în baza de date
                                    Toast.makeText(applicationContext, "Friend not found!", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Manejarea erorii în caz de eșec al citirii din baza de date
                                Log.e("Cancel", "Error reading user data", databaseError.toException())
                            }
                        }
                    )
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            dialog.show()
        }


    }
}