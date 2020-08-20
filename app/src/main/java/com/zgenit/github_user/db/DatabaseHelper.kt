package com.zgenit.github_user.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.zgenit.github_user.db.DatabaseContract.GithubUserColumns.Companion.TABLE_NAME
import com.zgenit.github_user.db.DatabaseContract.GithubUserColumns

internal class DatabaseHelper(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "db_github_user"
        private const val DATABASE_VERSION = 1

        private val SQL_CREATE_TABLE_FAVORITE = "CREATE TABLE $TABLE_NAME" +
                " (${GithubUserColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                " ${GithubUserColumns.USER_ID} INTEGER NOT NULL," +
                " ${GithubUserColumns.USERNAME} TEXT NOT NULL," +
                " ${GithubUserColumns.NODE_ID} TEXT NOT NULL," +
                " ${GithubUserColumns.AVATAR} TEXT NOT NULL)"
        private val SQL_DROP_TABLE_FAVORITE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_TABLE_FAVORITE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, old: Int, new: Int) {
        db?.execSQL(SQL_DROP_TABLE_FAVORITE)
        onCreate(db)
    }
}