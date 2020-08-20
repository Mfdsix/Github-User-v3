package com.zgenit.github_user.db

import android.provider.BaseColumns

internal class DatabaseContract {

    internal class GithubUserColumns: BaseColumns {
        companion object{
            const val TABLE_NAME = "favorite_users"
            const val _ID = "_id"
            const val USER_ID = "user_id"
            const val NODE_ID = "node_id"
            const val USERNAME = "username"
            const val AVATAR = "avatar"
        }
    }
}