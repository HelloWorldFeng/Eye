package com.eye.eye.ui.common.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.eye.eye.R
import com.eye.eye.extension.setOnClickListenter
import com.eye.eye.extension.showToast
import com.eye.eye.ui.search.SearchFragment
import com.flyco.tablayout.CommonTabLayout
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import kotlinx.android.synthetic.main.fragment_main_container.*
import kotlinx.android.synthetic.main.layout_main_page_title_bar.*

/*
* Fragment基类，适用场景：页面含有ViewPager + TabLayout的界面
* */
abstract class BaseViewPagerFragment:BaseFragment() {

    protected var viewPage:ViewPager2 ?= null

    protected var tabLayout:CommonTabLayout ?= null

    protected var pageChangeCallback:PageChangCallback ?= null

    protected val adapter:Vpadapter by lazy { Vpadapter(getActivity()!!).apply { addFragments(createFragments) } }

    protected var offscreenPageLimit = 1

    abstract val createTitles: ArrayList<CustomTabEntity>

    abstract val createFragments:Array<Fragment>

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        pageChangeCallback?.run { viewPager?.unregisterOnPageChangeCallback(this) }
        pageChangeCallback = null
    }

    open fun setupViews() {
        initViewPager()
        setOnClickListenter(ivCalendar,ivSearch){
            if (ivCalendar == this) {
                R.string.currently_not_supported.showToast()
            }else if (ivSearch == this) {
                SearchFragment.switchFragment(activity)
            }
        }
    }

    protected fun initViewPager() {
        viewPage = rootView?.findViewById(R.id.viewPager)
        tabLayout = rootView?.findViewById(R.id.tabLayout)

        viewPage?.offscreenPageLimit = offscreenPageLimit
        viewPage?.adapter = adapter
        tabLayout?.setTabData(createTitles)
        tabLayout?.setOnTabSelectListener(object :OnTabSelectListener{
            //点击tabLayout时调用，改变viewPager的currentItem
            override fun onTabSelect(position: Int) {
                viewPage?.currentItem = position
            }

            override fun onTabReselect(position: Int) {
            }
        })
        pageChangeCallback = PageChangCallback()
        viewPage?.registerOnPageChangeCallback(pageChangeCallback!!)

    }

    inner class PageChangCallback : ViewPager2.OnPageChangeCallback() {
        //ViewPage改变时调用,改变tabLayout
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            tabLayout?.currentTab = position
        }
    }

    /*
    * viewPager的适配器*/
    inner class Vpadapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

        private val fragments = mutableListOf<Fragment>()

        //向viewPager里添加所有Fragments
        fun addFragments(fragment: Array<Fragment>) {
            fragments.addAll(fragment)
        }

        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment = fragments[position]
    }
}