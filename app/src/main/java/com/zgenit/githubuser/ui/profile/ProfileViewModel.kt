package com.zgenit.githubuser.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.zgenit.githubuser.BuildConfig
import com.zgenit.githubuser.model.GithubUserDetail
import com.zgenit.githubuser.model.GithubUserDetailResponse
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class ProfileViewModel : ViewModel() {

    companion object {
        const val GITHUB_API_URL = "https://api.github.com/users/"
    }

    val githubUserDetail = MutableLiveData<GithubUserDetailResponse>()

    fun setUsers(query: String) {
        var userData: GithubUserDetail
        var userResponse: GithubUserDetailResponse
        val url = "${GITHUB_API_URL}${query}"

        val client = AsyncHttpClient()
        client.addHeader("Accept", "application/vnd.github.v3+json")
        client.addHeader("Authorization", "token ${BuildConfig.GITHUB_API_KEY}")
        client.addHeader("User-Agent", "request")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                try {
                    val result = String(responseBody)
                    val responseObject = JSONObject(result)

                    userData = GithubUserDetail(
                        responseObject.getInt("id"),
                        responseObject.getString("node_id"),
                        responseObject.getString("login"),
                        responseObject.getString("name"),
                        responseObject.getString("avatar_url"),
                        responseObject.getInt("public_repos"),
                        responseObject.getInt("followers"),
                        responseObject.getInt("following"),
                        responseObject.getString("company"),
                        responseObject.getString("location")
                    )
                    userResponse = GithubUserDetailResponse(
                        200,
                        "success",
                        userData
                    )
                    githubUserDetail.postValue(userResponse)

                } catch (e: Exception) {
                    userResponse = GithubUserDetailResponse(
                        400,
                        e.message.toString(),
                        null
                    )
                    githubUserDetail.postValue(userResponse)
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                userResponse = GithubUserDetailResponse(
                    400,
                    error?.message.toString(),
                    null
                )
                githubUserDetail.postValue(userResponse)
                Log.d("onFailure", error?.message.toString())
            }
        })
    }

    fun getUsers(): LiveData<GithubUserDetailResponse> {
        return githubUserDetail
    }
}