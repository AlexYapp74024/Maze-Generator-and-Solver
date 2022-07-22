package app

import javafx.geometry.Pos
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val boxSpacing by cssclass()
    }

    init {
        boxSpacing {
            padding = box(5.px)
            alignment = Pos.CENTER
            spacing = 5.px
        }
    }
}