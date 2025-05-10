package br.unipar.notafacil.controller

import br.unipar.notafacil.model.Note
import br.unipar.notafacil.service.NoteService
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notafacil")
@CrossOrigin(origins = ["http://localhost:5173"])
class NoteController(
    private val noteService: NoteService,
) {

    private fun getUid(): String? {
        val auth = SecurityContextHolder.getContext().authentication
        return auth?.principal as? String
    }

    @PostMapping("/post")
    fun cadastrarNotas(@RequestBody note: Note): ResponseEntity<Note> {
        val uid = getUid() ?: return ResponseEntity.status(401).build()

        if (note.id == null) {
            return ResponseEntity.badRequest().build()
        }

        return ResponseEntity.ok(
            noteService.registrarNota(
                note.id,
                note.title,
                note.content,
                note.lastEdited,
                uid
            )
        )
    }

    @GetMapping("/get")
    fun buscarTodos(): ResponseEntity<List<Note>> {
        val uid = getUid() ?: return ResponseEntity.status(401).build()
        val notes = noteService.getNotas(uid)
        return ResponseEntity.ok(notes)
    }

    data class DeleteRequest @JsonCreator constructor(
        @JsonProperty("ids") val ids: List<String>
    )

    @PostMapping("/delete")
    fun excluirNota(@RequestBody request: DeleteRequest): ResponseEntity<Void> {
        val uid = getUid() ?: return ResponseEntity.status(401).build()

        return if (noteService.excluirNotas(request.ids, uid)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/update/{id}")
    fun atualizarNota(
        @PathVariable id: String,
        @RequestBody updatedNote: Note
    ): ResponseEntity<Note> {
        val uid = getUid() ?: return ResponseEntity.status(401).build()

        val noteAtualizada = noteService.atualizarNota(id, updatedNote, uid)
        return if (noteAtualizada != null) {
            ResponseEntity.ok(noteAtualizada)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
