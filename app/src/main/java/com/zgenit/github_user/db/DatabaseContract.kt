package com.zgenit.github_user.db

import android.net.Uri
import android.provider.BaseColumns

object DatabaseContract {

    const val AUTHORITY = "com.zgenit.github_user"
    const val SCHEME = "content"

    class GithubUserColumns: BaseColumns {

        companion object{
            const val TABLE_NAME = "favorite_users"
            const val _ID = "_id"
            const val USER_ID = "user_id"
            const val NODE_ID = "node_id"
            const val USERNAME = "username"
            const val AVATAR = "avatar"

            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }
}