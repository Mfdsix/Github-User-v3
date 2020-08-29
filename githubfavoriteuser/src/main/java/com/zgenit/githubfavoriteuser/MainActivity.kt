package com.zgenit.githubfavoriteuser

import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zgenit.githubfavoriteuser.adapter.FavoriteUserAdapter
import com.zgenit.githubfavoriteuser.db.DatabaseContract.GithubUserColumns.Companion.CONTENT_URI
import com.zgenit.githubfavoriteuser.model.GithubUser
import com.zgenit.githubfavoriteuser.view_model.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_alert.*
import kotlinx.android.synthetic.main.include_loading.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: FavoriteUserAdapter
    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setTitle(R.string.title_favorite)

        context = this
        mainViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(MainViewModel::class.java)

        init()
    }

    private fun init(){
        setViewVisibility(0)
        adapter = FavoriteUserAdapter()
        adapter.notifyDataSetChanged()
        adapter.setOnItemClickCallback(object : FavoriteUserAdapter.OnItemClickCallback{
            override fun onItemClicked(data: GithubUser) {
                showUserDetail(data)
            }

            override fun onDeleteClicked(position: Int, data: GithubUser) {
                showDeletePrompt(position, data)
            }
        })
        rv_favorite_users.layoutManager = LinearLayoutManager(context)
        rv_favorite_users.adapter = adapter

        mainViewModel.setUsers(contentResolver)
        mainViewModel.getUsers().observe((context as MainActivity), {
            with(it) {
                when (status) {
                    200 -> {
                        adapter.setData(it.datas ?: ArrayList())
                        setViewVisibility(1)
                    }
                    404 -> {
                        tv_alert.setText(R.string.message_no_favorite_found)
                        setViewVisibility(2)
                    }
                    else -> {
                        tv_alert.text = it.message
                        setViewVisibility(2)
                    }
                }
            }
        })

        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        val myObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                mainViewModel.setUsers(contentResolver)
            }
        }
        contentResolver?.registerContentObserver(CONTENT_URI, true, myObserver)
    }

    private fun showUserDetail(githubUser: GithubUser) {
        val intent = Intent(context, ProfileActivity::class.java)
        intent.putExtra("user", githubUser)
        startActivity(intent)
    }

    private fun showDeletePrompt(position: Int, user: GithubUser){
        val alertDialogBuilder = AlertDialog.Builder((context as MainActivity))
        alertDialogBuilder.setTitle(R.string.confirmation_delete)
        alertDialogBuilder
            .setMessage(R.string.message_confirmation_delete)
            .setCancelable(false)
            .setPositiveButton(R.string.confirm_yes) { _, _ ->
                val uri = Uri.parse("$CONTENT_URI/${user.id}")
                val result = contentResolver.delete(uri, null, null)
                if(result > 0){
                    adapter.removeItem(position)
                    Toast.makeText(context, R.string.message_favorite_delete_success, Toast.LENGTH_SHORT).show()
                    mainViewModel.setUsers(contentResolver)
                }else{
                    Toast.makeText(context, R.string.message_something_wrong, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.confirm_no) { dialog, _ -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun setViewVisibility(state: Int){
        when(state){
            1 -> {
                // showing recyclerview
                wrap_loading.visibility = View.GONE
                rv_favorite_users.visibility = View.VISIBLE
                wrap_alert.visibility = View.GONE
            }
            2 -> {
                // showing notification view
                wrap_loading.visibility = View.GONE
                rv_favorite_users.visibility = View.GONE
                wrap_alert.visibility = View.VISIBLE
            }
            else -> {
                // showing loading
                wrap_loading.visibility = View.VISIBLE
                rv_favorite_users.visibility = View.GONE
                wrap_alert.visibility = View.GONE
            }
        }
    }
}
