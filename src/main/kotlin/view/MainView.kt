package view

import app.RandomizeDFS
import app.Styles
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.ToggleGroup
import tornadofx.*

class MainView : View("Hello TornadoFX") {

    val rowProperty = SimpleIntegerProperty(5)
    val colProperty = SimpleIntegerProperty(5)
    val displayGrid = DisplayGrid(this)
    val toggleGroup = ToggleGroup()

    override val root = borderpane {
        center = stackpane {
            add(displayGrid)
            displayGrid.widthProperty().bind(widthProperty())
            displayGrid.heightProperty().bind(heightProperty())
        }

        right = vbox {
            addClass(Styles.boxSpacing)
            hbox {
                addClass(Styles.boxSpacing)
                label("Height")
                spinner(property = rowProperty) { isEditable = true }
            }
            hbox {
                addClass(Styles.boxSpacing)
                label("Width ")
                spinner(property = colProperty) { isEditable = true }
            }

            hbox {
                addClass(Styles.boxSpacing)

                togglebutton("randomize_dfs", toggleGroup)
            }

            button("Generate") {
                action {
                    displayGrid.resize()
                    RandomizeDFS(this@MainView).generate()
                }
            }
        }
    }
}