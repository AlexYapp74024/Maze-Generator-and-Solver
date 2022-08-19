package view

import app.MazeGenerator
import app.MazeSolver
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
    private val generatorToggleGroup = ToggleGroup()
    private val solverToggleGroup = ToggleGroup()

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
                label("Generation Methods")

                addClass(Styles.boxSpacing)

                MazeGenerator.Type.values().forEach {
                    togglebutton(it.name, generatorToggleGroup)
                }
            }

            button("Generate") {
                action {
                    displayGrid.resize()
                    MazeGenerator.Type.valueOf((generatorToggleGroup.selectedToggle as ToggleButton).text)
                        .create(this@MainView)
                        .generate()
                }
            }

            hbox {
                region {
                    prefHeight = 20.0
                }
            }

            vbox {
                label("Solving Algorithms")

                addClass(Styles.boxSpacing)

                MazeSolver.Type.values().forEach {
                    togglebutton(it.name, solverToggleGroup)
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

                    displayGrid.cleanup()

                    MazeSolver(
                        displayGrid,
                        MazeSolver.Type.valueOf((solverToggleGroup.selectedToggle as ToggleButton).text)
                    ).solve()
                }
            }
        }
    }
}