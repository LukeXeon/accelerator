package open.source.accelerator.ui

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import androidx.annotation.MainThread
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import open.source.accelerator.mappings.MappingRenderNode
import open.source.accelerator.proto.PbRenderNode
import open.source.accelerator.proto.PbTemplate


class AcceleratorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AbstractComposeView(context, attrs, defStyleAttr) {

    private var worker: Handler? = null

    private var root by mutableStateOf<PbRenderNode?>(null)

    @Composable
    override fun Content() {
        val node = root
        if (node != null) {
            MappingRenderNode(descriptor = node, modifier = Modifier)
        }
    }

    @MainThread
    fun setTemplate(data: ByteArray) {
        var worker = worker
        if (worker == null) {
            worker = Handler(HandlerThread(toString()).apply { start() }.looper)
            this.worker = worker
        }
        worker.post {
            val template = PbTemplate.parseFrom(data)
            val newRoot = template.root
            post {
                root = newRoot
            }
        }
    }

    override fun onDetachedFromWindow() {
        worker?.looper?.quit()
        worker = null
        super.onDetachedFromWindow()
    }

}