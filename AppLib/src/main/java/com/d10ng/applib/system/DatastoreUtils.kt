package com.d10ng.applib.system

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.properties.ReadOnlyProperty

/**
 * Preference DataStore 键值对存储工具
 *
 * @Author: D10NG
 * @Time: 2021/8/12 3:03 下午
 */
object DatastoreUtils {

    private var mName: String = "settings"
    private var mSpName: String? = null

    fun init(name: String = mName, spName: String? = null) {
        mName = name
        mSpName = spName
    }

    fun createDataStore(): ReadOnlyProperty<Context, DataStore<Preferences>> {
        return preferencesDataStore(mName, produceMigrations = { context ->
            if (mSpName == null) listOf()
            else listOf(SharedPreferencesMigration(context, mSpName!!))
        })
    }
}

/**
 * DataStore
 */
val Context.dataStore: DataStore<Preferences> by DatastoreUtils.createDataStore()

/**
 * 在DataStore写入信息
 * @receiver Context
 * @param key String
 * @param value T
 * @return Preferences
 */
suspend inline fun <reified T : Any> Context.dataStoreWrite(key: String, value: T) = when (T::class) {
    String::class -> this.dataStore.edit { it[stringPreferencesKey(key)] = value as String }
    Int::class -> this.dataStore.edit { it[intPreferencesKey(key)] = value as Int }
    Boolean::class -> this.dataStore.edit { it[booleanPreferencesKey(key)] = value as Boolean }
    Long::class -> this.dataStore.edit { it[longPreferencesKey(key)] = value as Long }
    Float::class -> this.dataStore.edit { it[floatPreferencesKey(key)] = value as Float }
    Double::class -> this.dataStore.edit { it[doublePreferencesKey(key)] = value as Double }
    else -> throw IllegalArgumentException("Type not supported: ${T::class.java}")
}

/**
 * 在DataStore读取信息
 * @receiver Context
 * @param key String
 * @param default T?
 * @return T
 */
suspend inline fun <reified T : Any> Context.dataStoreRead(key: String, default: T? = null) = when (T::class) {
    String::class -> this.dataStore.data.map { it[stringPreferencesKey(key)] ?: default?: "" }.first()
    Int::class -> this.dataStore.data.map { it[intPreferencesKey(key)] ?: default?: 0 }.first()
    Boolean::class -> this.dataStore.data.map { it[booleanPreferencesKey(key)] ?: default?: false }.first()
    Long::class -> this.dataStore.data.map { it[longPreferencesKey(key)] ?: default?: 0L }.first()
    Float::class -> this.dataStore.data.map { it[floatPreferencesKey(key)] ?: default?: 0F }.first()
    Double::class -> this.dataStore.data.map { it[doublePreferencesKey(key)] ?: default?: 0 }.first()
    else -> throw IllegalArgumentException("Type not supported: ${T::class.java}")
} as T

/**
 * 在DataStore读取信息
 * @receiver Context
 * @param key String
 * @return T?
 */
suspend inline fun <reified T : Any> Context.dataStoreReadCanNull(key: String) = when (T::class) {
    String::class -> this.dataStore.data.map { it[stringPreferencesKey(key)] }.first()
    Int::class -> this.dataStore.data.map { it[intPreferencesKey(key)]}.first()
    Boolean::class -> this.dataStore.data.map { it[booleanPreferencesKey(key)] }.first()
    Long::class -> this.dataStore.data.map { it[longPreferencesKey(key)] }.first()
    Float::class -> this.dataStore.data.map { it[floatPreferencesKey(key)] }.first()
    Double::class -> this.dataStore.data.map { it[doublePreferencesKey(key)] }.first()
    else -> throw IllegalArgumentException("Type not supported: ${T::class.java}")
} as T?