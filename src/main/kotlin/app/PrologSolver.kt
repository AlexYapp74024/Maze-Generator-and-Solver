package app

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import org.jpl7.Query
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

abstract class PrologSolver {
    protected abstract val file: File
    protected abstract var tempFile: File
    private val prolog_dir = File("src//main//prolog")

    abstract fun defaultQuery(): Query
    abstract var query: Query

    init {
        Query.hasSolution(
            "working_directory(_,\'${
                prolog_dir.absolutePath.replace("\\", "\\\\")
            }\')"
        )
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun solve() {
        with(tempFile) {
            if (exists() && !delete()) {
                println("delete Failed")
                return@with
            }
            createNewFile()
            deleteOnExit()
        }
        if (writing) {
            println("Last Query still processing")
            return
        }

        if (!this::queryJob.isInitialized || !queryJob.isActive) {
            println(query.goal())
            queryJob = CoroutineScope(newSingleThreadContext("Query Thread")).launch {
                do {
                    writing = true
                    CoroutineScope(IO).launch { animate() }

                    query.next()
                    animating.await()
                    writing = false
                    pausing = CompletableDeferred()
                    pausing.await()
                } while (query.hasNext())
            }
        } else {
            println("Not done yet")
            pausing.complete(Unit)
        }
    }

    private lateinit var queryJob: Job

    private
    var pausing = CompletableDeferred<Unit>()
    private var animating = CompletableDeferred<Unit>()
    private var writing = false

    private suspend fun animate() {
        val reader = BufferedReader(FileReader(tempFile))

        animating.complete(Unit)
        do {
            var l = reader.readLine()
            while (l != null) {
                parseLine(l)
                l = reader.readLine()
            }
        } while (writing)
        animating = CompletableDeferred()

        reader.close()
        tempFile.delete()
    }

    protected abstract suspend fun parseLine(line: String)

    protected fun cancelQuery() {
        if (this::queryJob.isInitialized && queryJob.isActive)
            queryJob.cancel()
    }
}