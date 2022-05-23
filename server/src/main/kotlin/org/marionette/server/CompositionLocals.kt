package org.marionette.server

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.kotlin.AbstractCoroutineStub
import org.marionette.proto.rpc.DialogGrpcKt
import org.marionette.proto.rpc.FilesGrpcKt
import org.marionette.proto.rpc.PermissionsGrpcKt
import org.marionette.server.module.DialogModule
import org.marionette.server.module.FilesModule
import org.marionette.server.module.PermissionsModule

internal val LocalGrpcChannel = staticCompositionLocalOf<Channel> {
    error("No default channel")
}

val GrpcChannel: Channel
    @Composable
    @ReadOnlyComposable
    get() {
        return LocalGrpcChannel.current
    }

val Files: FilesModule
    @Composable
    get() {
        val stub = GrpcCoroutineStub(FilesGrpcKt::FilesCoroutineStub)
        return remember(stub) { FilesModule(stub) }
    }

val Permissions: PermissionsModule
    @Composable
    get() {
        val stub = GrpcCoroutineStub(PermissionsGrpcKt::PermissionsCoroutineStub)
        return remember(stub) { PermissionsModule(stub) }
    }

val Dialog: DialogModule
    @Composable
    get() {
        val stub = GrpcCoroutineStub(DialogGrpcKt::DialogCoroutineStub)
        return remember(stub) { DialogModule(stub) }
    }

@Composable
fun <T : AbstractCoroutineStub<T>> GrpcCoroutineStub(
    create: (Channel, CallOptions) -> T,
    options: CallOptions = CallOptions.DEFAULT,
): T {
    val channel = GrpcChannel
    return remember(channel, create, options) { create(channel, options) }
}

