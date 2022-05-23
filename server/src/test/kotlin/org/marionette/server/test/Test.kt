package org.marionette.server.test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.marionette.proto.rpc.PermissionType
import org.marionette.server.*

fun main(args: Array<String>) {
}

@Composable
fun Test() {
    val files = Files
    val permissions = Permissions
    val scope = rememberCoroutineScope()
    Image("", modifier = Modifier.clickable(remember(files, permissions, scope) {
        {
            scope.launch {
                if (permissions.request(PermissionType.PermissionType_File)) {
                    files.open("")
                }
            }
        }
    }))
}