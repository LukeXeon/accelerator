package open.source.accelerator.ui

import android.content.Context
import android.os.*
import android.util.AttributeSet
import androidx.annotation.MainThread
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import open.source.accelerator.mappings.MappingNode
import open.source.accelerator.proto.PbNode
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong


class AcceleratorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {

    private var templateNode by mutableStateOf<PbNode?>(null)
    private var diffNode by mutableStateOf<PbNode?>(null)

    @Composable
    override fun Content() {
        var completeNode by remember { mutableStateOf<PbNode?>(null) }
        DisposableEffect(templateNode, diffNode) {
            val tNode = templateNode
            val diff = diffNode
            if (tNode != null && diff != null) {
                workers.execute(object : SimpleAsyncTask<PbNode>() {
                    override fun doBackground(): PbNode {
                        return tNode
                            .toBuilder()
                            .mergeFrom(diff)
                            .build()
                    }

                    override fun onResult(value: PbNode) {
                        completeNode = value
                    }
                })
            }
            onDispose {
                completeNode = null
            }
        }
        completeNode?.let { MappingNode(it, Modifier) }
    }

    @MainThread
    fun setDiff(diff: ByteArray) {
        workers.execute(object : SimpleAsyncTask<PbNode>() {
            override fun doBackground(): PbNode {
                return PbNode.parseFrom(diff)
            }

            override fun onResult(value: PbNode) {
                templateNode = value
            }
        })
    }

    @MainThread
    fun setTemplate(base: ByteArray) {
        workers.execute(object : SimpleAsyncTask<PbNode>() {
            override fun doBackground(): PbNode {
                return PbNode.parseFrom(base)
            }

            override fun onResult(value: PbNode) {
                templateNode = value
            }
        })
    }

    private class SavedState : BaseSavedState {
        constructor(source: Parcel) : super(source) {
            templateNode = PbNode.parseFrom(source.createByteArray())
            diffNode = PbNode.parseFrom(source.createByteArray())
        }

        constructor(superState: Parcelable?) : super(superState)

        var templateNode: PbNode? = null
        var diffNode: PbNode? = null

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeByteArray(templateNode?.toByteArray())
            out.writeByteArray(diffNode?.toByteArray())
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }

    }

    override fun onSaveInstanceState(): Parcelable {
        val ss = SavedState(super.onSaveInstanceState())
        ss.templateNode = templateNode
        ss.diffNode = diffNode
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        templateNode = state.templateNode
        diffNode = state.diffNode
    }

    companion object {
        private val workers = ThreadPoolExecutor(
            2,
            Runtime.getRuntime().availableProcessors(),
            60,
            TimeUnit.SECONDS,
            LinkedBlockingQueue(),
            object : ThreadFactory {
                private val counter = AtomicLong()
                override fun newThread(r: Runnable?): Thread {
                    return Thread {
                        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
                        r?.run()
                    }.apply {
                        name = "accelerator-thread-${counter.incrementAndGet()}"
                    }
                }
            }
        )
    }
}