package com.eye.eye.ui.common.callback

import android.app.AlertDialog
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eye.eye.extension.dp2px
import com.eye.eye.extension.screenHeight
import com.eye.eye.extension.showToas
import com.eye.eye.util.GlobalUtil
import com.shuyu.gsyvideoplayer.R
import com.shuyu.gsyvideoplayer.utils.CommonUtil
import com.shuyu.gsyvideoplayer.utils.NetworkUtils
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import java.util.logging.Handler

/*
社区关注界面的
* RecyclerView列表滚动的自动播放监听
@param itemPlayId 播放器的布局id
@param rangeTop 范围高度
@param rangBottom 范围的底部
*/
class AutoPlayScrollListener(private val itemPlayId:Int,private val rangeTop:Int,private val rangeBottom:Int):RecyclerView.OnScrollListener() {

    private var isNeddShowWifiDialog = true

    private var firstVisible = 0
    private var lastVisible = 0
    private var visibleCount = 0
    private var runnable:PlayRunable ?= null

    override fun onScrollStateChanged(recyclerView: RecyclerView, scrollState: Int) {
        when(scrollState){
            RecyclerView.SCROLL_STATE_IDLE -> playVideo(recyclerView)
        }
    }

    private fun playVideo(recyclerView: RecyclerView) {
        recyclerView?.run {
            val layoutManager = recyclerView.layoutManager
            var gsyBaseVideoPlayer:GSYBaseVideoPlayer ?= null
            var needPlay = false
            for (i in 0 until visibleCount) {
                if (layoutManager!!.getChildAt(i) != null && layoutManager.getChildAt(i)!!.findViewById<View?>(itemPlayId) != null){
                    val player = layoutManager.getChildAt(i)!!.findViewById<View>(itemPlayId) as GSYBaseVideoPlayer
                    val rect = Rect()
                    player.getLocalVisibleRect(rect)
                    val height = player.height
                    //说明第一个完全可视
                    if (rect.top == 0 && rect.bottom == height) {
                        gsyBaseVideoPlayer = player
                        if (player.currentPlayer.currentState == GSYVideoPlayer.CURRENT_STATE_NORMAL || player.currentPlayer.currentState == GSYBaseVideoPlayer.CURRENT_STATE_ERROR) {
                            needPlay = true
                        }
                        break
                    }
                }
            }
            if (gsyBaseVideoPlayer != null && needPlay) {
                runnable?.let {
                    val tmpPlayer = it.gsyBaseVideoPlayer
                    playHandler.removeCallbacks(it)
                    runnable = null
                    if (tmpPlayer === gsyBaseVideoPlayer) return
                }
                runnable = PlayRunable(gsyBaseVideoPlayer)
                //降低频率
                playHandler.postDelayed(runnable,400)
            }
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val lastVisbleItem = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

        if (firstVisible == firstVisibleItem) return
        firstVisible = firstVisibleItem
        lastVisible = lastVisbleItem
        visibleCount = lastVisbleItem - firstVisibleItem
    }

    private inner class PlayRunable(var gsyBaseVideoPlayer: GSYBaseVideoPlayer?):Runnable {
        override fun run() {
            var inPosistion = false
            //如果未播放，需要播放
            if (gsyBaseVideoPlayer != null) {
                val screenPosition = IntArray(2)
                gsyBaseVideoPlayer!!.getLocationOnScreen(screenPosition)
                val halfHeight = gsyBaseVideoPlayer!!.height / 2
                val rangePostion = screenPosition[1] + halfHeight   //中心点的位置 屏幕高度的一半
                //中心点在播放区域内
                if (rangePostion >= rangeTop && rangePostion <= rangeBottom){
                    inPosistion = true
                }
                if (inPosistion) {
                    startPlayLogic(gsyBaseVideoPlayer!!,gsyBaseVideoPlayer!!.context)
                }
            }
        }
    }

    private fun startPlayLogic(gsyBaseVideoPlayer: GSYBaseVideoPlayer, context: Context) {
        if (!CommonUtil.isWifiConnected(context) && isNeddShowWifiDialog) {
            showWifiDialog(gsyBaseVideoPlayer, context)
            return
        }
        gsyBaseVideoPlayer.startPlayLogic()
    }

    private fun showWifiDialog(gsyBaseVideoPlayer: GSYBaseVideoPlayer, context: Context) {
        if (!NetworkUtils.isAvailable(context)) {
            GlobalUtil.getString(R.string.no_net).showToas()
            return
        }
        AlertDialog.Builder(context).apply {
            setMessage(GlobalUtil.getString(R.string.tips_not_wifi))
            setPositiveButton(GlobalUtil.getString(R.string.tips_not_wifi_confirm)){dialog,which ->
                dialog.dismiss()
                gsyBaseVideoPlayer.startPlayLogic()
                isNeddShowWifiDialog = false
            }
            setPositiveButton(context.resources.getString(R.string.tips_not_wifi_confirm)) { dialog, which ->
                dialog.dismiss()
                gsyBaseVideoPlayer.startPlayLogic()
                isNeddShowWifiDialog = false
            }
            setNegativeButton(context.resources.getString(R.string.tips_not_wifi_cancel)) { dialog, which ->
                dialog.dismiss()
                isNeddShowWifiDialog = true
            }
            create()
        }.show()
    }

    private val playHandler = android.os.Handler()


    companion object {
        /*
        * 指定自动播放，在屏幕上的区域范围，上
        * */
        val PLAY_RANGE_TOP = screenHeight / 2 - dp2px(180f)
        /*
        * 指定自动播放，在屏幕上的区域范围，下
        * */
        val PLAY_RANGE_BOTTOM = screenHeight / 2 + dp2px(180f)
    }
}