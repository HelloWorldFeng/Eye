package com.eye.eye.ui.community.follow

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eye.eye.BuildConfig
import com.eye.eye.Const
import com.eye.eye.R
import com.eye.eye.extension.*
import com.eye.eye.logic.model.Follow
import com.eye.eye.ui.common.holder.EmptyViewHolder
import com.eye.eye.ui.community.commend.CommendAdapter
import com.eye.eye.ui.login.LoginActivity
import com.eye.eye.ui.newdetail.NewDetailActivity
import com.eye.eye.ui.search.HotSearchAdapter
import com.eye.eye.util.DateUtil
import com.eye.eye.util.GlobalUtil
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import kotlinx.android.synthetic.main.item_community_auto_play_follow_card_follow_card_type.view.*

class FollowAdapter(private val fragment: FollowFragment,private val dataList:List<Follow.Item>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> Const.ItemViewType.CUSTOM_HEADER
            dataList[position - 1].type == "autoPlayFollowCard" && dataList[position - 1 ].data.dataType == "FollowCard"-> AUTO_PLAY_FOLLOW_CARD
            else -> Const.ItemViewType.UNKNOWN
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        Const.ItemViewType.CUSTOM_HEADER -> HeaderViewHolder(R.layout.item_community_follow_header_type.inflate(parent))
        AUTO_PLAY_FOLLOW_CARD -> AutoPlayFollwCardViewHolder(R.layout.item_community_auto_play_follow_card_follow_card_type.inflate(parent))
        else -> EmptyViewHolder(View(parent.context))

    }

    override fun getItemCount() = dataList.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.itemView.setOnClickListener { LoginActivity.start(fragment.activity) }

            is AutoPlayFollwCardViewHolder ->{
                val item = dataList[position - 1]
                item.data.content.data.run {
                    holder.ivAvatar.load(item.data.header.icon ?: author?.icon?:"")

                    holder.tvReleaseTime.text = DateUtil.getDate(releaseTime?:author?.latestReleaseTime?: System.currentTimeMillis(),"HH:mm")
                    holder.tvTitle.text = title
                    holder.tvNickname.text = author?.name?:""
                    holder.tvContent.text = description
                    holder.tvCollectionCount.text = consumption.collectionCount.toString()
                    holder.tvReplyCount.text = consumption.replyCount.toString()
                    holder.tvVideoDuration.visible()        //视频播放后，复用tvVideoDuration直接隐藏了
                    holder.tvVideoDuration.text = duration.coversionVideoDuration()
                    com.eye.eye.ui.home.commed.CommendAdapter.startAutoPlay(fragment.activity,holder.videoPlayer,position,playUrl,cover.feed,TAG,object:
                        GSYSampleCallBack() {
                        override fun onPrepared(url: String?, vararg objects: Any?) {
                            super.onPrepared(url, *objects)
                            GSYVideoManager.instance().isNeedMute = true
                        }

                        override fun onClickResume(url: String?, vararg objects: Any?) {
                            super.onClickResume(url, *objects)
                            holder.tvVideoDuration.gone()
                        }

                        override fun onClickBlank(url: String?, vararg objects: Any?) {
                            super.onClickBlank(url, *objects)
                            holder.tvVideoDuration.visible()
                            NewDetailActivity.start(fragment.activity,NewDetailActivity.VideoInfo(id,playUrl,title,description,category,library,consumption,cover,author!!,webUrl))
                        }
                    })
                    holder.let {
                        setOnClickListenter(it.videoPlayer.thumbImageView,it.itemView,it.ivCollectionCount,it.tvCollectionCount,it.ivFavorites,it.ivShare){
                            when(this){
                                it.videoPlayer.thumbImageView,it.itemView ->{
                                    NewDetailActivity.start(
                                        fragment.activity,
                                        NewDetailActivity.VideoInfo(
                                            item.data.content.data.id,
                                            playUrl,
                                            title,
                                            description,
                                            category,
                                            library,
                                            consumption,
                                            cover,
                                            author,
                                            webUrl
                                        )
                                    )
                                }
                                it.ivCollectionCount,it.tvCollectionCount,it.ivFavorites,it.tvFavorites ->{LoginActivity.start(fragment.activity)}
                                it.ivShare ->{
                                    showDialogShare(fragment.activity,getShareContent(item))}
                            }
                        }
                    }
                }
            }
            else ->{
                holder.itemView.gone()
                if (BuildConfig.DEBUG) "${TAG}:${Const.Toast.BIND_VIEWHOLDER_TYPE_WARN}\n${holder}".showToas()
            }
        }
    }

    private fun getShareContent(item: Follow.Item): String {
        item.data.content.data.run {
            val linkUrl = "${item.data.content.data.webUrl.raw}&utm_campaign=routine&utm_medium=share&utm_source=others&uid=0&resourceType=${resourceType}"
            return "${title}|${GlobalUtil.appName}：\n${linkUrl}"
        }
    }

    //视频的Item的ViewHolder
    class AutoPlayFollwCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAvatar = view.findViewById<ImageView>(R.id.ivAvatar)
        val tvReleaseTime = view.findViewById<TextView>(R.id.tvReleaseTime)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvNickname = view.findViewById<TextView>(R.id.tvNickName)
        val tvContent = view.findViewById<TextView>(R.id.tvContent)
        val ivCollectionCount = view.findViewById<ImageView>(R.id.ivCollectionCount)
        val tvCollectionCount = view.findViewById<TextView>(R.id.tvCollectionCount)
        val ivReply = view.findViewById<ImageView>(R.id.ivReply)
        val tvReplyCount = view.findViewById<TextView>(R.id.tvReplyCount)
        val ivFavorites = view.findViewById<ImageView>(R.id.ivFavorites)
        val tvFavorites = view.findViewById<TextView>(R.id.tvFavorites)
        val tvVideoDuration = view.findViewById<TextView>(R.id.tvVideoDuration)
        val ivShare = view.findViewById<ImageView>(R.id.ivShare)
        val videoPlayer: GSYVideoPlayer = view.findViewById<GSYVideoPlayer>(R.id.videoPlayer)
    }
    //头部的ViewHolder
    private inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAvatar = view.findViewById<ImageView>(R.id.ivAvatar)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
    }

    companion object {
        const val TAG = "FollowAdapter"
        const val AUTO_PLAY_FOLLOW_CARD = Const.ItemViewType.MAX
    }
}


