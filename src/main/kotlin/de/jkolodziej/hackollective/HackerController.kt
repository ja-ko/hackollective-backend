package de.jkolodziej.hackollective

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondFile
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import java.nio.file.Files

class HackerController(routing: Routing, private val repositoryManager: RepositoryManager) {

    init {
        routing.route("/hacker") {
            get("") {
                getHackers(call)
            }
            route("/{key}") {
                get("") {
                    listItems(call)
                }
                get("/{item}") {
                    getItem(call)
                }
            }
        }
    }

    /**
     * Retrieve all available hacker keys.
     */
    private suspend fun getHackers(call: ApplicationCall) {
        // TODO list all hacker keys from hackerPath
        call.respondText { "WIP" }
    }

    /**
     * List all items for the given key.
     */
    private suspend fun listItems(call: ApplicationCall) {
        val key = call.parameters["key"] // TODO make sure no path traversal is possible
        val keyDirectory = repositoryManager.hackerPath.resolve(key)
        if (!Files.exists(keyDirectory) || !Files.isDirectory(keyDirectory)) {
            call.respondText("Unknown key requested.", status = HttpStatusCode.BadRequest)
        } else {
            // TODO construct a json with all entries of the directory
            call.respondText { "WIP" }
        }
    }

    /**
     * Retrieve given item from the given key.
     */
    private suspend fun getItem(call: ApplicationCall) {
        val key = call.parameters["key"] // TODO make sure no path traversal is possible
        val item = call.parameters["item"]

        val resultPath = repositoryManager.hackerPath.resolve(key).resolve(item)
        if (!Files.exists(resultPath) || !Files.isRegularFile(resultPath)) {
            call.respondText("Unknown item requested.", status = HttpStatusCode.BadRequest)
        } else {
            call.respondFile(resultPath.toFile())
        }
    }

}
