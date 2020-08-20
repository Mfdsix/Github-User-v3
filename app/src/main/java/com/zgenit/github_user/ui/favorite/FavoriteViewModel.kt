package com.zgenit.github_user.ui.favorite

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.zgenit.github_user.db.FavoriteUserHelper
import com.zgenit.github_user.helper.MappingHelper
import com.zgenit.github_user.model.GithubUser
import com.zgenit.github_user.model.GithubUserResponse
import com.zgenit.github_user.ui.home.HomeViewModel
import cz.msebera.android.httpclient.Header
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject

class FavoriteViewModel : ViewModel() {

    private val githubUserResponse = MutableLiveData<GithubUserResponse>()

    fun setUsers(database: FavoriteUserHelper) {
        var userResponse: GithubUserResponse
        var listItems: ArrayList<GithubUser>

        GlobalScope.launch(Dispatchers.Main) {
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = database.getAll()
                MappingHelper.parseToArrayList(cursor)
            }
            listItems = deferredNotes.await()

            userResponse = if(listItems.size > 0){
                GithubUserResponse(
                    200,
                    "success",
                    listItems
                )
            }else{
                GithubUserResponse(
                    404,
                    "No Data Found",
                    listItems
                )
            }

            githubUserResponse.postValue(userResponse)
        }
    }

    fun getUsers(): LiveData<GithubUserResponse> = githubUserResponse
}