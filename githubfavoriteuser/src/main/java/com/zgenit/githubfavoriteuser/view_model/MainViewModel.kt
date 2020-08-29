package com.zgenit.githubfavoriteuser.view_model

import android.content.ContentResolver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zgenit.githubfavoriteuser.db.DatabaseContract.GithubUserColumns.Companion.CONTENT_URI
import com.zgenit.githubfavoriteuser.helper.MappingHelper
import com.zgenit.githubfavoriteuser.model.GithubUser
import com.zgenit.githubfavoriteuser.model.GithubUserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val githubUserResponse = MutableLiveData<GithubUserResponse>()

    fun setUsers(contentResolver: ContentResolver) {
        var userResponse: GithubUserResponse
        var listItems: ArrayList<GithubUser>

        GlobalScope.launch(Dispatchers.Main) {
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = contentResolver.query(CONTENT_URI, null, null, null, null)
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