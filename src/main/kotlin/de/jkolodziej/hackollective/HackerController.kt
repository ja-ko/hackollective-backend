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
import java.nio.file.Files

class HackerController(routing: Routing, private val repositoryManager: RepositoryManager) {

    init {
        routing.route("/hacker") {
            get("") {
                getHackers(call)
            }
            route("/{code}") {
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
        Files.newDirectoryStream(repositoryManager.hackerPath).use {
            call.respond(it.filter { Files.isDirectory(it) }.map { it.fileName.toString() })
        }
    }

    /**
     * List all items for the given key.
     */
    private suspend fun listItems(call: ApplicationCall) {
        val code = call.parameters["code"] // TODO make sure no path traversal is possible
        val codeDirectory = repositoryManager.hackerPath.resolve(code)
        if (!Files.exists(codeDirectory) || !Files.isDirectory(codeDirectory)) {
            call.respondText("Unknown code requested.", status = HttpStatusCode.BadRequest)
        } else {
            Files.newDirectoryStream(codeDirectory, "*.json").use { directoryContent ->
                call.respond(directoryContent.map { it.fileName.toString() })
            }
        }
    }

    /**
     * Retrieve given item from the given key.
     */
    private suspend fun getItem(call: ApplicationCall) {
        val code = call.parameters["code"] // TODO make sure no path traversal is possible
        val item = call.parameters["item"]

        val resultPath = repositoryManager.hackerPath.resolve(code).resolve(item)
        if (!Files.exists(resultPath) || !Files.isRegularFile(resultPath)) {
            call.respondText("Unknown item requested.", status = HttpStatusCode.BadRequest)
        } else {
            call.respondFile(resultPath.toFile())
        }
    }

}
