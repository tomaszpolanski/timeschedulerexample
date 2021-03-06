package com.tomek.timescheduler

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

typealias Second = Long

const val FAB_DELAY_TAG = "FAB_DELAY_TAG"
const val FAB_DELAY: Second = 3L
const val MESSAGE = "Hi there!"


class MainActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
    }

    private val clickStream: Observable<View> =
        Observable.create<View> { emitter ->
            val fab = findViewById<View>(R.id.fab) as FloatingActionButton
            fab.setOnClickListener {
                Toast.makeText(this, "Tap!", Toast.LENGTH_SHORT).show()
                emitter.onNext(fab)
            }
            emitter.setCancellable { fab.setOnClickListener(null) }
        }
            .subscribeOn(AndroidSchedulers.mainThread())

    override fun onResume() {
        super.onResume()
        disposable =
                clickStream.debounce(FAB_DELAY, TimeUnit.SECONDS, TimeScheduler.time(FAB_DELAY_TAG))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ view ->
                                   Snackbar.make(view, MESSAGE, Snackbar.LENGTH_SHORT)
                                       .show()
                               }) { error ->
                        Log.wtf("MainActivity", error)
                    }
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
        disposable = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        if (item.itemId == R.id.action_settings)
            true
        else
            super.onOptionsItemSelected(item)
}
