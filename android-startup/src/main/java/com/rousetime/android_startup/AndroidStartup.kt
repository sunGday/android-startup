package com.rousetime.android_startup

import com.rousetime.android_startup.dispatcher.Dispatcher
import com.rousetime.android_startup.executor.ExecutorManager
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor

/**
 * Created by idisfkj on 2020/7/23.
 * Email: idisfkj@gmail.com.
 */
abstract class AndroidStartup<T> : Startup<T> {

    private val mWaitCountDown by lazy { CountDownLatch(getDependenciesCount()) }
    private val mObservers by lazy { mutableListOf<Dispatcher>() }

    override fun toWait() {
        try {
            mWaitCountDown.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun toNotify() {
        mWaitCountDown.countDown()
    }

    override fun createExecutor(): Executor = ExecutorManager.instance.ioExecutor

    override fun dependencies(): List<Class<out Startup<*>>>? {
        return null
    }

    override fun dependenciesByName(): List<String>? {
        return null
    }

    override fun dependenciesByPaths(): List<String>? {
        return null
    }

    override fun path(): String {
        return this::class.java.name
    }

    override fun getDependenciesCount(): Int {
        return if (!dependencies().isNullOrEmpty()) {
            dependencies()?.size ?: 0
        } else if (!dependenciesByName().isNullOrEmpty()) {
            dependenciesByName()?.size ?: 0
        } else {
            dependenciesByPaths()?.size ?: 0
        }
    }

    override fun onDependenciesCompleted(startup: Startup<*>, result: Any?) {}

    override fun manualDispatch(): Boolean = false

    override fun registerDispatcher(dispatcher: Dispatcher) {
        mObservers.add(dispatcher)
    }

    override fun onDispatch() {
        mObservers.forEach {
            it.toNotify()
        }
    }

    override fun isInitializationDoneInMainThread(): Boolean {
        return true
    }
}