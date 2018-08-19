package de.jkolodziej.hackollective

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route

class EntityController(routing: Routing, private val repositoryManager: RepositoryManager) {

    init {
        routing.route("/entity") {
            get("") {
                listAvailableItems(call)
            }
            get("/{item}") {
                retrieveItem(call)
            }
        }
    }

    private suspend fun retrieveItem(call: ApplicationCall) {
        call.respond(call.entity?.listItems().orEmpty())
    }

    private suspend fun listAvailableItems(call: ApplicationCall) {
        val file = call.entity?.retrieveItem(call.parameters["item"])?.toFile()
        if (file == null) {
            call.respondText("Unable to retrieve item", status = HttpStatusCode.NotFound)
        } else {
            call.respondFile(file)
        }
    }
}

