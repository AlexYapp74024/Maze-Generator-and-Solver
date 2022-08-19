package view

import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import tornadofx.*

class DisplayGrid(view: MainView) : Canvas() {

    val rows by view.rowProperty.objectBinding { it!!.toInt() * 2 - 1 }
    val cols by view.colProperty.objectBinding { it!!.toInt() * 2 - 1 }
    private val spacing = 1.0

    private var cells = mutableListOf<Cell>()

    init {
        view.colProperty.onChange { resize() }
        view.rowProperty.onChange { resize() }
        widthProperty().onChange { redraw() }
        heightProperty().onChange { redraw() }

        setOnMouseClicked {
            toggleCell(
                (it.y * rows!! / height).toInt(),
                (it.x * cols!! / width).toInt()
            )
        }

        resize()
    }

    override fun isResizable(): Boolean = true
    override fun prefHeight(height: Double): Double = height
    override fun prefWidth(width: Double): Double = width

    private fun toggleCell(r: Int, c: Int) {
        val cell = cells[xyToIndex(r, c)]

        // Cannot interact with wall
        if (cell.state == Cell.State.WALL) return
        // Can only have up to 2 end points

        cell.state = if (cell.state == Cell.State.CLEAR) {
            if (has2RedSquares()) return
            Cell.State.END
        } else {
            Cell.State.CLEAR
        }
        redraw()
    }

    fun animate(r: Int, c: Int, state: String) {
        val cell = cells[xyToIndex(c, r)]
        if (cell.state == Cell.State.END) return
        cell.state = Cell.State.valueOf(state)
        drawCell(xyToIndex(c, r))
    }

    fun has2RedSquares(): Boolean = cells.count { it.state == Cell.State.END } == 2

    fun endPoints(): Pair<Point, Point> {
        val points = cells.withIndex().filter { it.value.state == Cell.State.END }.map { it.index }
        return Pair(indexToXY(points[0]), indexToXY(points[1]))
    }

    private fun redraw() {
        with(graphicsContext2D) {
            fill = c("#555555")
            fillRect(0.0, 0.0, width, height)

            cells.forEachIndexed { index, cell ->
                drawCell(index)
            }
        }
    }

    fun cleanup() {
        cells.forEach {
            if (it.state != Cell.State.WALL && it.state != Cell.State.END)
                it.state = Cell.State.CLEAR
        }
        redraw()
    }

    private fun drawCell(index: Int) {
        with(graphicsContext2D) {
            val w = width / cols!!
            val h = height / rows!!
            val x = index % cols!! * w + spacing
            val y = index / cols!! * h + spacing

            fill = cells[index].state.color
            fillRect(x, y, w - 2 * spacing, h - 2 * spacing)
        }
    }

    fun getStringRepresentation(): String {
        val list = mutableListOf<MutableList<Char>>()

        repeat(rows!!) { r ->
            val rowList = mutableListOf<Char>()
            repeat(cols!!) { c ->
                rowList.add(cells[xyToIndex(r, c)].state.charCode)
            }
            list.add(rowList)
        }

        return list.toString()
    }

    data class Point(val x: Int, val y: Int)

    fun xyToIndex(r: Int, c: Int) = r * cols!! + c
    fun indexToXY(index: Int) = Point(
        index / cols!!,
        index % cols!!
    )

    fun resize() {
        cells = mutableListOf()
        repeat(rows!! * cols!!) {
            cells.add(Cell())
        }
        redraw()
    }
}

class Cell {
    enum class State(val color: Color, val charCode: Char) {
        SEARCH(Color.CYAN, 's'),
        PATH(Color.ORANGE, 'p'),
        WALL(Color.BLACK, 'w'),
        END(Color.RED, 'e'),
        CLEAR(Color.WHITE, 'c');
    }

    var state = State.WALL
}