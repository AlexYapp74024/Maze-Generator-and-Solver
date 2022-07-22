package app

import javafx.scene.control.ToggleButton
import org.jpl7.Integer
import org.jpl7.Query
import org.jpl7.Term
import tornadofx.*
import view.MainView
import java.io.File

class MazeGenerator(private val mainView: MainView) : PrologSolver() {
    override val file = File("mazeGenerator.pl")
    override var tempFile = File("maze.txt")

    private val rows by mainView.rowProperty
    private val cols by mainView.colProperty

    private val grid = mainView.displayGrid
    private fun mode(): String {
        mainView.toggleGroup.selectedToggle?.let {
            return@let (it as ToggleButton).text
        }
        return "randomize_dfs"
    }

    override var query = defaultQuery()
    override fun defaultQuery(): Query {
        val q1 = Query(
            "make_maze",
            arrayOf(
                Integer(rows.toLong()),
                Integer(cols.toLong()),
                Term.textToTerm(mode()),
                Term.textToTerm("_")
            )
        )
        return Query("once(${q1.toString()})")
    }

    init {
        mainView.rowProperty.onChange { resetQuery() }
        mainView.colProperty.onChange { resetQuery() }
        Query.hasSolution("['${file}']")
    }

    private fun resetQuery() {
        query = defaultQuery()
    }

    override fun parseLine(line: String) {
        val args = line.split(" ")
        if (args.size == 3)
            grid.animate(args[0].toInt(), args[1].toInt(), args[2].uppercase())
    }
}