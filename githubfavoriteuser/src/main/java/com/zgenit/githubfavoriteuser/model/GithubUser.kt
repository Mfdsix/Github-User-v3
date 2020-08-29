package com.zgenit.githubfavoriteuser.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GithubUser(
    var id: Int = 0,
    var nodeId: String? = null,
    var username: String? = null,
    var avatar: String? = null
): Parcelable