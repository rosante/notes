package com.ruzzante.contentprovider.database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.media.UnsupportedSchemeException
import android.net.Uri
import android.provider.BaseColumns._ID
import android.util.Log
import com.ruzzante.contentprovider.database.NotesDatabaseHelper.Companion.TABLE_NOTES

class NotesProvider : ContentProvider() {

    private lateinit var mUriMatcher: UriMatcher
    private lateinit var dbHelper: NotesDatabaseHelper

    override fun onCreate(): Boolean {
        //validação da URI
        mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        //Sempre que foi chamado nosso com.ruzzante.contentprovider.provider/notes ele deve trazer todas nossas notes
        mUriMatcher.addURI(AUTHORITY, "notes", NOTES)
        //Este vai ser chamado com o ID
        mUriMatcher.addURI(AUTHORITY, "notes/#", NOTES_BY_ID)
        if (context != null) {dbHelper = NotesDatabaseHelper(context as Context) }
        return true
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        // Verifica se o URI está procurando o delete por ID
        if (mUriMatcher.match(uri) == NOTES_BY_ID){
            //Verifica se a base está com permissão de escrita
            val db: SQLiteDatabase = dbHelper.writableDatabase
            // =? Pergunta de qual ID será deletado e o uri.lastPath que vem o ID na solicitação
            val linesAffect:Int = db.delete(TABLE_NOTES, "$_ID =?", arrayOf(uri.lastPathSegment))
            db.close()
            // Para tudo que for feito no COntent Provider devemos notificar as alterações
            context?.contentResolver?.notifyChange(uri, null)
            return linesAffect
        }
        else{
            throw UnsupportedSchemeException("Uri inválido para exclusão")
        }
    }

    override fun getType(uri: Uri): String? = throw UnsupportedSchemeException("Uri não implementada")

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
       if(mUriMatcher.match(uri) == NOTES){
           val db: SQLiteDatabase = dbHelper.writableDatabase
           val id:Long = db.insert(TABLE_NOTES, null, values)
           val insertUri = Uri.withAppendedPath(BASE_URI, id.toString())
           db.close()
           // Notificando o Provider da inserção
           context?.contentResolver?.notifyChange(insertUri, null)
           return insertUri
       }else{
           throw UnsupportedSchemeException("Uri inválido para inclusão")
       }
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
       return when {
           mUriMatcher.match(uri) == NOTES -> {
               val db: SQLiteDatabase = dbHelper.readableDatabase
               val cursor = db.query(TABLE_NOTES, projection, selection, selectionArgs, null, null, sortOrder)
               cursor.setNotificationUri(context?.contentResolver, uri)
               cursor
           }
           mUriMatcher.match(uri) == NOTES_BY_ID -> {
               val db: SQLiteDatabase = dbHelper.readableDatabase
               val cursor = db.query(TABLE_NOTES, projection, "$_ID = ?", arrayOf(uri.lastPathSegment), null, null, sortOrder)
               cursor.setNotificationUri(context?.contentResolver, uri)
               cursor
           }
           else -> {
               throw UnsupportedSchemeException("Uri não implementada")
           }
       }
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        if(mUriMatcher.match(uri) == NOTES_BY_ID){
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val linesAffect:Int = db.update(TABLE_NOTES, values, "$_ID =? ", arrayOf(uri.lastPathSegment))
            val updateUri = Uri.withAppendedPath(BASE_URI, linesAffect.toString())
            db.close()
            // Notificando o Provider da inserção
            context?.contentResolver?.notifyChange(updateUri, null)
            return linesAffect
        }else{
            throw UnsupportedSchemeException("Uri inválido para atualização")
        }
    }
    companion object{
        const val AUTHORITY = "com.ruzzante.contentprovider.provider"
        //Configurando a base URI como content://com.ruzzante.contentprovider.provider
        val BASE_URI = Uri.parse("content://$AUTHORITY")
        //Configurando a URI Notes como content://com.ruzzante.contentprovider.provider/notes
        val URI_NOTES = Uri.withAppendedPath(BASE_URI, "notes")
        const val NOTES = 1
        const val NOTES_BY_ID = 2
    }
}