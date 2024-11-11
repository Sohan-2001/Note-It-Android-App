package com.example.noteit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotesAdapter
    private lateinit var tvEmptyMessage: TextView
    private var notesList = mutableListOf<Note>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        findViewById<ImageView>(R.id.addButton).setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

        // Load notes asynchronously
        loadNotesAsync()
    }

    private fun loadNotesAsync() {
        coroutineScope.launch {
            notesList = withContext(Dispatchers.IO) {
                loadNotes()
            }
            adapter = NotesAdapter(notesList, this@MainActivity::onNoteClick, this@MainActivity::onDeleteClick)
            recyclerView.adapter = adapter

            // Show or hide the empty message
            tvEmptyMessage.visibility = if (notesList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun loadNotes(): MutableList<Note> {
        val file = File(filesDir, "notes.json")
        if (file.exists()) {
            val type = object : TypeToken<MutableList<Note>>() {}.type
            return Gson().fromJson(file.readText(), type)
        }
        return mutableListOf()
    }

    private fun saveNotesAsync() {
        coroutineScope.launch(Dispatchers.IO) {
            saveNotes()
        }
    }

    private fun saveNotes() {
        val file = File(filesDir, "notes.json")
        file.writeText(Gson().toJson(notesList))
    }

    override fun onResume() {
        super.onResume()
        loadNotesAsync()
    }

    private fun onNoteClick(note: Note) {
        val intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("note", note)
        startActivity(intent)
    }

    private fun onDeleteClick(note: Note) {
        notesList.remove(note)
        saveNotesAsync()
        adapter.notifyDataSetChanged()

        // Update visibility of the empty message
        tvEmptyMessage.visibility = if (notesList.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}
