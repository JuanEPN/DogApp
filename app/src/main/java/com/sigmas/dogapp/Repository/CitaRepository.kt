package com.sigmas.dogapp.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.sigmas.dogapp.Data.Model.Cita
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CitaRepository @Inject constructor( private val db: FirebaseFirestore) {

    private val citasLiveData = MutableLiveData<List<Cita>>()

    fun getAllCitas(): LiveData<List<Cita>> {
        db.collection("citas")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    citasLiveData.postValue(emptyList())
                    return@addSnapshotListener
                }

                val citas = snapshot.documents.mapNotNull { document ->
                    try {
                        // Forzamos que el ID se lea como string desde Firestore
                        val cita = document.toObject(Cita::class.java)
                        cita?.copy(id = document.id)
                    } catch (e: Exception) {
                        null
                    }
                }

                citasLiveData.postValue(citas)
            }

        return citasLiveData
    }

    suspend fun insertCita(cita: Cita) {
        val counterRef = db.collection("metadata").document("citaCounter")

        db.runTransaction { transaction ->
            val snapshot = transaction.get(counterRef)
            val currentId = snapshot.getLong("lastId") ?: 0L
            val newId = currentId + 1

            // Asignar el nuevo ID numérico como String (si `id` sigue siendo String en tu clase)
            val nuevaCita = cita.copy(id = newId.toString())

            val citaRef = db.collection("citas").document(newId.toString())
            transaction.set(citaRef, nuevaCita)
            transaction.update(counterRef, "lastId", newId)
        }.await()
    }


    suspend fun updateCita(cita: Cita) {
        db.collection("citas").document(cita.id)
            .set(cita, SetOptions.merge())
            .await()
    }

    suspend fun getCitaById(id: String): Cita? {
        val snapshot = db.collection("citas").document(id).get().await()
        val cita = snapshot.toObject(Cita::class.java)
        return cita?.copy(id = snapshot.id)
    }

    suspend fun deleteCita(cita: Cita) {
        db.collection("citas").document(cita.id).delete().await()
    }
}

