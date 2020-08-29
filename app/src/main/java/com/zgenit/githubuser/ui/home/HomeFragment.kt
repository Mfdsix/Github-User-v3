package com.zgenit.githubuser.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.zgenit.githubuser.MainActivity
import com.zgenit.githubuser.R
import com.zgenit.githubuser.adapter.GithubUserAdapter
import com.zgenit.githubuser.model.GithubUser
import com.zgenit.githubuser.ui.profile.ProfileActivity
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.include_alert.*
import kotlinx.android.synthetic.main.include_loading.*
import kotlinx.android.synthetic.main.include_toolbar.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: GithubUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(HomeViewModel::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)

        adapter = GithubUserAdapter()
        adapter.notifyDataSetChanged()
        adapter.setOnItemClickCallback(object : GithubUserAdapter.OnItemClickCallback{
            override fun onItemClicked(data: GithubUser) {
                showUserDetail(data)
            }
        })
        rv_github_users.layoutManager = LinearLayoutManager(context)
        rv_github_users.adapter = adapter

        tv_alert.setText(R.string.search_github_user)
        setViewVisibility(2)

        homeViewModel.getUsers().observe((context as MainActivity), {
            with(it) {
                when (status) {
                    200 -> {
                        adapter.setData(it.datas ?: ArrayList())
                        setViewVisibility(1)
                    }
                    404 -> {
                        tv_alert.setText(R.string.message_user_not_found)
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

    private fun searchGithubUser(s: String) {
        if(s != ""){
            setViewVisibility(0)
            homeViewModel.setUsers(s)
        }
    }

    private fun showUserDetail(githubUser: GithubUser) {
        val intent = Intent(context, ProfileActivity::class.java)
        intent.putExtra("user", githubUser)
        startActivity(intent)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)

        val searchView = SearchView((context as MainActivity))
        menu.findItem(R.id.nav_search).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
            actionView = searchView
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchGithubUser(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setViewVisibility(state: Int){
        when(state){
            1 -> {
                // showing recyclerview
                wrap_loading.visibility = View.GONE
                rv_github_users.visibility = View.VISIBLE
                wrap_alert.visibility = View.GONE
            }
            2 -> {
                // showing notification view
                wrap_loading.visibility = View.GONE
                rv_github_users.visibility = View.GONE
                wrap_alert.visibility = View.VISIBLE
            }
            else -> {
                // showing loading
                wrap_loading.visibility = View.VISIBLE
                rv_github_users.visibility = View.GONE
                wrap_alert.visibility = View.GONE
            }
        }
    }
}