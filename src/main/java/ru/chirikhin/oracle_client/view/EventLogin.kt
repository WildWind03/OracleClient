package ru.chirikhin.oracle_client.view

import tornadofx.EventBus
import tornadofx.FXEvent


data class EventLogin(val ip : String, val port : String,
                      val username : String, val password : String,
                      val loginView : LoginView) : FXEvent(EventBus.RunOn.BackgroundThread)