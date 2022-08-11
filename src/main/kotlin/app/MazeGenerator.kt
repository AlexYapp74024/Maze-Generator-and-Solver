package app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import view.MainView
import java.util.*

abstract class MazeGenerator(private val mainView: MainView) {
    protected val grid = mainView.displayGrid

    companion object {

        enum class GenerationType(val str: String) {
            RandomizeDFS("Randomized Dfs")
        }

        fun createGenerator(type: String) {
            GenerationType.valueOf(type)
        }
    }

    protected fun neighbours(index: Int): ArrayList<Int> {
        val p = grid.indexToXY(index)
        val out = arrayListOf<Int>()

        if (p.x - 2 >= 0)
            out.add(grid.xyToIndex(p.x - 2, p.y))
        if (p.x + 2 < grid.cols!!)
            out.add(grid.xyToIndex(p.x + 2, p.y))
        if (p.y - 2 >= 0)
            out.add(grid.xyToIndex(p.x, p.y - 2))
        if (p.y + 2 < grid.rows!!)
            out.add(grid.xyToIndex(p.x, p.y + 2))

        return out
    }

    protected fun connect(i1: Int, i2: Int) {
        val p1 = grid.indexToXY(i1)
        val p2 = grid.indexToXY(i2)

        grid.animate(p1.y, p1.x, "CLEAR")
        grid.animate((p1.y + p2.y) / 2, (p1.x + p2.x) / 2, "CLEAR")
        grid.animate(p2.y, p2.x, "CLEAR")
    }

    abstract fun generate()
}

class RandomizeDFS(mainView: MainView) : MazeGenerator(mainView) {

    private val stack = Stack<Int>()
    private val visited = arrayListOf(0)
    override fun generate() {
        CoroutineScope(Dispatchers.IO).launch {
            stack.push(0)

            while (!stack.empty()) {
                val current = stack.pop()
                val neighbour = neighbours(current)

                neighbour.removeIf { visited.contains(it) }
                if (neighbour.isEmpty()) continue

                stack.push(current)
                val next = neighbour.random()

                stack.push(next)
                visited.add(next)

                connect(current, next)
            }
        }
    }
}

class RandomizePrims(mainView: MainView) : MazeGenerator(mainView) {
    override fun generate() {

    }
}