package com.zgenit.github_user.ui.profile

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.zgenit.github_user.R
import com.zgenit.github_user.adapter.FollowersPagerAdapter
import com.zgenit.github_user.db.DatabaseContract.GithubUserColumns.Companion.AVATAR
import com.zgenit.github_user.db.DatabaseContract.GithubUserColumns.Companion.CONTENT_URI
import com.zgenit.github_user.db.DatabaseContract.GithubUserColumns.Companion.NODE_ID
import com.zgenit.github_user.db.DatabaseContract.GithubUserColumns.Companion.USERNAME
import com.zgenit.github_user.db.DatabaseContract.GithubUserColumns.Companion.USER_ID
import com.zgenit.github_user.model.GithubUser
import com.zgenit.github_user.model.GithubUserDetail
import com.zgenit.github_user.provider.FavoriteUserProvider
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.include_alert.*
import kotlinx.android.synthetic.main.include_loading.*

class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var profileViewModel: ProfileViewModel
    private var githubUser: GithubUser?= null
    private var githubUserDetail: GithubUserDetail ?= null
    private lateinit var favoriteUserProvider: FavoriteUserProvider
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.elevation = 0f
        supportActionBar?.setTitle(R.string.user_profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        favoriteUserProvider = FavoriteUserProvider()
        favoriteUserProvider.onCreate()

        setViewVisibility(0)
        val bundle: Bundle? = intent.extras
        if(bundle != null){
            githubUser = bundle.getParcelable("user")!!
        }

        // view model
        profileViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ProfileViewModel::class.java)
        profileViewModel.setUsers(githubUser?.username?: "", getString(R.string.github_token))
        profileViewModel.getUsers().observe(this, {
            with(it){
                when(status){
                    200 -> {
                        githubUserDetail = it.datas
                        setViewVisibility(1)
                        setUserProfile()
                    }
                    else -> {
                        tv_alert.text = it.message
                        setViewVisibility(2)
                    }
                }
            }
        })

        fab_favorite.setOnClickListener(this)
    }

    private fun setUserProfile(){
        Glide.with(this)
            .load(githubUserDetail!!.avatar)
            .placeholder(R.color.colorGrey)
            .into(img_user)

        txt_user_name.text = githubUserDetail?.username
        txt_user_fullname.text = githubUserDetail?.fullname

        txt_followers_amount.text = getString(R.string.user_followers_amount, githubUserDetail?.followers)
        txt_followings_amount.text = getString(R.string.user_following_amount, githubUserDetail?.following)
        txt_repositories_amount.text = getString(R.string.user_repository_amount, githubUserDetail?.publicRepos)

        txt_user_office.text = githubUserDetail?.office
        txt_user_location.text = githubUserDetail?.location

        setTabLayout()
        setFavoriteStatus()
    }

    private fun setTabLayout(){
        val sectionPagerAdapter = FollowersPagerAdapter(this, githubUserDetail?.username?: "-", supportFragmentManager)
        view_pager.adapter = sectionPagerAdapter
        tab_layout.setupWithViewPager(view_pager)
    }
    
    private fun setFavoriteStatus(){
        isFavorite = favoriteUserProvider.isExists(CONTENT_URI, githubUserDetail?.id?: 0)
        if(isFavorite) {
            fab_favorite.setImageResource(R.drawable.ic_favorite)
        }else{
            fab_favorite.setImageResource(R.drawable.ic_favorite_border)
        }
        fab_favorite.isEnabled = true
    }

    private fun toggleFavorite(){
        fab_favorite.isEnabled = false
        if(isFavorite) {
            val uriWithId = Uri.parse("$CONTENT_URI/${githubUserDetail?.id ?: 0}")
            favoriteUserProvider.delete(uriWithId, null, null)
            Toast.makeText(applicationContext, R.string.message_favorite_delete_success, Toast.LENGTH_SHORT).show()
        }else{
            val values = ContentValues()
            values.put(USER_ID, githubUserDetail?.id?: 0)
            values.put(USERNAME, githubUserDetail?.username?: "")
            values.put(NODE_ID, githubUserDetail?.nodeId?: "")
            values.put(AVATAR, githubUserDetail?.avatar?: "")

            favoriteUserProvider.insert(CONTENT_URI, values)
            Toast.makeText(applicationContext, R.string.message_favorite_add_success, Toast.LENGTH_SHORT).show()
        }
        setFavoriteStatus()
    }

    private fun setViewVisibility(state: Int){
        when(state){
            1 -> {
                // showing main content
                wrap_loading.visibility = View.GONE
                wrap_main.visibility = View.VISIBLE
                wrap_alert.visibility = View.GONE
            }
            2 -> {
                // showing notification view
                wrap_loading.visibility = View.GONE
                wrap_main.visibility = View.GONE
                wrap_alert.visibility = View.VISIBLE
            }
            else -> {
                // showing loading
                wrap_loading.visibility = View.VISIBLE
                wrap_main.visibility = View.GONE
                wrap_alert.visibility = View.GONE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.fab_favorite -> toggleFavorite()
        }
    }
}