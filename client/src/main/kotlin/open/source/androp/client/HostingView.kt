package open.source.androp.client

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import okhttp3.OkHttpClient
import open.source.androp.proto.InsertItem
import open.source.androp.proto.TypedValue


open class HostingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private val callbacks = object : SocketSession.Callbacks {
        override fun onInsert(id: Int, start: Int, items: List<InsertItem>) {

        }

        override fun onMove(id: Int, from: Int, to: Int, count: Int) {

        }

        override fun onRemove(id: Int, start: Int, count: Int) {

        }

        override fun onUpdate(id: Int, values: List<TypedValue>) {

        }

        override fun onCleanup() {
            
        }
    }
    private var session: SocketSession? = null

    @Synchronized
    fun connect(httpClient: OkHttpClient, url: String) {
        disconnect()
        session = SocketSession.open(httpClient, url, callbacks)
    }

    @Synchronized
    fun disconnect() {
        session?.close()
        session = null
    }

}