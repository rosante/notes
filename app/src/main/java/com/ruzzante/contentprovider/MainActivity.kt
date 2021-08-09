package com.ruzzante.contentprovider

import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
        noteAdd.setOnClickListener{}

        adapter = NotesAdapter()
        adapter.setHasStableIds(true)

        noteRecyclerView = findViewById(R.id.rv_notes)
        noteRecyclerView.layoutManager = LinearLayoutManager(this)
        noteRecyclerView.adapter = adapter
    }

    // Instancia o que será buscado
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> =
        CursorLoader(this, URI_NOTES, null, null, null, TITLE_NOTES)

    // Manipula os dados recebidos
    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if (data != null){}
    }

    // Termina a pesquisa em segundo plano
    override fun onLoaderReset(loader: Loader<Cursor>) {
        TODO("Not yet implemented")
    }
}