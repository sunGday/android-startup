package com.rousetime.android_startup

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.os.TraceCompat
import com.rousetime.android_startup.execption.StartupException
import com.rousetime.android_startup.extensions.getUniqueKey
import com.rousetime.android_startup.manager.StartupCacheManager
import com.rousetime.android_startup.model.StartupProviderStore
import com.rousetime.android_startup.provider.StartupProviderConfig

/**
 * Created by idisfkj on 2020/8/7.
 * Email: idisfkj@gmail.com.
 */
class StartupInitializer {

    companion object {
        @JvmStatic
        val instance by lazy { StartupInitializer() }
    }

    internal fun discoverAndInitialize(context: Context): StartupProviderStore {

        TraceCompat.beginSection(StartupInitializer::class.java.simpleName)

        val result = mutableListOf<AndroidStartup<*>>()
        var config: StartupProviderConfig? = null
        try {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            val startup = context.getString(R.string.android_startup)
            val providerConfig = context.getString(R.string.android_startup_provider_config)
            appInfo.metaData?.let { metaData ->
                metaData.keySet().forEach { key ->
                    val value = metaData[key]
                    val clazz = Class.forName(key)
                    if (providerConfig == value) {
                        if (StartupProviderConfig::class.java.isAssignableFrom(clazz)) {
                            config = clazz.getDeclaredConstructor()
                                .newInstance() as? StartupProviderConfig
                            // save initialized config
                            StartupCacheManager.instance.saveConfig(config?.getConfig())
                        }
                    } else if (startup == value) {
                        if (AndroidStartup::class.java.isAssignableFrom(clazz)) {
                            val instance =
                                (clazz.getDeclaredConstructor().newInstance() as AndroidStartup<*>)
                            if (!result.contains(instance)) {
                                result.add(instance)
                            }
                        }
                    }
                }
            }
        } catch (t: Throwable) {
            throw StartupException(t)
        }

        TraceCompat.endSection()

        return StartupProviderStore(result, config)
    }
}