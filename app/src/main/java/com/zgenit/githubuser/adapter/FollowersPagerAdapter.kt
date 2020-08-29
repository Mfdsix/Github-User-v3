package com.zgenit.githubuser.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.zgenit.githubuser.R
import com.zgenit.githubuser.ui.follower.FollowerFragment
import com.zgenit.githubuser.ui.following.FollowingFragment

class FollowersPagerAdapter(private val context: Context, private val username: String, frameManager: FragmentManager): FragmentPagerAdapter(frameManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    companion object{
        private val TAB_TITLES = intArrayOf(R.string.tab_follower, R.string.tab_following)
    }

    override fun getItem(position: Int): Fragment {
        val fragment: Fragment? = when(position){
            0 -> FollowerFragment.newInstance(username)
            1 -> FollowingFragment.newInstance(username)
            else -> null
        }
        return fragment as Fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }

}