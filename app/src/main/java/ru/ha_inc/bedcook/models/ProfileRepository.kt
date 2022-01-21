package ru.ha_inc.bedcook.models

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ProfileRepository(context: Context) {
    companion object {
        private const val SHARED_PREFS_NAME = "bedcook_prefs"
        private const val PROFILE_DOCUMENT_REF = "profileDocumentRef"
        private const val MUSIC_ENABLED = "musicEnabled"
    }

    private val db = Firebase.firestore
    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    private var profileDocumentRef: String
        get() = sharedPreferences?.getString(PROFILE_DOCUMENT_REF, "") ?: ""
        set(value) {
            sharedPreferences?.edit {
                putString(PROFILE_DOCUMENT_REF, value)
            }
        }

    var musicEnabled: Boolean
        get() = sharedPreferences?.getBoolean(MUSIC_ENABLED, true) ?: true
        set(value) {
            sharedPreferences?.edit {
                putBoolean(MUSIC_ENABLED, value)
            }
        }

    fun isProfileStored() = profileDocumentRef.isNotEmpty()

    fun getProfile() = flow {
        val profileRef = profileDocumentRef
        if (profileRef.isNotEmpty()) {
            db.collection("progress").document(profileRef).get()
                .await().toObject<Profile>()?.let {
                    emit(it)
                }
        }
    }.catch { error ->
        error.printStackTrace()
    }

    suspend fun createUser(username: String) {
        val profile = hashMapOf(
            "username" to username,
            "level" to 1,
            "money" to 0,
            "score" to 0
        )
        try {
            db.collection("progress").add(profile).await().let { documentReference ->
                Log.d("DADAYA", "DocumentSnapshot added with ID: ${documentReference.id}")
                profileDocumentRef = documentReference.id
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateUsername(username: String) {
        try {
            db.collection("progress").document(profileDocumentRef)
                .update("username", username).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateProfile(profile: Profile) {
        try {
            val profileMap = hashMapOf(
                "username" to profile.username,
                "level" to profile.level,
                "score" to profile.score,
                "money" to profile.money,
            ).filterValues { it != null }
            db.collection("progress").document(profileDocumentRef)
                .set(profileMap, SetOptions.merge()).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}