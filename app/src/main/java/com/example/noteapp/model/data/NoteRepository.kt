package com.example.noteapp.model.data

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDAO) {

    val listAllNotes: LiveData<List<Note>> = noteDao.listAllNotes()

    suspend fun addNote(note: Note) {
        noteDao.addNote(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    fun searchNote(query: String) {
        noteDao.searchNote(query)
    }
}