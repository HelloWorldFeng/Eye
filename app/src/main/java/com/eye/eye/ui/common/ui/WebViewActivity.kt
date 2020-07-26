package com.eye.eye.ui.common.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.*
import com.eye.eye.R
import com.eye.eye.extension.logD
import com.eye.eye.extension.preCreateSession
import com.eye.eye.extension.visible
import com.eye.eye.util.GlobalUtil
import com.eyepetizer.android.ui.common.ui.vassonic.OfflinePkgSessionConnection
import com.eyepetizer.android.ui.common.ui.vassonic.SonicJavaScriptInterface
import com.eyepetizer.android.ui.common.ui.vassonic.SonicRuntimeImpl
import com.eyepetizer.android.ui.common.ui.vassonic.SonicSessionClientImpl
import com.tencent.sonic.sdk.*
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.item_auto_play_video_ad.tvTitle
import kotlinx.android.synthetic.main.layout_title_bar.*

/*
*展示网页通用界面
*/
class WebViewActivity : BaseActivity() {

    private var title: String = ""

    private var linkUrl: String = ""

    private var isShare:Boolean = false

    private var isTitleFixed:Boolean = false

    private var sonicSession: SonicSession?= null

    private var sonicSessionClient: SonicSessionClientImpl?= null

    private var mode:Int = MODE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initParams()
        preloadInitVasSonic()
        setContentView(R.layout.activity_web_view)
    }

    override fun setupViews() {
        super.setupViews()
        initTitleBar()
        initWebView()
        if (sonicSessionClient != null) {
            sonicSessionClient?.bindWebView(webView)
            sonicSessionClient?.clientReady()
        } else {
            webView.loadUrl(linkUrl)
        }
    }

    private fun initWebView() {
        webView.settings.run {
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            javaScriptEnabled = true
            webView.removeJavascriptInterface("searchBoxJavaBridge_")
            intent.putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis())
            webView.addJavascriptInterface(SonicJavaScriptInterface(sonicSessionClient, intent), "sonic")
            allowContentAccess = true
            databaseEnabled = true
            domStorageEnabled = true
            setAppCacheEnabled(true)
            savePassword = false
            saveFormData = false
            useWideViewPort = true
            loadWithOverviewMode = true
            defaultTextEncodingName = "UTF-8"
            setSupportZoom(true)
        }
        webView.webChromeClient = UIWebChromeClient()
        webView.webViewClient = UIWebViewClient()
        webView.setDownloadListener { url, _, _, _, _ ->
            // 调用系统浏览器下载
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }


    private fun initTitleBar() {
        tvTitle.text = title
        if (isShare) ivShare.visible()
        ivShare.setOnClickListener { showDialogShare("${title}:${linkUrl}") }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            finish()
        }
    }

    override fun onDestroy() {
        webView.destroy()
        sonicSession?.destroy()
        sonicSession = null
        super.onDestroy()
    }

    /**
     * 使用VasSonic框架提升H5首屏加载速度。
     */
    private fun preloadInitVasSonic() {
        window.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)

        // init sonic engine if necessary, or maybe u can do this when application created
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(SonicRuntimeImpl(application), SonicConfig.Builder().build())
        }

        // if it's sonic mode , startup sonic session at first time
        if (MODE_DEFAULT != mode) { // sonic mode
            val sessionConfigBuilder = SonicSessionConfig.Builder()
            sessionConfigBuilder.setSupportLocalServer(true)

            // if it's offline pkg mode, we need to intercept the session connection
            if (MODE_SONIC_WITH_OFFLINE_CACHE == mode) {
                sessionConfigBuilder.setCacheInterceptor(object : SonicCacheInterceptor(null) {
                    override fun getCacheData(session: SonicSession): String? {
                        return null // offline pkg does not need cache
                    }
                })
                sessionConfigBuilder.setConnectionInterceptor(object : SonicSessionConnectionInterceptor() {
                    override fun getConnection(session: SonicSession, intent: Intent): SonicSessionConnection {
                        return OfflinePkgSessionConnection(this@WebViewActivity, session, intent)
                    }
                })
            }

            // create sonic session and run sonic flow
            sonicSession = SonicEngine.getInstance().createSession(linkUrl, sessionConfigBuilder.build())
            if (null != sonicSession) {
                sonicSession?.bindClient(SonicSessionClientImpl().also { sonicSessionClient = it })
            } else {
                // this only happen when a same sonic session is already running,
                // u can comment following codes to feedback as a default mode.
                // throw new UnknownError("create session fail!");
                logD(TAG, "${title},${linkUrl}:create sonic session fail!")
            }
        }
    }

    private fun initParams() {
        title = intent.getStringExtra(TITLE) ?: GlobalUtil.appName
        linkUrl = intent.getStringExtra(LINK_URL) ?: DEFAULT_URL
        isShare = intent.getBooleanExtra(IS_SHARE, false)
        isTitleFixed = intent.getBooleanExtra(IS_TITLE_FIXED, false)
        mode = intent.getIntExtra(PARAM_MODE, MODE_DEFAULT)
    }

    inner class UIWebChromeClient : WebChromeClient() {
        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            logD(TAG, "onReceivedTitle >>> title:${title}")
            if (!isTitleFixed) {
                title?.run {
                    this@WebViewActivity.title = this
                    tvTitle.text = this
                }
            }
        }
    }

    inner class UIWebViewClient : WebViewClient() {
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            logD(TAG, "onPageStarted >>> url:${url}")
            linkUrl = url
            super.onPageStarted(view, url, favicon)
            progressBar.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView, url: String) {
            logD(TAG, "onPageFinished >>> url:${url}")
            super.onPageFinished(view, url)
            sonicSession?.sessionClient?.pageFinish(url)
            progressBar.visibility = View.INVISIBLE
        }

        override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
            if (sonicSession != null) {
                val requestResponse = sonicSessionClient?.requestResource(url)
                if (requestResponse is WebResourceResponse) return requestResponse
            }
            return null
        }
    }

    companion object {
        const val TAG = "WebViewActivity"

        private const val TITLE = "title"

        private const val LINK_URL = "link_url"

        private const val IS_SHARE = "is_share"

        private const val IS_TITLE_FIXED = "isTitleFixed"

        const val MODE_DEFAULT = 0

        const val MODE_SONIC = 1

        const val MODE_SONIC_WITH_OFFLINE_CACHE = 2

        const val PARAM_MODE = "param_mode"

        const val DEFAULT_URL = ""

        val DEFAULT_TITLE = GlobalUtil.appName

        fun start(context: Context,title:String,url:String,isShare:Boolean = true,isTitleFixed:Boolean = true,mode:Int = MODE_DEFAULT){
            url.preCreateSession() //预加载url
            val intent = Intent(context,WebViewActivity::class.java).apply {
                putExtra(TITLE, title)
                putExtra(LINK_URL, url)
                putExtra(IS_SHARE, isShare)
                putExtra(IS_TITLE_FIXED, isTitleFixed)
                putExtra(PARAM_MODE, mode)
                putExtra(SonicJavaScriptInterface.PARAM_CLICK_TIME, System.currentTimeMillis())
            }
            context.startActivity(intent)
        }
    }
}