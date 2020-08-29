package com.zgenit.githubfavoriteuser.ui.follower

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zgenit.githubfavoriteuser.ProfileActivity
import com.zgenit.githubfavoriteuser.R
import com.zgenit.githubfavoriteuser.adapter.GithubUserAdapter
import com.zgenit.githubfavoriteuser.model.GithubUser
import kotlinx.android.synthetic.main.fragment_follower.*
import kotlinx.android.synthetic.main.include_alert.*
import kotlinx.android.synthetic.main.include_loading.*

class FollowerFragment(private val username: String) : Fragment() {

    private lateinit var adapter: GithubUserAdapter

    companion object {
        fun newInstance(username: String) = FollowerFragment(username)
    }

    private lateinit var viewModel: FollowerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_follower, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setViewVisibility(0)
        adapter = GithubUserAdapter()
        adapter.notifyDataSetChanged()
        adapter.setOnItemClickCallback(object : GithubUserAdapter.OnItemClickCallback{
            override fun onItemClicked(data: GithubUser) {
                showUserProfile(data)
            }
        })
        rv_followers.layoutManager = LinearLayoutManager(context)
        rv_followers.adapter = adapter

        viewModel = ViewModelProvider((context as ProfileActivity), ViewModelProvider.NewInstanceFactory()).get(
            FollowerViewModel::class.java)
        viewModel.setFollowers(username)
        viewModel.getFollowers().observe((context as ProfileActivity), {
            with(it) {
                when (status) {
                    200 -> {
                        adapter.setData(it.datas ?: ArrayList())
                        setViewVisibility(1)
                    }
                    404 -> {
                        tv_alert.setText(R.string.have_no_follower)
                        setViewVisibility(2)
                    }
                    else -> {
                        tv_alert.text = it.message
                        setViewVisibility(2)
                    }
                }
            }
        })

    }

    private fun setViewVisibility(state: Int){
        when(state){
            1 -> {
                // showing recyclerview
                wrap_loading.visibility = View.GONE
                rv_followers.visibility = View.VISIBLE
                wrap_alert.visibility = View.GONE
            }
            2 -> {
                // showing notification view
                wrap_loading.visibility = View.GONE
                rv_followers.visibility = View.GONE
                wrap_alert.visibility = View.VISIBLE
            }
            else -> {
                // showing loading
                wrap_loading.visibility = View.VISIBLE
                rv_followers.visibility = View.GONE
                wrap_alert.visibility = View.GONE
            }
        }
    }

    private fun showUserProfile(data: GithubUser) {
        val intent = Intent(context, ProfileActivity::class.java)
        intent.putExtra("user", data)
        startActivity(intent)
    }

}