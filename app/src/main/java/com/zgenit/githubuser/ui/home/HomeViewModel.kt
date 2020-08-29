package com.zgenit.githubuser.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.zgenit.githubuser.BuildConfig
import com.zgenit.githubuser.model.GithubUser
import com.zgenit.githubuser.model.GithubUserResponse
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class HomeViewModel : ViewModel() {

    companion object {
        const val GITHUB_API_URL = "https://api.github.com/search/users?q="
    }

    val githubUserResponse = MutableLiveData<GithubUserResponse>()

    fun setUsers(query: String) {
        var userResponse: GithubUserResponse
        val listItems = ArrayList<GithubUser>()
        val endpoint = "${GITHUB_API_URL}${query}"

        val client = AsyncHttpClient()
        client.addHeader("Accept", "application/vnd.github.v3+json")
        client.addHeader("Authorization", "token ${BuildConfig.GITHUB_API_KEY}")
        client.addHeader("User-Agent", "request")
        client.get(endpoint, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                try {
                    val result = String(responseBody)
                    val responseObject = JSONObject(result)
                    val list = responseObject.getJSONArray("items")

                    for (i in 0 until list.length()) {
                        val user = list.getJSONObject(i)
                        val userItem = GithubUser(
                            user.getInt("id"),
                            user.getString("node_id"),
                            user.getString("login"),
                            user.getString("avatar_url")
                        )

                        listItems.add(userItem)
                    }

                    userResponse = if(listItems.size > 0){
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
                    githubUserResponse.postValue(userResponse)

                } catch (e: Exception) {
                    Log.d("Exception", e.message.toString())

                    userResponse = GithubUserResponse(
                        400,
                        e.message.toString(),
                        listItems
                    )
                    githubUserResponse.postValue(userResponse)
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                userResponse = GithubUserResponse(
                    400,
                    error?.message.toString(),
                    listItems
                )
                githubUserResponse.postValue(userResponse)

                Log.d("onFailure", error?.message.toString())
            }
        })
    }

    fun getUsers(): LiveData<GithubUserResponse> = githubUserResponse
}
