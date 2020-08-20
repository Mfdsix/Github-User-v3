package com.zgenit.github_user.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zgenit.github_user.R
import com.zgenit.github_user.model.GithubUser
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_github_user.view.*

class GithubUserAdapter: RecyclerView.Adapter<GithubUserAdapter.ViewHolder>(){

    private val mData = ArrayList<GithubUser>()
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setData(items: ArrayList<GithubUser>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.item_github_user, parent, false)
        return ViewHolder(mView)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mData[position]

        holder.tvUsername.text = user.username
        holder.tvUserId.text = user.id.toString()
        holder.tvUserNode.text = user.nodeId
        Glide.with(holder.itemView.context)
            .load(user.avatar)
            .placeholder(R.color.colorGrey)
            .into(holder.imgAvatar)

        holder.wrapUser.setOnClickListener { onItemClickCallback?.onItemClicked(user) }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val wrapUser: CardView = view.wrap_user
        val tvUsername: TextView = view.tv_username
        val imgAvatar: CircleImageView = view.img_avatar
        val tvUserId: TextView = view.tv_user_id
        val tvUserNode: TextView = view.tv_user_node
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: GithubUser)
    }

}