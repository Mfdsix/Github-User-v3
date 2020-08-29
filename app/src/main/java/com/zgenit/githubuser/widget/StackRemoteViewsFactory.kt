package com.zgenit.githubuser.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.zgenit.githubuser.R
import com.zgenit.githubuser.db.FavoriteUserHelper
import com.zgenit.githubuser.helper.MappingHelper
import com.zgenit.githubuser.model.GithubUser
import com.zgenit.githubuser.widget.FavoriteUserWidget.Companion.EXTRA_ITEM_ID
import com.zgenit.githubuser.widget.FavoriteUserWidget.Companion.EXTRA_ITEM_USERNAME


internal class StackRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory{

    private var mWidgetItems = ArrayList<GithubUser>()

    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        mWidgetItems = ArrayList()
        val cursor = FavoriteUserHelper(context).getAll()
        mWidgetItems.addAll(MappingHelper.parseToArrayList(cursor))
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(p0: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.item_favorite_user_widget)

        try {
            val bitmap: Bitmap = Glide.with(context)
                .asBitmap()
                .load(mWidgetItems[p0].avatar)
                .submit(512, 512)
                .get()
            remoteViews.setImageViewBitmap(R.id.img_avatar, bitmap)
        } catch (e: Exception) {
            remoteViews.setImageViewResource(R.id.img_avatar, R.drawable.example_appwidget_preview)
        }
        remoteViews.setTextViewText(R.id.tv_username, mWidgetItems[p0].username)
        remoteViews.setTextViewText(R.id.tv_user_id, mWidgetItems[p0].nodeId)

        val extras = bundleOf(
            EXTRA_ITEM_ID to mWidgetItems[p0].id,
            EXTRA_ITEM_USERNAME to mWidgetItems[0].username
        )
        val intent = Intent()
        intent.putExtras(extras)

        remoteViews.setOnClickFillInIntent(R.id.wrap_user, intent)
        return remoteViews
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(p0: Int): Long = 0

    override fun hasStableIds(): Boolean = false

}