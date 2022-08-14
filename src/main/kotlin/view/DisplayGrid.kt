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
        widthProperty().onChange { draw() }
        heightProperty().onChange { draw() }

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
        draw()
    }

    fun animate(r: Int, c: Int, state: String) {
        cells[xyToIndex(r, c)].state = Cell.State.valueOf(state)
        draw()
    }

    fun has2RedSquares(): Boolean = cells.count { it.state == Cell.State.END } == 2

    private fun draw() {
        with(graphicsContext2D) {
            fill = c("#555555")
            fillRect(0.0, 0.0, width, height)

            cells.forEachIndexed { index, cell ->
                val w = width / cols!!
                val h = height / rows!!
                val x = index % cols!! * w + spacing
                val y = index / cols!! * h + spacing

                fill = cell.state.color
                fillRect(x, y, w - 2 * spacing, h - 2 * spacing)
            }
        }
    }

    data class Point(val x: Int, val y: Int)

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
        draw()
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