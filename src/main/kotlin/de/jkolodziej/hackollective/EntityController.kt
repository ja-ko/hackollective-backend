package de.jkolodziej.hackollective

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route

class EntityController(routing: Routing, private val repositoryManager: RepositoryManager) {

    init {
        routing.route("/hacker") {
            get("") {
                listAvailableItems(call)
            }
            get("/{item}") {
                retrieveItem(call)
            }
        }
    }

    private fun retrieveItem(call: ApplicationCall) {
        // TODO implement
    }

    private fun listAvailableItems(call: ApplicationCall) {
        // TODO implement
    }
}

