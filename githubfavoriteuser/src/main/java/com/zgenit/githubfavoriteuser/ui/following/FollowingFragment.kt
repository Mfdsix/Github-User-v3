package com.zgenit.githubfavoriteuser.ui.following

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
import kotlinx.android.synthetic.main.fragment_following.*
import kotlinx.android.synthetic.main.include_alert.*
import kotlinx.android.synthetic.main.include_loading.*

class FollowingFragment(private val username: String) : Fragment() {

    private lateinit var adapter: GithubUserAdapter

    companion object {
        fun newInstance(username: String) = FollowingFragment(username)
    }

    private lateinit var viewModel: FollowingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_following, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setViewVisibility(0)
        adapter = GithubUserAdapter()
        adapter.notifyDataSetChanged()
        adapter.setOnItemClickCallback(object : GithubUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: GithubUser) {
                showUserProfile(data)
            }
        })
        rv_following.layoutManager = LinearLayoutManager(context)
        rv_following.adapter = adapter

        viewModel = ViewModelProvider(
            (context as ProfileActivity),
            ViewModelProvider.NewInstanceFactory()
        ).get(FollowingViewModel::class.java)
        viewModel.setFollowing(username)
        viewModel.getFollowing().observe((context as ProfileActivity), {
            with(it) {
                when (status) {
                    200 -> {
                        adapter.setData(it.datas ?: ArrayList())
                        setViewVisibility(1)
                    }
                    404 -> {
                        tv_alert.setText(R.string.have_no_following)
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

    private fun setViewVisibility(state: Int) {
        when (state) {
            1 -> {
                // showing recyclerview
                wrap_loading.visibility = View.GONE
                rv_following.visibility = View.VISIBLE
                wrap_alert.visibility = View.GONE
            }
            2 -> {
                // showing notification view
                wrap_loading.visibility = View.GONE
                rv_following.visibility = View.GONE
                wrap_alert.visibility = View.VISIBLE
            }
            else -> {
                // showing loading
                wrap_loading.visibility = View.VISIBLE
                rv_following.visibility = View.GONE
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