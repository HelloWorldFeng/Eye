package com.eye.eye.util

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import com.eye.eye.EyeApplication
import com.eye.eye.extension.edit
import com.eye.eye.extension.logW
import com.eye.eye.extension.sharedPreferences
import java.util.*

object GlobalUtil {

    private var TAG = "GlobalUtil"
    /*
    * 获取App包名
    * */
    val appPackage: String
        get() = EyeApplication.context.packageName

    /*获取当前应用程序名称
    * */
    val appName: String
        get() = EyeApplication.context.resources.getString(EyeApplication.context.applicationInfo.labelRes)

    /*
    * 获取当前应用程序的版本名
    * */
    val appVersionName: String
        get() = EyeApplication.context.packageManager.getPackageInfo(appPackage, 0).versionName

    /*
    * 获取当前应用程序的版本号
    * */
    val appVersionCode: Long
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            EyeApplication.context.packageManager.getPackageInfo(appPackage, 0).longVersionCode
        } else {
            EyeApplication.context.packageManager.getPackageInfo(appPackage, 0).versionCode.toLong()
        }

    /*
    获取开眼应用的版本名
    @return 开眼当前应用的版本名
    */
    val eyeVersionName: String
        get() = "6.3.01"

    /*
    * 获取开眼应用的版本号
    * @return 版本号
    */
    val eyeVersionCode: Long
        get() = 6030012

    /**
     * 获取设备的的型号，如果无法获取到，则返回Unknown。
     * @return 设备型号。
     */
    val deviceModel: String
        get() {
            var deviceModel = Build.MODEL
            if (TextUtils.isEmpty(deviceModel)) {
                deviceModel = "unknown"
            }
            return deviceModel
        }

    /*
    * 获取设备的品牌，如果无法获取到，则返回unknown
    * @return 设备的品牌，全部转为小写*/
    val deviceBrand: String
        get() {
            var deviceBrand = Build.BRAND
            if (TextUtils.isEmpty(deviceBrand)) {
                deviceBrand = "unknown"
            }
            return deviceBrand.toLowerCase(Locale.getDefault())
        }

    /*
    * 获取设备的序列号。如果无法获取到设备的序列号，则会生成随机一个uuid来作为设备的序列号，uuid之后会存入缓存
    * 下次获取设备的序列号优先从缓存中读取
    * @return 设备的序列号*/
    private var deviceSerial: String? = null

    @SuppressLint("HardwareIds")
    fun getDeviceSerial(): String {
        if (deviceSerial == null) {
            var deviceId: String? = null
            val appChannel = getApplicationMetaData("APP_CHANNEL")
            if ("google" != appChannel || "samsung" != appChannel) {
                try {
                    deviceId = Settings.Secure.getString(EyeApplication.context.contentResolver, Settings.Secure.ANDROID_ID)
                } catch (e: Exception) {
                    logW(TAG, "get android_id with error", e)
                }
                if (!TextUtils.isEmpty(deviceId) && deviceId!!.length < 255) {
                    deviceSerial = deviceId
                    return deviceSerial.toString()
                }
            }
            var uuid = sharedPreferences.getString("uuid", "")
            if (!TextUtils.isEmpty(uuid)) {
                deviceSerial = uuid
                return deviceSerial.toString()
            }
            uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.getDefault())
            sharedPreferences.edit { putString("uuid", uuid) }
            deviceSerial = uuid
            return deviceSerial.toString()
        } else {
            return deviceSerial.toString()
        }
    }

    /*
    * 获取AndroidManifest.xml文件中,<application>标签的meta-data的值*/
    fun getApplicationMetaData(key: String): String? {
        var applicationInfo: ApplicationInfo? = null
        try {
            applicationInfo = EyeApplication.context.packageManager.getApplicationInfo(appPackage, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            logW(TAG, e.message, e)
        }
        if (applicationInfo == null) return ""
        return applicationInfo.metaData.getString(key)
    }

    /**
     * 获取资源文件中定义的字符串。
     *
     * @param resId
     * 字符串资源id
     * @return 字符串资源id对应的字符串内容。
     */
    fun getString(resId: Int): String {
        return EyeApplication.context.resources.getString(resId)
    }

    /**
     * 获取资源文件中定义的字符串。
     *
     * @param resId
     * 字符串资源id
     * @return 字符串资源id对应的字符串内容。
     */
    fun getDimension(resId: Int): Int {
        return EyeApplication.context.resources.getDimensionPixelOffset(resId)
    }

    /**
     * 获取指定资源名的资源id。
     *
     * @param name
     * 资源名
     * @param type
     * 资源类型
     * @return 指定资源名的资源id。
     */
    fun getResourceId(name: String, type: String): Int {
        return EyeApplication.context.resources.getIdentifier(name, type, appPackage)
    }

    /*判断某个应用是否安装
    * @param packageName
    * 要检查的是否安装的包名
    * @return 安装返回true 否者防护false*/
    fun isInstalled(packageName: String): Boolean {
        val packageInfo: PackageInfo? = try {
            EyeApplication.context.packageManager.getPackageInfo(packageName, 0)
        } catch (e: Exception) {
            null
        }

        return packageInfo != null
    }

    /*
    * 获取应用的图标*/
    fun getAppIcon(): Drawable {
        val packageManager = EyeApplication.context.packageManager
        val applicationInfo = packageManager.getApplicationInfo(appPackage, 0)
        return packageManager.getApplicationIcon(applicationInfo)
    }

    /*
    * 判断手机是否安装了QQ
    * */
    fun isQQInstalled() = isInstalled("com.tencent.mobileqq")

    /*
    * 判断手机是否安装了微信
    * */
    fun isWechatInstalled() = isInstalled("com.tencent.mm")
}