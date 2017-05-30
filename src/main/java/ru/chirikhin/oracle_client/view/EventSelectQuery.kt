package ru.chirikhin.oracle_client.view

import tornadofx.FXEvent


data class EventSelectQuery(val query : String, val onErrorAction: StringArgRunnable,
                            val onSuccessAction: StringArgRunnable) : FXEvent()