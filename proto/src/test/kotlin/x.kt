import org.marionette.proto.node.INode
import kotlin.reflect.KClass

fun main(args: Array<String>) {
    val type = INode::onClick.returnType
    print(arrayOf(type, type.classifier, type.arguments.map { it.type?.classifier }).contentToString())
}