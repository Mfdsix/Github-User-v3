package com.zgenit.githubfavoriteuser.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GithubUserDetail(
    var id: Int = 0,
    var nodeId: String? = null,
    var username: String? = null,
    var fullname: String? = null,
    var avatar: String? = null,
    var publicRepos: Int? = 0,
    var followers: Int? = 0,
    var following: Int? = 0,
    var office: String? = null,
    var location: String? = null
): Parcelable