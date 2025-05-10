package br.unipar.notafacil.repository

import br.unipar.notafacil.model.Note
import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import org.springframework.stereotype.Repository

@Repository
class NoteRepository(
    private val firestore: Firestore = FirestoreClient.getFirestore()
) {
    private val collectionName = "notes"

    fun salvar(note: Note): Note {
        firestore.collection(collectionName)
            .document(note.id)
            .set(note)
        return note
    }

    fun buscarNotas(): List<Note> {
        val snapshot = firestore.collection(collectionName)
            .get().get()

        return snapshot.documents.mapNotNull { it.toObject(Note::class.java) }
    }

    fun buscarNotasPorUsuario(userId: String): List<Note> {
        val snapshot = firestore.collection(collectionName)
            .whereEqualTo("userId", userId)
            .get().get()

        return snapshot.documents.mapNotNull { it.toObject(Note::class.java) }
    }

    fun buscarNotaPorId(id: String): Note? {
        val snapshot = firestore.collection(collectionName)
            .document(id)
            .get().get()

        return if (snapshot.exists()) {
            snapshot.toObject(Note::class.java)
        } else {
            null
        }
    }

    fun excluirNotas(ids: List<String>): Boolean {
        ids.forEach { id ->
            firestore.collection(collectionName).document(id).delete()
        }
        return true
    }
}
