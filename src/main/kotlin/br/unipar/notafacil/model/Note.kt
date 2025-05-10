package br.unipar.notafacil.model

data class Note (
    val id: String = "",
    var title: String = "",
    var content: String = "",
    var lastEdited: String = "",
    var userId: String = ""
)
