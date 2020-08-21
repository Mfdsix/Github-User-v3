package com.zgenit.github_user.ui.favorite

import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zgenit.github_user.MainActivity
import com.zgenit.github_user.R
import com.zgenit.github_user.adapter.FavoriteUserAdapter
import com.zgenit.github_user.db.DatabaseContract.GithubUserColumns.Companion.CONTENT_URI
import com.zgenit.github_user.db.FavoriteUserHelper
import com.zgenit.github_user.model.GithubUser
import com.zgenit.github_user.ui.profile.ProfileActivity
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.include_alert.*
import kotlinx.android.synthetic.main.include_loading.*
import kotlinx.android.synthetic.main.include_toolbar.*

class FavoriteFragment : Fragment() {

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteUserHelper: FavoriteUserHelper
    private lateinit var adapter: FavoriteUserAdapter
    private lateinit var ctx: Context

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        favoriteViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(FavoriteViewModel::class.java)
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        toolbar.setTitle(R.string.title_favorite)
        ctx = (context as MainActivity)

        favoriteUserHelper = FavoriteUserHelper.getInstance(ctx)
        favoriteUserHelper.open()

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

        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        val myObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                favoriteViewModel.setUsers(ctx.contentResolver)
            }
        }
        context?.contentResolver?.registerContentObserver(CONTENT_URI, true, myObserver)

        favoriteViewModel.setUsers((context as MainActivity).contentResolver)
        favoriteViewModel.getUsers().observe((context as MainActivity), Observer {
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

        super.onViewCreated(view, savedInstanceState)
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
                val result = favoriteUserHelper.delete(user.id)
                if(result > 0){
                    adapter.removeItem(position)
                    Toast.makeText(context, R.string.message_favorite_delete_success, Toast.LENGTH_SHORT).show()
                    favoriteViewModel.setUsers((context as MainActivity).contentResolver)
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