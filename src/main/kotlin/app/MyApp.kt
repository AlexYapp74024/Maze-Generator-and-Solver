package app

import javafx.stage.Stage
import tornadofx.*
import view.MainView

class MyApp : App(MainView::class, Styles::class) {
    init {
        reloadStylesheetsOnFocus()
    }

    override fun start(stage: Stage) {
        with(stage) {
            width = 1200.0
            height = 800.0
        }
        super.start(stage)
    }
}