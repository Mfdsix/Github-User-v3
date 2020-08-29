package com.zgenit.githubuser.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zgenit.githubuser.R
import com.zgenit.githubuser.model.GithubUser
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_favorite_user.view.*
import kotlinx.android.synthetic.main.item_github_user.view.img_avatar
import kotlinx.android.synthetic.main.item_github_user.view.tv_user_id
import kotlinx.android.synthetic.main.item_github_user.view.tv_username
import kotlinx.android.synthetic.main.item_github_user.view.wrap_user

class FavoriteUserAdapter: RecyclerView.Adapter<FavoriteUserAdapter.ViewHolder>(){

    private val mData = ArrayList<GithubUser>()
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setData(items: ArrayList<GithubUser>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_user, parent, false)
        return ViewHolder(mView)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mData[position]

        holder.tvUsername.text = user.username
        holder.tvUserId.text = user.id.toString()
        Glide.with(holder.itemView.context)
            .load(user.avatar)
            .placeholder(R.color.colorGrey)
            .into(holder.imgAvatar)

        holder.wrapUser.setOnClickListener { onItemClickCallback?.onItemClicked(user) }
        holder.imgDelete.setOnClickListener { onItemClickCallback?.onDeleteClicked(position, user) }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val wrapUser: CardView = view.wrap_user
        val tvUsername: TextView = view.tv_username
        val imgAvatar: CircleImageView = view.img_avatar
        val tvUserId: TextView = view.tv_user_id
        val imgDelete: ImageView = view.img_delete
    }

    fun removeItem(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mData.size)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: GithubUser)
        fun onDeleteClicked(position: Int, data: GithubUser)
    }

}