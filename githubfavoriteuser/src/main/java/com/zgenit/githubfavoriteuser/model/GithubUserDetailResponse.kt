package com.zgenit.githubfavoriteuser.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GithubUserDetailResponse(
    var status: Int = 400,
    var message: String? = null,
    var datas: GithubUserDetail? = GithubUserDetail()
): Parcelable