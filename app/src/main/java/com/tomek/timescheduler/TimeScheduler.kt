package com.tomek.timescheduler

import io.reactivex.Scheduler
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

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

/**
 * Scheduler used for time based operations.
 *
 *
 * During normal application runtime, the computation scheduler should be used.
 * But during the tests, there is possibility to override it to be able to better
 * test time based actions.
 */
object TimeScheduler {

    @Volatile private var onTimeHandler: BiFunction<Scheduler, String, Scheduler>? = null

    /**
     * Returns scheduler for time operations.
     *
     * @param tag Tag to be used in tests to mock the scheduler
     */
    fun time(tag: String): Scheduler {
        val f = onTimeHandler ?: return Schedulers.computation()
        return f.apply(Schedulers.computation(), tag)
    }

    /**
     * Sets handler for time scheduler.
     *
     * @param handler Handler to be used
     */
    fun setTimeSchedulerHandler(handler: BiFunction<Scheduler, String, Scheduler>?) {
        onTimeHandler = handler
    }

    /**
     * Resets current scheduler handler.
     */
    fun reset() {
        onTimeHandler = null
    }
}
