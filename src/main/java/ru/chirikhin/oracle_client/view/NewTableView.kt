package ru.chirikhin.oracle_client.view

import javafx.scene.layout.VBox
import ru.chirikhin.oracle_client.database.DatabaseControllerMock
import tornadofx.*
import java.util.ArrayList

class NewTableView(selectedTableSpace : String?) : View() {
    override val root = VBox()

    private val TABLESPACE = "Tablespace"
    private val TYPE_TABLENAME = "Name of the table"

    private val columnSettings = ArrayList<ColumnProperties>()

    init {
        with(root) {
            form {
                fieldset {
                    field(TABLESPACE) {
                        combobox <String> {
                            items = DatabaseControllerMock.getTablespaces().observable()

                            var i : Int = 0
                            items.forEach {
                                if (it == selectedTableSpace) {
                                    selectionModel.select(i)
                                    return@forEach
                                }

                                i++
                            }
                        }
                    }
                    field(TYPE_TABLENAME) {
                        textfield {

                        }
                    }

                    field("COLUMN_SETTINGS") {
                        tableview<ColumnProperties> {
                            contextmenu {
                                menuitem("Add column") {

                                }
                            }
                            items = columnSettings.observable()
                            column("NAME", ColumnProperties::name)
                            column("NOT NULL", ColumnProperties::notNull)
                            column("UNIQUE", ColumnProperties::isUnique)
                            column("PRIMARY KEY", ColumnProperties::isPrimaryKey)
                        }
                    }

                }
            }
        }
    }

    constructor() : this(null)

}
