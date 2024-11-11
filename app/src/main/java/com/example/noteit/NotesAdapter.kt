package com.example.noteit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(
    private val notesList: List<Note>,
    private val onNoteClick: (Note) -> Unit,
    private val onDeleteClick: (Note) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNoteHeading: TextView = itemView.findViewById(R.id.tvNoteHeading)
        val deleteIcon: ImageView = itemView.findViewById(R.id.deleteIcon)

        fun bind(note: Note) {
            tvNoteHeading.text = note.heading
            itemView.setOnClickListener { onNoteClick(note) }
            deleteIcon.setOnClickListener { onDeleteClick(note) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notesList[position])
    }

    override fun getItemCount(): Int = notesList.size
}
