package com.tomek.timescheduler

import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import java.util.concurrent.TimeUnit

/**
 * Scheduler that instantly executes the task.
 */
object InstantScheduler : Scheduler() {
    override fun createWorker(): Worker =
        object : Worker() {
            override fun isDisposed(): Boolean = false

            override fun dispose() = Unit

            override fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
                run.run()
                return Disposables.empty()
            }
        }
}
