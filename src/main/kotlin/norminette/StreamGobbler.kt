package norminette

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.function.Consumer


class StreamGobbler(inputStream: InputStream, consumer: Consumer<String>) : Runnable {
    private val inputStream: InputStream
    private val consumer: Consumer<String>
    override fun run() {
        BufferedReader(InputStreamReader(inputStream)).lines()
            .forEach(consumer)
    }

    init {
        this.inputStream = inputStream
        this.consumer = consumer
    }
}