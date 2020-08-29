package com.zgenit.githubuser.helper

import android.database.Cursor
import com.zgenit.githubuser.db.DatabaseContract
import com.zgenit.githubuser.model.GithubUser

object MappingHelper {

    fun parseToArrayList(notesCursor: Cursor?): ArrayList<GithubUser> {
        val userList = ArrayList<GithubUser>()
        notesCursor?.apply {
            while (moveToNext()) {
                val githubUser = GithubUser(
                    getInt(getColumnIndexOrThrow(DatabaseContract.GithubUserColumns.USER_ID)),
                    getString(getColumnIndexOrThrow(DatabaseContract.GithubUserColumns.NODE_ID)),
                    getString(getColumnIndexOrThrow(DatabaseContract.GithubUserColumns.USERNAME)),
                    getString(getColumnIndexOrThrow(DatabaseContract.GithubUserColumns.AVATAR))
                )
                userList.add(githubUser)
            }
        }
        return userList
    }
}