import kotlinx.coroutines.*

fun main(args: Array<String>) {

    runBlocking {
        var job: Job? = null
        job = GlobalScope.launch {
            delay(1000)
            println("asdasdad")
        }
        delay(1500)
        job.cancelAndJoin()
    }
}