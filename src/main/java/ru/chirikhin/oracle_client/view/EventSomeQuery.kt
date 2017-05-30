package ru.chirikhin.oracle_client.view

import tornadofx.FXEvent


data class EventSomeQuery(val query : String, val onErrorAction: StringArgRunnable,
                          val onSuccessAction: StringArgRunnable,
                          val runnable: DatabaseRepresentationRunnable) : FXEvent()