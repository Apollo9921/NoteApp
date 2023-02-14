package com.example.noteapp.model.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDAO {

     @Insert(onConflict = OnConflictStrategy.IGNORE)
     suspend fun addNote(note: Note)

     @Query("SELECT * FROM note_table ORDER BY date DESC")
     fun listAllNotes(): LiveData<List<Note>>

     @Query("SELECT * FROM note_table WHERE id =:id")
     fun listSelectedNote(id: Int): LiveData<Note>

     @Update
     suspend fun updateNote(note: Note)

     @Delete
     suspend fun deleteNote(note: Note)

     @Query("SELECT * FROM note_table WHERE title LIKE :query ORDER BY date DESC")
     fun searchNote(query: String): LiveData<List<Note>>

}