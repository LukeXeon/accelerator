package org.marionette.server.module

import com.google.protobuf.ByteString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.marionette.proto.rpc.FilesGrpcKt
import org.marionette.proto.rpc.fileOpenOptions

class FilesModule(
    private val stub: FilesGrpcKt.FilesCoroutineStub
) {
    fun open(path: String): Flow<ByteString> {
        return stub.open(fileOpenOptions { this.path = path })
            .map {
                it.value
            }
    }
}