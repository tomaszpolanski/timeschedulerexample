package com.tomek.timescheduler

import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/*
 * Copyright 2016 Futurice GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


typealias TimeHandler = (defaultScheduler: Scheduler, tag: String) -> Scheduler

/**
 * Scheduler used for time based operations.
 *
 *
 * During normal application runtime, the computation scheduler should be used.
 * But during the tests, there is possibility to override it to be able to better
 * test time based actions.
 */
object TimeScheduler {

    /**
     * Handler for time scheduler.
     */
    @Volatile var timeSchedulerHandler: TimeHandler? = null

    /**
     * Returns scheduler for time operations.
     *
     * @param tag Tag to be used in tests to mock the scheduler
     */
    fun time(tag: String): Scheduler {
        val handler = timeSchedulerHandler ?: return Schedulers.computation()
        return handler(Schedulers.computation(), tag)
    }

    /**
     * Resets current scheduler handler.
     */
    fun reset() {
        timeSchedulerHandler = null
    }
}
