package org.marionette.rpc

import com.google.protobuf.BytesValue
import io.grpc.stub.StreamObserver
import org.marionette.proto.rpc.FileOpenOptions
import org.marionette.proto.rpc.FilesGrpc

class FilesApi : FilesGrpc.FilesImplBase() {
    override fun open(request: FileOpenOptions, responseObserver: StreamObserver<BytesValue>) {

    }
}