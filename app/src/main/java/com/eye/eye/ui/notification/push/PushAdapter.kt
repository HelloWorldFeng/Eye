package com.eye.eye.ui.notification.push

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eye.eye.R
import com.eye.eye.extension.inflate
import com.eye.eye.extension.load
import com.eye.eye.logic.model.PushMessage
import com.eye.eye.util.ActionUrlUtil
import com.eye.eye.util.DateUtil

class PushAdapter(private val fragment: PushFragment,private var dataList:List<PushMessage.Message>): RecyclerView.Adapter<PushAdapter.ViewHoler>() {

    inner class ViewHoler(view:View): RecyclerView.ViewHolder(view) {
        val tvTitle:TextView = view.findViewById(R.id.tvTitle)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val ivAvatar: ImageView = view.findViewById(R.id.ivAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHoler {
        val holder = ViewHoler(R.layout.item_notification_push.inflate(parent))
        holder.itemView.setOnClickListener {
            val item = dataList[holder.bindingAdapterPosition]
            ActionUrlUtil.process(fragment, item.actionUrl, item.title)
        }
        return holder
    }

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: ViewHoler, position: Int) {
        dataList[position].run {
            holder.ivAvatar.load(icon){ error(R.mipmap.ic_launcher)}
            holder.tvTitle.text = title
            holder.tvTime.text = DateUtil.getConvertedDate(date)
            holder.tvContent.text = content
        }
    }
}