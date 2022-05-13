package org.marionette.proto.rpc

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import java.net.SocketAddress

class MarionetteSocketAddress(
    val incoming: ReceiveChannel<ByteArray>,
    val outgoing: SendChannel<ByteArray>,
) : SocketAddress()