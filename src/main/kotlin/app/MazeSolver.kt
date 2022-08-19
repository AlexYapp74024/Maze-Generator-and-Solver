package app

import kotlinx.coroutines.delay
import org.jpl7.Query
import org.jpl7.Term
import view.DisplayGrid
import java.io.File

class MazeSolver(private val grid: DisplayGrid, val type: Type) : PrologSolver() {
    override val file = File("mazeSolver.pl")
    override var tempFile = File("file.txt")

    enum class Type {
        dfs,
        bfs,
        a_star
    }

    override fun defaultQuery(): Query {
        val p1 = grid.endPoints().first
        val p2 = grid.endPoints().second
        return Query(
            "solve_maze",
            arrayOf(
                Term.textToTerm(grid.getStringRepresentation()),
                Term.textToTerm("_"),
                Term.textToTerm("p(${p1.y},${p1.x})"),
                Term.textToTerm("p(${p2.y},${p2.x})"),
                Term.textToTerm(type.toString())
            )
        )
    }

    init {
        Query.hasSolution("['${file.name}']")
    }

    override var query = defaultQuery()

    override suspend fun parseLine(line: String) {
        val input = line.split(" ")
        grid.animate(input[0].toInt(), input[1].toInt(), input[2])
        delay(25)
    }
}