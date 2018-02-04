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
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

const val FAB_DELAY = "FAB_DELAY"
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
        Observable.create { emitter ->
            val fab = findViewById<View>(R.id.fab) as FloatingActionButton
            fab.setOnClickListener {
                emitter.onNext(fab)
            }
            emitter.setCancellable { fab.setOnClickListener(null) }
        }

    override fun onResume() {
        super.onResume()
        disposable = clickStream.debounce(5, TimeUnit.SECONDS, TimeScheduler.time(FAB_DELAY))
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                           Snackbar.make(it, MESSAGE, Snackbar.LENGTH_LONG)
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
