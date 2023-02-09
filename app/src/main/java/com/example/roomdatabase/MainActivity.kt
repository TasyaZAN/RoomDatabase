package com.example.roomdatabase

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdatabase.room.Constant
import com.example.roomdatabase.room.Note
import com.example.roomdatabase.room.NoteDB
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.lang.reflect.Type

class MainActivity : AppCompatActivity() {

val db by lazy {NoteDB (this) }
    lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
    }

    override fun onStart() {
        super.onStart()
        loadNote()
    }
    fun loadNote(){
        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.noteDao().getNotes()
            Log.d("MainActivity","dbResponse: $notes")
            withContext(Dispatchers.Main) {
                noteAdapter.setData(notes)
            }
        }
    }

    fun button_create (view: View){
        intentEdit(0,Constant.TYPE_CREATE)
    }

    fun intentEdit(noteId: Int,intentType: Int){
        val pindah = Intent (applicationContext, EditActivity::class.java)
        startActivity(pindah
            .putExtra("intent_id",noteId)
            .putExtra("intent_type",intentType)
        )

    }

    private fun setupRecyclerView(){
        noteAdapter = NoteAdapter(arrayListOf(),object :NoteAdapter.OnAdapterListener{
            override fun onClick(note: Note) {
            // Toast.makeText(applicationContext, note.title, Toast.LENGTH_SHORT).show()

                //read detail note
                intentEdit(note.id,Constant.TYPE_READ)

            }

            override fun onUpdate(note: Note) {
                intentEdit(note.id,Constant.TYPE_UPDATE)
            }

            override fun onDelete(note: Note) {
                deleteDialog(note)
            }
        })
        list_note.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = noteAdapter
        }
    }
    private fun deleteDialog(note:Note){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Konfirmasi")
            setMessage("JADI DI HAPUS ${note.title}? ")
            setNegativeButton("Batal") { dialogInterface, i ->
            dialogInterface.dismiss()
            }
            setPositiveButton("Hapus") { dialogInterface, i ->
                dialogInterface.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    db.noteDao().deleteNote(note)
                    loadNote()
                }
            }
        }
        alertDialog.show()
    }
    }
