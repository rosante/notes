package com.ruzzante.contentprovider

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.textfield.TextInputEditText
import com.ruzzante.contentprovider.database.NotesDatabaseHelper.Companion.DESCRIPTION_NOTES
import com.ruzzante.contentprovider.database.NotesDatabaseHelper.Companion.TITLE_NOTES
import com.ruzzante.contentprovider.database.NotesProvider.Companion.URI_NOTES

class NotesDetailFragment : DialogFragment(), DialogInterface.OnClickListener {

    private lateinit var noteEditTitle: EditText
    private lateinit var noteEditDescription: EditText
    private var id:Long = 0

    companion object{
        private const val EXTRA_ID = "id"
        fun newIntance(id:Long):NotesDetailFragment {
            val bundle = Bundle()
            bundle.putLong(EXTRA_ID, id)
            val notesFragment = NotesDetailFragment()
            notesFragment.arguments = bundle
            return notesFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view: View? = activity?.layoutInflater?.inflate(R.layout.note_detail, null)
        noteEditTitle = view?.findViewById(R.id.note_edt_title) as TextInputEditText
        noteEditDescription = view.findViewById(R.id.note_edt_description) as TextInputEditText

        var newNote = true
        if (arguments != null && arguments?.getLong(EXTRA_ID) != 0L){
            id = arguments?.getLong(EXTRA_ID) as Long
            var uri = Uri.withAppendedPath(URI_NOTES, id.toString())
            val cursor = activity?.contentResolver?.query(uri, null, null, null, null)
            if (cursor?.moveToNext() as Boolean){
                newNote = false
                noteEditTitle.setText(cursor.getString(cursor.getColumnIndex(TITLE_NOTES)))
                noteEditDescription.setText(cursor.getString(cursor.getColumnIndex(DESCRIPTION_NOTES)))
            }
            cursor.close()
        }
        return AlertDialog.Builder(activity as Activity)
            .setTitle(if (newNote) "Nova Mensagem" else "Editar Mensagem")
            .setView(view)
            .setPositiveButton("Salvar", this)
            .setNegativeButton("Cancelar", this)
            .create()
    }

    override fun onClick(p0: DialogInterface?, p1: Int) {
        val values = ContentValues()
        values.put(TITLE_NOTES, noteEditTitle.text.toString())
        values.put(DESCRIPTION_NOTES, noteEditDescription.text.toString())

        if (id != 0L){
            val uri = Uri.withAppendedPath(URI_NOTES, id.toString())
            context?.contentResolver?.update(uri, values, null, null)
        }else{
            context?.contentResolver?.insert(URI_NOTES, values)
        }

    }

}