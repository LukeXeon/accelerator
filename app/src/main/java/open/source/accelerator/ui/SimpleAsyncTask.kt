package open.source.accelerator.ui

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread

internal abstract class SimpleAsyncTask<T> : Runnable {
    @Volatile
    private var value: T? = null

    final override fun run() {
        val value = value
        if (value == null) {
            val time = SystemClock.uptimeMillis()
            this.value = doBackground()
            mainThread.postAtTime(this, time)
        } else {
            onResult(value)
        }
    }

    @WorkerThread
    protected abstract fun doBackground(): T

    @MainThread
    protected abstract fun onResult(value: T)


    companion object {
        private val mainThread = Handler(Looper.getMainLooper())
    }
}