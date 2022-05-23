package org.marionette.server.module

import org.marionette.proto.rpc.PermissionType
import org.marionette.proto.rpc.PermissionsGrpcKt
import org.marionette.proto.rpc.requestPermissionOptions

class PermissionsModule(
    private val stub: PermissionsGrpcKt.PermissionsCoroutineStub
) {
    suspend fun request(type: PermissionType): Boolean {
        return stub.request(requestPermissionOptions { this.type = type }).value
    }
}