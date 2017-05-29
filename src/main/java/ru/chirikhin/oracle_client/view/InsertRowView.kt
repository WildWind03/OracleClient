package ru.chirikhin.oracle_client.view

import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.layout.VBox
import ru.chirikhin.oracle_client.database.MyColumn
import ru.chirikhin.oracle_client.model.Column
import ru.chirikhin.oracle_client.util.showSQLInternalError
import tornadofx.*


class InsertRowView(val columns: Collection<Column>, val nameOfTable: String, val realRow : ObservableList<List<String>>) : View() {
    override val root = VBox()

    init {
        val myColumns: ArrayList<MyColumn> = ArrayList()
        val observableMyColumns = myColumns.observable()

        columns.forEach {
            observableMyColumns.add(MyColumn(it.name, ""))
        }

        with(root) {
            alignmentProperty().value = Pos.CENTER
            tableview(observableMyColumns) {
                isEditable = true
                column("Column Name", MyColumn::columnName)
                column("Column Value", MyColumn::columnValue).useTextField()
            }

            button("OK") {
                action {
                    try {
                        fire(EventInsertRow(nameOfTable, observableMyColumns))
                        realRow.add(observableMyColumns.map { it.columnValue ?: "null" })
                    } catch (e : Exception) {
                        showSQLInternalError(e.toString())
                    }
                    close()
                }
            }
        }
    }
}