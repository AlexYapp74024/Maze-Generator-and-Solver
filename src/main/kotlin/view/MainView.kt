package view

import app.GenerationType
import app.Styles
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.Alert
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import tornadofx.*

class MainView : View("Hello TornadoFX") {

    val rowProperty = SimpleIntegerProperty(5)
    val colProperty = SimpleIntegerProperty(5)
    val displayGrid = DisplayGrid(this)
    private val toggleGroup = ToggleGroup()

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

            vbox {
                addClass(Styles.boxSpacing)

                GenerationType.values().forEach {
                    togglebutton(it.name, toggleGroup)
                }
            }

            button("Generate") {
                action {
                    displayGrid.resize()
                    GenerationType.valueOf((toggleGroup.selectedToggle as ToggleButton).text)
                        .create(this@MainView)
                        .generate()
                }
            }

            button("Solve") {
                action {
                    if (!displayGrid.has2RedSquares()) {
                        alert(
                            Alert.AlertType.WARNING,
                            "Error",
                            "There must be exactly 2 end points (Red tiles) to solve"
                        )
                        return@action
                    }

                    println(displayGrid.getStringRepresentation())
                }
            }
        }
    }
}