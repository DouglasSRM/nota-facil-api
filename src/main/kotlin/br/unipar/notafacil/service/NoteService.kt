package br.unipar.notafacil.service

import br.unipar.notafacil.model.Note
import br.unipar.notafacil.repository.NoteRepository
import org.springframework.stereotype.Service

@Service
class NoteService(
    private val noteRepository: NoteRepository
) {

    fun registrarNota(
        id: String,
        title: String,
        content: String,
        lastEdited: String,
        userId: String
    ): Note {
        return noteRepository.salvar(
            Note(
                id = id,
                title = title,
                content = content,
                lastEdited = lastEdited,
                userId = userId
            )
        )
    }

    fun getNotas(userId: String): List<Note> {
        return noteRepository.buscarNotasPorUsuario(userId)
    }

    fun excluirNotas(ids: List<String>, userId: String): Boolean {
        val notas = ids.mapNotNull { noteRepository.buscarNotaPorId(it) }
        val todasDoUsuario = notas.all { it.userId == userId }

        return if (notas.isNotEmpty() && todasDoUsuario) {
            noteRepository.excluirNotas(ids)
        } else {
            false
        }
    }

    fun atualizarNota(id: String, updatedNote: Note, userId: String): Note? {
        val notaExistente = noteRepository.buscarNotaPorId(id)
        return if (notaExistente != null && notaExistente.userId == userId) {
            val updated = notaExistente.copy(
                title = updatedNote.title,
                content = updatedNote.content,
                lastEdited = updatedNote.lastEdited
            )
            noteRepository.salvar(updated)
        } else {
            null
        }
    }
}
