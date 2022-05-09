package com.example.diplom.repository

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.diplom.model.GalleryPhotoItem
import com.example.diplom.model.SavePhotoItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
object PhotoUserRepository {

    private val storage = Firebase.storage
    private val listRef = storage.reference.child("usersPhoto/")
    private val listRefAvatar = storage.reference.child("images/")

    private var auth = FirebaseAuth.getInstance()
    val email = auth.currentUser!!.email

    @SuppressLint("StaticFieldLeak")
    private var database = Firebase.firestore
    var photoUserAvatar: String? = null
    var allPhoto = MutableLiveData<ArrayList<SavePhotoItem>>()
    var photoProfileList = MutableLiveData<ArrayList<GalleryPhotoItem>>()

    var allPhotoList = MutableLiveData<ArrayList<GalleryPhotoItem>>()
    lateinit var delete: (bool: Boolean) -> Unit

    fun getAllPhotoFromFirebase() {
        val response = arrayListOf<SavePhotoItem>()
        database.collection("photoInfo").get().addOnSuccessListener { result ->
            for (document in result) {
                Log.d(TAG, "${document.id} => ${document.data}")
                response.add(document.toObject(SavePhotoItem::class.java))
            }
            allPhoto.postValue(response)
        }.addOnFailureListener {
            Log.w(TAG, "Error", it)
        }
    }

    private val usersCollectionRef = Firebase.firestore.collection("photoInfo")

    fun deletePhotoFromDatabase(item: SavePhotoItem, path: String) = CoroutineScope(Dispatchers.IO).launch {
        val listRef = storage.reference.child("usersPhoto/$path")
        listRef.delete().addOnSuccessListener {
            delete(true)
            loadPhoto()
        }.addOnFailureListener{
            delete(false)
        }
        val userQuery = usersCollectionRef
            .whereEqualTo("id", item.id)
            .whereEqualTo("price", item.price)
            .whereEqualTo("nameMall", item.nameMall).get()

        userQuery.addOnSuccessListener {
            for (document in it) {
                usersCollectionRef.document(document.id).delete().addOnSuccessListener {
                    delete(true)
                }.addOnFailureListener {
                    delete(false)
                }
            }
        }
    }

    fun loadPhotoAll() {
        val response = arrayListOf<GalleryPhotoItem>()
        listRef.listAll()
            .addOnSuccessListener { (items) ->
                items.forEach { item ->
                    // All the items under listRef.
                    if (!item.path.contains(auth.currentUser?.email.toString())) {
                        response.add(GalleryPhotoItem(item.path))
                    }
                }
                allPhotoList.postValue(response)
            }
    }

    fun loadPhoto() {
        val response = arrayListOf<GalleryPhotoItem>()
        listRef.listAll()
            .addOnSuccessListener { (items) ->
                items.forEach { item ->
                    // All the items under listRef.
                    if  (item.path.contains(
                            auth.currentUser?.email.toString()
                        )
                    ) {
                        response.add(GalleryPhotoItem(item.path))
                    }
                }
                photoProfileList.postValue(response)
            }
    }

    fun loadPhotoAvatar() {
        listRefAvatar.listAll()
            .addOnSuccessListener { (items) ->
                items.forEach { item ->
                    // All the items under listRef.
                    if (item.path.contains(auth.currentUser?.email.toString())) {
                        photoUserAvatar = item.path
                    }
                }
            }
    }
}