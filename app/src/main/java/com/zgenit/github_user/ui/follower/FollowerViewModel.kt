package com.zgenit.github_user.ui.follower

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.zgenit.github_user.model.GithubUser
import com.zgenit.github_user.model.GithubUserResponse
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

class FollowerViewModel : ViewModel() {
    companion object {
        const val GITHUB_API_URL = "https://api.github.com/users/"
    }
    val githubUserResponse = MutableLiveData<GithubUserResponse>()

    fun setFollowers(query: String, token: String) {
        var githubUser: GithubUserResponse
        val listItems = ArrayList<GithubUser>()
        val url = "${GITHUB_API_URL}${query}/followers"

        val client = AsyncHttpClient()
        client.addHeader("Accept", "application/vnd.github.v3+json")
        client.addHeader("Authorization", "token $token")
        client.addHeader("User-Agent", "request")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                try {
                    val result = String(responseBody)
                    val responseObject = JSONArray(result)

                    for (i in 0 until responseObject.length()) {
                        val user = responseObject.getJSONObject(i)
                        val userItem = GithubUser(
                            user.getInt("id"),
                            user.getString("node_id"),
                            user.getString("login"),
                            user.getString("avatar_url")
                        )
                        listItems.add(userItem)
                    }

                    githubUser = if(listItems.size > 0){
                        GithubUserResponse(
                            200,
                            "success",
                            listItems
                        )
                    }else{
                        GithubUserResponse(
                            404,
                            "User Not Found",
                            listItems
                        )
                    }
                    githubUserResponse.postValue(githubUser)

                } catch (e: Exception) {
                    githubUser = GithubUserResponse(
                        400,
                        e.message.toString(),
                        listItems
                    )
                    githubUserResponse.postValue(githubUser)
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                githubUser = GithubUserResponse(
                    400,
                    error?.message.toString(),
                    ArrayList()
                )
                githubUserResponse.postValue(githubUser)
                Log.d("onFailure", error?.message.toString())
            }
        })
    }

    fun getFollowers() : LiveData<GithubUserResponse> = githubUserResponse
}