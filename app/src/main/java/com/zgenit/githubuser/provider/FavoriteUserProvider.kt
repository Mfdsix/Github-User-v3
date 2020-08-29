package com.zgenit.githubuser.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.zgenit.githubuser.db.DatabaseContract.AUTHORITY
import com.zgenit.githubuser.db.DatabaseContract.GithubUserColumns.Companion.CONTENT_URI
import com.zgenit.githubuser.db.DatabaseContract.GithubUserColumns.Companion.TABLE_NAME
import com.zgenit.githubuser.db.FavoriteUserHelper

class FavoriteUserProvider : ContentProvider() {

    companion object{
        private const val FAVORITE_USER = 1
        private const val FAVORITE_USER_ID = 2
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        private lateinit var favoriteUserHelper: FavoriteUserHelper

        init{
            sUriMatcher.addURI(AUTHORITY, TABLE_NAME, FAVORITE_USER)
            sUriMatcher.addURI(AUTHORITY, "$TABLE_NAME/#", FAVORITE_USER_ID)
        }
    }

    override fun onCreate(): Boolean {
        favoriteUserHelper = FavoriteUserHelper(context as Context)
        favoriteUserHelper.open()
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {

        return when(sUriMatcher.match(uri)){
            FAVORITE_USER -> favoriteUserHelper.getAll()
            FAVORITE_USER_ID -> favoriteUserHelper.getById(uri.lastPathSegment.toString())
            else -> null
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val added: Long = when(FAVORITE_USER){
            sUriMatcher.match(uri) -> favoriteUserHelper.insert(values?: ContentValues())
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return Uri.parse("$CONTENT_URI/$added")
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val deleted: Int = when(FAVORITE_USER_ID){
            sUriMatcher.match(uri) -> favoriteUserHelper.delete(uri.lastPathSegment?.toInt()?: 0)
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return deleted
    }
}
