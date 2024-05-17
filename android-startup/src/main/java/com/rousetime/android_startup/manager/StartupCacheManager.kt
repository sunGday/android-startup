package com.rousetime.android_startup.manager

import com.rousetime.android_startup.AndroidStartup
import com.rousetime.android_startup.Startup
import com.rousetime.android_startup.model.ResultModel
import com.rousetime.android_startup.model.StartupConfig
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by idisfkj on 2020/8/11.
 * Email: idisfkj@gmail.com.
 */
class StartupCacheManager {

    /**
     * Save initialized components result.
     */
    private val mInitializedComponents = ConcurrentHashMap<Class<out Startup<*>>, ResultModel<*>>()

    private val mRouters = ConcurrentHashMap<String, AndroidStartup<*>>()
    var initializedConfig: StartupConfig? = null
        private set

    companion object {
        @JvmStatic
        val instance by lazy { StartupCacheManager() }
    }

    /**
     * save result of initialized component.
     */
    internal fun saveInitializedComponent(zClass: Class<out Startup<*>>, result: ResultModel<*>) {
        mInitializedComponents[zClass] = result
    }

    /**
     * save router list
     */
    internal fun saveRouterList(path: String, zClass: AndroidStartup<*>) {
        mRouters[path] = zClass
    }

    internal fun obtainRouterInstance(path: String): AndroidStartup<*>? {
        return mRouters[path]
    }


    /**
     * check initialized.
     */
    fun hadInitialized(zClass: Class<out Startup<*>>): Boolean =
        mInitializedComponents.containsKey(zClass)

    @Suppress("UNCHECKED_CAST")
    fun <T> obtainInitializedResult(zClass: Class<out Startup<*>>): T? =
        mInitializedComponents[zClass]?.result as? T?

    fun remove(zClass: Class<out Startup<*>>) {
        mInitializedComponents.remove(zClass)
    }

    fun clear() {
        mInitializedComponents.clear()
    }

    /**
     * save initialized config.
     */
    internal fun saveConfig(config: StartupConfig?) {
        initializedConfig = config
    }
}