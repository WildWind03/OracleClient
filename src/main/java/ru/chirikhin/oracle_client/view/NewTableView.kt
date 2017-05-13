package ru.chirikhin.oracle_client.view

import javafx.geometry.Orientation
import javafx.scene.layout.VBox
import ru.chirikhin.oracle_client.database.Constraint
import ru.chirikhin.oracle_client.model.Column
import ru.chirikhin.oracle_client.model.DatabaseRepresentation
import tornadofx.*
import java.util.ArrayList

class NewTableView(val databaseRepresentation: DatabaseRepresentation) : View() {
    override val root = VBox()

    private val TABLESPACE = "Tablespace"
    private val TABLE_NAME = "Name of the table"
    private val EXAMPLE_NAME_OF_TABLE = "New table"

    private val columnSettings = ArrayList<Column>()
    private val constraints = ArrayList<Constraint>()

    init {
        with(root) {
            form {
                fieldset {
                    labelPosition = Orientation.VERTICAL
                    hbox {
                        vbox {
                            field(TABLESPACE) {
                                combobox <String> {
                                    items = databaseRepresentation.getTablespaces().observable()
                                    selectionModel.select(0)
                                }
                            }

                            field("Table description") {
                                listview<Column> {
                                    items = columnSettings.observable()
                                }
                            }

                            button {
                                text = "Add column"

                                action {
                                    AddColumnView(columnSettings).openModal(resizable = false)
                                }
                            }
                        }

                        vbox {
                            paddingLeft = 15
                            field(TABLE_NAME) {
                                textfield {
                                    text = EXAMPLE_NAME_OF_TABLE
                                }
                            }

                            field("Constraints") {
                                listview<Constraint> {
                                    items = constraints.observable()
                                }
                            }

                            button("Add constraint") {

                            }
                        }
                    }

//                    tableview<ColumnProperties> {
//                        items = columnSettings.observable()
//                        column("NAME", ColumnProperties::name)
//                        column("NOT NULL", ColumnProperties::notNull)
//                        column("UNIQUE", ColumnProperties::isUnique)
//                        column("PRIMARY KEY", ColumnProperties::isPrimaryKey)
//                    }

                }
            }
        }
    }
}
