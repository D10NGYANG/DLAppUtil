package com.d10ng.app.app.permission

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Permission manager which handles checking permission is granted or not and if not then will request permission.
 * A headless fragment which wraps the boilerplate code for checking and requesting permission
 * and suspends the coroutines until result is available.
 * A simple [Fragment] subclass.
 */
class PermissionManager : BasePermissionManager() {

    companion object {

        private const val TAG = "PermissionManager"

        /**
         * A static method to request permission from activity.
         *
         * @param activity an instance of [AppCompatActivity]
         * @param requestId Request ID for permission request
         * @param permissions Permission(s) to request
         *
         * @return [PermissionResult]
         *
         * Suspends the coroutines until result is available.
         */
        suspend fun requestPermissions(
            activity: AppCompatActivity,
            requestId: Int,
            vararg permissions: String
        ): PermissionResult {
            return withContext(Dispatchers.Main) {
                return@withContext _requestPermissions(
                    activity,
                    requestId,
                    *permissions
                )
            }
        }

        /**
         * A static method to request permission from fragment.
         *
         * @param fragment an instance of [Fragment]
         * @param requestId Request ID for permission request
         * @param permissions Permission(s) to request
         *
         * @return [PermissionResult]
         *
         * Suspends the coroutines until result is available.
         */
        suspend fun requestPermissions(
            fragment: Fragment,
            requestId: Int,
            vararg permissions: String
        ): PermissionResult {
            return withContext(Dispatchers.Main) {
                return@withContext _requestPermissions(
                    fragment,
                    requestId,
                    *permissions
                )
            }
        }

        private suspend fun _requestPermissions(
            activityOrFragment: Any,
            requestId: Int,
            vararg permissions: String
        ): PermissionResult {
            val fragmentManager = if (activityOrFragment is AppCompatActivity) {
                activityOrFragment.supportFragmentManager
            } else {
                (activityOrFragment as Fragment).childFragmentManager
            }
            return if (fragmentManager.findFragmentByTag(TAG) != null) {
                val permissionManager = fragmentManager.findFragmentByTag(TAG) as PermissionManager
                permissionManager.completableDeferred = CompletableDeferred()
                permissionManager.requestPermissions(
                    requestId,
                    *permissions
                )
                permissionManager.completableDeferred.await()
            } else {
                val permissionManager = PermissionManager().apply {
                    completableDeferred = CompletableDeferred()
                }
                fragmentManager.beginTransaction().add(
                    permissionManager,
                    TAG
                ).commitNow()
                permissionManager.requestPermissions(requestId, *permissions)
                permissionManager.completableDeferred.await()
            }
        }
    }

    private lateinit var completableDeferred: CompletableDeferred<PermissionResult>

    override fun onPermissionResult(permissionResult: PermissionResult) {
        // When fragment gets recreated due to memory constraints by OS completableDeferred would be
        // uninitialized and hence check
        if (::completableDeferred.isInitialized) {
            completableDeferred.complete(permissionResult)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::completableDeferred.isInitialized && completableDeferred.isActive) {
            completableDeferred.cancel()
        }
    }
}
