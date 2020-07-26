package com.eye.eye.ui.ugcdetail

import androidx.lifecycle.ViewModel
import com.eye.eye.logic.model.CommunityRecommend

class UgcDetailViewModel : ViewModel() {

    var dataList: List<CommunityRecommend.Item>? = null

    var itemPosition:Int = -1
}