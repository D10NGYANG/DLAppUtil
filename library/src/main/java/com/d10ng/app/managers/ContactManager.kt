package com.d10ng.app.managers

import android.app.Activity
import android.app.Application
import android.content.ContentProviderOperation
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import com.d10ng.app.startup.ctx
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

    data class Data(
        // 联系人姓名
        val name: String,
        // 联系人电话号码
        val number: String
    )

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
        app.apply {
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
     * @return Data?
     */
    suspend fun pick(): Data? = withContext(Dispatchers.IO) {
        val launcher = launcherMap[topActivity] ?: return@withContext null
        launcher.launch(null)
        val uri = resultFlow.first() ?: return@withContext null
        val projection: Array<String> = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        var data: Data? = null
        ctx.contentResolver.query(uri, projection, null, null, null)?.use { c ->
            if (c.moveToFirst()) {
                data = Data(c.getString(0), c.getString(1).replace(" ", ""))
            }
        }
        data
    }

    /**
     * 获取联系人列表
     * > 需要权限：android.permission.READ_CONTACTS
     * @return List<Data>
     */
    fun list(): List<Data> {
        val projection: Array<String> = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val list = mutableListOf<Data>()
        ctx.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { c ->
            while (c.moveToNext()) {
                list.add(Data(c.getString(0), c.getString(1).replace(" ", "")))
            }
        }
        return list.sortedBy { it.name }
    }

    /**
     * 添加联系人
     * > 需要权限：android.permission.WRITE_CONTACTS
     * @param data Data
     * @return Boolean
     */
    fun add(data: Data): Boolean {
        val ops = ArrayList<ContentProviderOperation>()

        ops.add(
            ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build()
        )

        // 名字
        ops.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                )
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, data.name)
                .build()
        )

        // 电话
        ops.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                )
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, data.number)
                .withValue(
                    ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                )
                .build()
        )

        return try {
            ctx.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 添加或更新联系人
     * > 需要权限：android.permission.WRITE_CONTACTS、android.permission.READ_CONTACTS
     * > 如果联系人已存在，则更新联系人信息
     * @param data Data
     * @return Boolean
     */
    fun addOrUpdate(data: Data): Boolean {
        val ops = ArrayList<ContentProviderOperation>()

        // 查询联系人是否存在
        val nameCursor = ctx.contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            arrayOf(ContactsContract.Data.RAW_CONTACT_ID),
            "${ContactsContract.Data.MIMETYPE} = ? AND ${ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME} = ?",
            arrayOf(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, data.name),
            null
        )

        var rawContactId: Long? = null

        if (nameCursor?.moveToFirst() == true) {
            // 获取联系人的ID
            rawContactId =
                nameCursor.getLong(nameCursor.run { getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID) })
        }
        nameCursor?.close()
        return if (rawContactId != null) {
            // 检查电话号码是否存在
            val phoneCursor = ctx.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ? AND ${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?",
                arrayOf(rawContactId.toString(), data.number),
                null
            )

            if (phoneCursor?.moveToNext() != true) {
                // 电话号码不存在，添加新的电话号码
                ops.add(
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        .withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                        )
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, data.number)
                        .withValue(
                            ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                        )
                        .build()
                )
            }
            phoneCursor?.close()
            try {
                ctx.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        } else add(data)
    }
}