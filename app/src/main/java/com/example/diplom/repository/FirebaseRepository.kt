package com.example.diplom.repository

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.diplom.model.*
import com.example.diplom.model.AllPhoto.tempList
import com.example.diplom.model.FriendRequest
import com.example.diplom.model.Mall
import com.example.diplom.model.User
import com.example.diplom.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject
import javax.inject.Singleton
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

private const val REALTIMEDB_URL =
    "https://diplom-bafa5-default-rtdb.europe-west1.firebasedatabase.app/"

private const val PHOTO_KEY = "photoLike"

private const val MESSAGE_KEY = "Message"

@Singleton
class FirebaseRepository @Inject constructor() {

    private var auth = FirebaseAuth.getInstance()
    lateinit var entry: (bool: Boolean) -> Unit

    private lateinit var mDatabase: DatabaseReference

    private var database = Firebase.firestore
    private var reference = FirebaseDatabase.getInstance().getReferenceFromUrl(REALTIMEDB_URL)
    private var connectionsCount = 0

    lateinit var makeToast: (s: String) -> Unit

    var email = auth.currentUser?.email.toString()
    private var storage = FirebaseStorage.getInstance()
        .getReference("images/${auth.currentUser?.email.toString()}")

    private var isRequestExists: Boolean? = null

    val requestList = MutableLiveData<ArrayList<FriendRequest>>(arrayListOf())
    val friendList = MutableLiveData<ArrayList<FriendRequest>>(arrayListOf())
    val messageList = MutableLiveData<ArrayList<Message>>(arrayListOf())
    val chatList = MutableLiveData<ArrayList<Chat>>(arrayListOf())


    fun checkUser(): Boolean {
        return auth.currentUser != null
    }

    var malls = MutableLiveData<ArrayList<Mall>>()
    var listOfUsers = MutableLiveData<ArrayList<User>>()

    var dataList = MutableLiveData<ArrayList<PhotoLikesInfo>>()

    fun nickName(): String = auth.currentUser?.email.toString()

    fun getUserPhoto(): String {
        if (checkUser()) {
            return auth.currentUser!!.photoUrl.toString()
        } else {
            Log.d(TAG, "User is not logged in")
            return ""
        }
    }

    fun getUserNickName(): String {
        if (checkUser()) {
            return auth.currentUser!!.email.toString()
        } else {
            Log.d(TAG, "User is not logged in")
            return ""
        }
    }

    fun signOut() = auth.signOut()

    fun loadImage(image: Uri?) {
        val storage = FirebaseStorage.getInstance()
            .getReference("images/${auth.currentUser!!.email.toString()}")
        if (image != null) {
            storage.putFile(image).addOnCompleteListener {
                if (it.isSuccessful) {
                    entry(true)
                } else {
                    entry(false)
                }
            }
        }
    }

    fun loadImagePhotoInfo(image: Uri?, location: String) {
        if (image != null) {
            val fileName = "${auth.currentUser!!.email.toString()}|$location"
            val storageSavePhotoInfo = FirebaseStorage.getInstance()
                .getReference("usersPhoto/$fileName")
            storageSavePhotoInfo.putFile(image).addOnCompleteListener {
                if (it.isSuccessful) {
                    entry(true)
                } else {
                    entry(false)
                }
            }
        }
    }

    fun signIn(email: String, pass: String): Observable<FirebaseUser> {
        return BehaviorSubject.create<FirebaseUser>().apply {
            auth.signInWithEmailAndPassword(
                email, pass
            ).addOnSuccessListener {
                auth.currentUser?.let { onNext(it) }
                onComplete()
            }
                .addOnFailureListener {
                    onError(Exception(it))
                }
        }.subscribeOn(Schedulers.io())
    }

    fun register(user: User): Observable<FirebaseUser> {
        return BehaviorSubject.create<FirebaseUser>().apply {
            auth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        auth.currentUser?.let { onNext(it) }
                        setUser(user)
                        onComplete()
                    } else {
                        onError(Exception(task.exception))
                    }
                }
        }.subscribeOn(Schedulers.io())
    }

    fun setPhotoInfo(photo: SavePhotoItem) {
        val photoHashMap = hashMapOf(
            "id" to photo.id,
            "nameMall" to photo.nameMall,
            "price" to photo.price
        )

        database.collection("photoInfo")
            .add(photoHashMap)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Photo added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun firebaseAuthWithGoogle(idToken: String): Observable<FirebaseUser> {
        return BehaviorSubject.create<FirebaseUser>().apply {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    if (authResult.additionalUserInfo!!.isNewUser) {
                        auth.currentUser?.let { onNext(it) }
                        onComplete()
                    }
                }
                .addOnFailureListener {
                    onError(Exception(it))
                }
        }
    }

    suspend fun getMallsFromFirebase() {
        database.collection("malls").get().addOnSuccessListener { result ->
            val response = arrayListOf<Mall>()
            for (document in result) {
                Log.d(TAG, "${document.id} => ${document.data}")
                response.add(document.toObject(Mall::class.java))
            }
            malls.postValue(response)
        }.addOnFailureListener {
            Log.w(TAG, "Error get malls", it)
        }
    }

    fun resetPassword(email: String): Observable<FirebaseUser> {
        return BehaviorSubject.create<FirebaseUser>().apply {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        auth.currentUser?.let { onNext(it) }
                        onComplete()
                    } else {
                        onError(Exception(task.exception))
                    }
                }
        }.subscribeOn(Schedulers.io())
    }

    fun getInfoAboutUsersFromFirebase() {
        database.collection("users").get().addOnSuccessListener { result ->
            val response = arrayListOf<User>()
            for (document in result) {
                Log.d(TAG, "${document.id} => ${document.data}")
                response.add(document.toObject(User::class.java))
            }
            listOfUsers.postValue(response)
        }.addOnFailureListener {
            Log.w(TAG, "Error", it)
        }
    }

    fun setPhotoFromDBRealTime(photo: PhotoLikesInfo) {
        mDatabase = FirebaseDatabase.getInstance().getReference(PHOTO_KEY)
        val id = photo.idImage
        mDatabase.child(id).setValue(
            photo
        ) { error, _ ->
            error?.message?.let { Log.w(TAG, it) }
        }
        mDatabase.child(id).child("listUsersLike").setValue(
            null
        ) { error, _ ->
            error?.message?.let { Log.w(TAG, it) }
        }
    }

    fun setPhotoFromDBRealTimeTest(photo: String, list: ArrayList<String>) {
        mDatabase = FirebaseDatabase.getInstance().getReference(PHOTO_KEY)
        list.add(auth.currentUser!!.email.toString())
        mDatabase.child(photo).child("listUsersLike").setValue(
            list
        ) { error, _ ->
            error?.message?.let { Log.w(TAG, it) }
        }
    }

    fun delPhotoFromDBRealTimeTest(photo: String, list: ArrayList<String>) {
        mDatabase = FirebaseDatabase.getInstance().getReference(PHOTO_KEY)
        list.remove(auth.currentUser!!.email.toString())
        mDatabase.child(photo).child("listUsersLike").setValue(
            list
        ) { error, _ ->
            error?.message?.let { Log.w(TAG, it) }
        }
    }

    fun getDataFromDB() {
        reference.child("photoLike").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (data in it.result.children) {
                    data.getValue(PhotoLikesInfo::class.java).let {
                        tempList.add(it!!)
                    }
                }
            }
        }
    }


    fun sendFriendRequest(friendEmail: String) {
        val user = auth.currentUser
        database.collection("users").get().addOnSuccessListener {
            var showToast = false
            for (document in it) {
                if (document.toObject(User::class.java).email == friendEmail) {
                    checkRequest(user?.email, friendEmail)
                    showToast = false
                    break
                } else showToast = true
            }
            if (showToast)
                makeToast("This user is not exists")

        }.addOnFailureListener {
            Log.d(TAG, it.message.toString())
        }
    }

    private fun checkRequest(currentUserEmail: String?, friendEmail: String) {
        connectionsCount = 0
        reference.child("Friends").get().addOnCompleteListener {
            if (it.isSuccessful) {
                isRequestExists = false
                var br = false
                for (data in it.result.children) {
                    data.getValue(FriendRequest::class.java).let {
                        if ((it?.firstEmail.equals(currentUserEmail) && it?.secondEmail.equals(
                                friendEmail
                            ) || it?.firstEmail.equals(friendEmail) && it?.secondEmail.equals(
                                currentUserEmail
                            ))
                        ) {
                            isRequestExists = true
                            makeToast("This request is exists")
                            br = true
                        }
                        connectionsCount++
                    }
                    if (br) break
                }
                if (isRequestExists == false) {
                    sendData(friendEmail)
                    return@addOnCompleteListener
                }
            }
        }
    }

    private fun sendData(friendEmail: String) {
        val user = auth.currentUser
        reference.child("Friends").child("FriendRequest${connectionsCount++}")
            .setValue(FriendRequest(connectionsCount.toString(), user!!.email, friendEmail, false))
            .addOnFailureListener {
                Log.d(TAG, it.message.toString())
            }.addOnCompleteListener {
                makeToast("Request send")
            }
    }

    suspend fun getRequests() {
        val currentUserEmail = auth.currentUser?.email
        reference.child("Friends").get().addOnCompleteListener {
            if (it.isSuccessful) {
                val tempRequests = arrayListOf<FriendRequest>()
                val tempFriends = arrayListOf<FriendRequest>()
                for (data in it.result.children) {

                    data.getValue(FriendRequest::class.java).let {
                        if (it?.secondEmail.equals(currentUserEmail)) {
                            if (it?.confirmation!!) {
                                if (!tempFriends.contains(it))
                                    tempFriends.add(it)
                            } else
                                if (!tempRequests.contains(it))
                                    tempRequests.add(it)
                        }
                        if (it?.firstEmail.equals(currentUserEmail))
                            if (it?.confirmation!!) {
                                if (!tempFriends.contains(it))
                                    tempFriends.add(it)
                            }
                    }

                }
                if (tempRequests.isNotEmpty()) {
                    requestList.postValue(tempRequests)
                }
                if (tempFriends.isNotEmpty()) {
                    friendList.postValue(tempFriends)
                }
            }
        }
    }

    fun acceptRequest(request: FriendRequest) {
        val newRequest = FriendRequest(request.id, request.firstEmail, request.secondEmail, true)
        val requestValues = newRequest.toMap()

        val childUpdates = hashMapOf<String, Any>(
            "Friends/FriendRequest${request.id}" to requestValues
        )

        reference.updateChildren(childUpdates)
    }


    private fun setUser(user: User) {
        val userHashMap = hashMapOf(
            "first" to user.name,
            "last" to user.surname,
            "city" to user.city,
            "nickname" to user.nickname,
            "email" to user.email,
            "password" to user.password
        )

        database.collection("users")
            .add(userHashMap)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun setInfoFromDBRealTime(item: Message) {
        mDatabase = FirebaseDatabase.getInstance().getReference(MESSAGE_KEY)
        val id = item.firstEmail
        mDatabase.child(id).setValue(
            item
        ) { error, _ ->
            error?.message?.let { Log.w(TAG, it) }
        }
    }

    fun setNewMessage(list: ArrayList<Chat>, message: Chat) {
        mDatabase = FirebaseDatabase.getInstance().getReference(MESSAGE_KEY)
        reference.child("Message").child(reference.child("Message").push().key ?: "bla")
            .setValue(
                message
            ) { error, _ ->
                error?.message?.let { Log.w(TAG, it) }
            }
        //getListOfMessages()
    }

    fun getListOfMessages() {
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = arrayListOf<Chat>()
                for (data in snapshot.children) {
                    data.getValue(Chat::class.java).let {
                        tempList.add(it!!)
                    }
                }
                if (tempList.isNotEmpty()) {
                    chatList.postValue(tempList)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                //do nothing
            }

        }
        reference.child("Message").addListenerForSingleValueEvent(eventListener)
        reference.child("Message").addValueEventListener(eventListener)
    }
}
