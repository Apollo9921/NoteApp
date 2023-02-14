package com.example.noteapp.view.navigation

sealed class Destination(val route: String) {
    object Home : Destination(route = "home")
    object AddNote : Destination(route = "add_note")
    object UpdateNote : Destination(route = "update_note/{id}") {
        fun passArguments(id: Int): String {
            return "update_note/$id"
        }
    }
}
