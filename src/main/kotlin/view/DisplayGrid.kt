package view

import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import tornadofx.*

class DisplayGrid(view: MainView) : Canvas() {

    private val rows by view.rowProperty.objectBinding { it!!.toInt() * 2 - 1 }
    private val cols by view.colProperty.objectBinding { it!!.toInt() * 2 - 1 }
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
    override fun prefHeight(width: Double): Double = height
    override fun prefWidth(width: Double): Double = width

    private fun toggleCell(r: Int, c: Int) {
        val index = index(r, c)
        val cell = cells[index]
        cell.state = if (cell.state == Cell.State.CLEAR) {
            Cell.State.WALL
        } else {
            Cell.State.CLEAR
        }
        draw()
    }

    fun animate(r: Int, c: Int, state: String) {
        with(graphicsContext2D) {
            val w = width / cols!!
            val h = height / rows!!
            val x = r * w + spacing
            val y = c * h + spacing

            fill = Cell.State.from_String(state).color()
            fillRect(x, y, w - 2 * spacing, h - 2 * spacing)
        }
    }

    private fun draw() {
        with(graphicsContext2D) {
            fill = c("#555555")
            fillRect(0.0, 0.0, width, height)

            cells.forEachIndexed { index, cell ->
                val w = width / cols!!
                val h = height / rows!!
                val x = index % cols!! * w + spacing
                val y = index / cols!! * h + spacing

                fill = cell.state.color()
                fillRect(x, y, w - 2 * spacing, h - 2 * spacing)
            }
        }
    }

    private fun index(r: Int, c: Int) = r * cols!! + c

    private fun resize() {
        cells = mutableListOf()
        repeat(rows!! * cols!!) {
            cells.add(Cell())
        }
        draw()
    }
}

class Cell {
    enum class State {
        SEARCH {
            override fun color(): Color = Color.CYAN
        },
        PATH {
            override fun color(): Color = Color.ORANGE
        },
        WALL {
            override fun color(): Color = Color.BLACK
        },
        CLEAR {
            override fun color(): Color = Color.WHITE
        };

        abstract fun color(): Color

        companion object {
            fun from_String(str: String): State {
                return when (str) {
                    "SEARCH" -> SEARCH
                    "PATH" -> PATH
                    "WALL" -> WALL
                    else -> CLEAR
                }
            }
        }
    }

    var state = State.WALL
}