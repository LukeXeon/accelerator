package org.marionette.server.module

import androidx.compose.runtime.Composable
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.marionette.proto.rpc.DialogGrpcKt

class DialogModule(
    private val stub: DialogGrpcKt.DialogCoroutineStub
) {
    suspend fun openAsync(content: @Composable () -> Unit): Deferred<Unit> {
        return coroutineScope {
            async {
                
            }
        }
    }
}