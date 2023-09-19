package com.d10ng.app.utils

import android.content.ContentUris
import android.content.ContentValues
import android.provider.ContactsContract
import com.d10ng.app.startup.ctx

/**
 * 添加联系人到系统里
 * @param name String 名字
 * @param phone String 电话
 */
fun addSystemContact(name: String, phone: String) {
    try {
        val contentValues = ContentValues()
        val uri = ctx.contentResolver.insert(
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
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name)
        ctx.contentResolver.insert(ContactsContract.Data.CONTENT_URI, contentValues)
        // 插入电话
        contentValues.clear()
        contentValues.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawId)
        contentValues.put(
            ContactsContract.Contacts.Data.MIMETYPE,
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
        )
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
        contentValues.put(
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
        )
        ctx.contentResolver.insert(ContactsContract.Data.CONTENT_URI, contentValues)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}