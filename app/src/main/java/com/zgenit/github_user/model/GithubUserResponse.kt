package com.zgenit.github_user.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GithubUserResponse(
    var status: Int = 400,
    var message: String? = null,
    var datas: ArrayList<GithubUser>? = ArrayList()
): Parcelable