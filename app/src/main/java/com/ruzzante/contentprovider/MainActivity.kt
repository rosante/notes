package com.ruzzante.contentprovider

import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns._ID
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ruzzante.contentprovider.`interface`.NoteClickedListener
import com.ruzzante.contentprovider.database.NotesDatabaseHelper.Companion.TITLE_NOTES
import com.ruzzante.contentprovider.database.NotesProvider.Companion.URI_NOTES

//Adicionado Loader Manager na Activity, que não prende a tela e evita os erros de Thread do front android. A busca é feita em segundo plano
class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    lateinit var noteRecyclerView: RecyclerView
    lateinit var noteAdd:FloatingActionButton
    lateinit var adapter: NotesAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        noteAdd = findViewById(R.id.btn_add)
        noteAdd.setOnClickListener{
            NotesDetailFragment().show(supportFragmentManager, "dialog1")
        }

        adapter = NotesAdapter(object: NoteClickedListener{
            override fun noteClickedItem(cursor: Cursor) {
                val id : Long = cursor?.getLong(cursor.getColumnIndex(_ID))
                val fragment = NotesDetailFragment.newIntance(id)
                fragment.show(supportFragmentManager, "dialog")
            }

            override fun noteRemoveItem(cursor: Cursor?) {
                val id : Long? = cursor?.getLong(cursor.getColumnIndex(_ID))
                contentResolver.delete(Uri.withAppendedPath(URI_NOTES, id.toString()), null, null)
            }

        })
        adapter.setHasStableIds(true)

        noteRecyclerView = findViewById(R.id.rv_notes)
        noteRecyclerView.layoutManager = LinearLayoutManager(this)
        noteRecyclerView.adapter = adapter

        LoaderManager.getInstance(this).initLoader(0, null, this)
    }

    // Instancia o que será buscado
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> =
        CursorLoader(this, URI_NOTES, null, null, null, TITLE_NOTES)

    // Manipula os dados recebidos
    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if (data != null){
            adapter.setCursor(data)
        }
    }

    // Termina a pesquisa em segundo plano
    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter.setCursor(null)
    }
}