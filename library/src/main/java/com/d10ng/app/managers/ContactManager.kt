package com.d10ng.app.managers

import android.app.Activity
import android.app.Application
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 联系人管理器
 * @Author d10ng
 * @Date 2023/11/10 10:30
 */
object ContactManager {

    private lateinit var application: Application

    private val scope = CoroutineScope(Dispatchers.IO)

    // 最顶部展示的Activity
    private var topActivity: ComponentActivity? = null

    // 权限申请执行器
    private val launcherMap =
        mutableMapOf<ComponentActivity, ActivityResultLauncher<Void?>>()

    // 权限申请结果Flow
    private val resultFlow = MutableSharedFlow<Uri?>()

    private class PickPhoneContact : ActivityResultContract<Void?, Uri?>() {
        override fun createIntent(context: Context, input: Void?): Intent {
            return Intent(Intent.ACTION_PICK).setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return intent.takeIf { resultCode == Activity.RESULT_OK }?.data
        }
    }

    internal fun init(app: Application) {
        application = app.apply {
            registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
                override fun onActivityCreated(p0: Activity, p1: Bundle?) {
                    if (p0 !is ComponentActivity) return
                    launcherMap[p0] =
                        p0.registerForActivityResult(PickPhoneContact()) { data ->
                            scope.launch { resultFlow.emit(data) }
                        }
                    topActivity = p0
                }

                override fun onActivityStarted(p0: Activity) {}

                override fun onActivityResumed(p0: Activity) {
                    if (p0 !is ComponentActivity) return
                    topActivity = p0
                }

                override fun onActivityPaused(p0: Activity) {}

                override fun onActivityStopped(p0: Activity) {}

                override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

                override fun onActivityDestroyed(p0: Activity) {
                    if (p0 !is ComponentActivity) return
                    launcherMap.remove(p0)
                    if (topActivity == p0) topActivity = null
                }
            })
        }
    }

    /**
     * 选择联系人
     * @return ContactPhone?
     */
    suspend fun pick(): ContactPhone? = withContext(Dispatchers.IO) {
        val launcher = launcherMap[topActivity] ?: return@withContext null
        launcher.launch(null)
        val uri = resultFlow.first() ?: return@withContext null
        val projection: Array<String> = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        var contactPhone: ContactPhone? = null
        application.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                contactPhone =
                    ContactPhone(cursor.getString(0), cursor.getString(1).replace(" ", ""))
            }
        }
        contactPhone
    }

    /**
     * 添加联系人
     * @param contactPhone ContactPhone
     */
    fun add(contactPhone: ContactPhone) {
        try {
            val contentValues = ContentValues()
            val uri = application.contentResolver.insert(
                ContactsContract.RawContacts.CONTENT_URI,
                contentValues
            ) ?: return
            val rawId = ContentUris.parseId(uri)
            // 插入名字
            contentValues.clear()
            contentValues.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawId)
            contentValues.put(
                ContactsContract.Contacts.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
            )
            contentValues.put(
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                contactPhone.name
            )
            application.contentResolver.insert(ContactsContract.Data.CONTENT_URI, contentValues)
            // 插入电话
            contentValues.clear()
            contentValues.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawId)
            contentValues.put(
                ContactsContract.Contacts.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
            )
            contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, contactPhone.number)
            contentValues.put(
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
            )
            application.contentResolver.insert(ContactsContract.Data.CONTENT_URI, contentValues)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * 联系人电话
 * @property name String 联系人姓名
 * @property number String 联系人电话号码
 * @constructor
 */
data class ContactPhone(
    val name: String,
    val number: String
)