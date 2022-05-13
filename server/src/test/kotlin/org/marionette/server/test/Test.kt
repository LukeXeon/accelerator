package org.marionette.server.test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.marionette.proto.rpc.*
import org.marionette.server.CoroutineStub

fun main(args: Array<String>) {
}

@Composable
fun Test() {
    val files = CoroutineStub<FilesGrpcKt.FilesCoroutineStub>()
    val permissions = CoroutineStub<PermissionsGrpcKt.PermissionsCoroutineStub>()
    LaunchedEffect(files) {
        if (permissions.request(requestPermissionOptions {
                type = PermissionType.PermissionType_File
            }).value) {
            files.open(fileOpenOptions {
                path = ""
            })

        }


    }
}