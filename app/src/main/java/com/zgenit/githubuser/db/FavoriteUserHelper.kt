package com.zgenit.githubuser.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.zgenit.githubuser.db.DatabaseContract.GithubUserColumns.Companion.TABLE_NAME
import com.zgenit.githubuser.db.DatabaseContract.GithubUserColumns.Companion.USER_ID
import com.zgenit.githubuser.db.DatabaseContract.GithubUserColumns.Companion._ID
import java.sql.SQLException

class FavoriteUserHelper(context: Context) {

    companion object{
        private const val DATABASE_TABLE = TABLE_NAME
        private lateinit var databaseHelper: DatabaseHelper
        private var INSTANCE: FavoriteUserHelper? = null
        private lateinit var database: SQLiteDatabase

        fun getInstance(context: Context): FavoriteUserHelper =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: FavoriteUserHelper(context)
            }
    }

    init{
        databaseHelper = DatabaseHelper(context)
    }

    @Throws(SQLException::class)
    fun open(){
        database = databaseHelper.writableDatabase
    }

    fun close(){
        databaseHelper.close()
        if(database.isOpen)
            database.close()
    }

    fun getAll(): Cursor{
        return database.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "$_ID ASC"
        )
    }

    fun getById(userId: String): Cursor{
        return database.query(
            DATABASE_TABLE,
            null,
            "$USER_ID = ?",
            arrayOf(userId),
            null,
            null,
            null,
            null
        )
    }

    fun insert(values: ContentValues): Long{
        return database.insert(DATABASE_TABLE, null, values)
    }

    fun delete(id: Int): Int{
        return database.delete(DATABASE_TABLE, "$USER_ID = '$id'", null)
    }
}