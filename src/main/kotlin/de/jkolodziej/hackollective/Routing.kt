package de.jkolodziej.hackollective

import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.DefaultHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.request.authorization
import io.ktor.request.header
import io.ktor.response.respondFile
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import java.nio.file.Files

fun Application.routing() {
    install(DefaultHeaders)

    routing {
        intercept(ApplicationCallPipeline.Infrastructure) {
            val auth = call.request.authorization()
            val user = call.request.header("User")
            if (auth == null || user == null) {
                call.respondText("Authorization needed.", status = HttpStatusCode.Unauthorized)
            }
            // TODO: check against registered Authorizations and log user
        }

        val hackerPath = repositoryManager.hackerPath
        val entityPath = repositoryManager.entityPath

        route("/hacker") {
            get("") {
                // TODO: construct a json with all keys in the hackerPath directory
            }
            route("/{key}") {
                get("") {
                    val key = call.parameters["key"] // TODO make sure no path traversal is possible
                    val keyDirectory = hackerPath.resolve(key)
                    if (!Files.exists(keyDirectory) || !Files.isDirectory(keyDirectory)) {
                        call.respondText("Unknown key requested.", status = HttpStatusCode.BadRequest)
                    } else {
                        // TODO construct a json with all entries of the directory
                    }
                }
                get("/{item}") {
                    val key = call.parameters["key"] // TODO make sure no path traversal is possible
                    val item = call.parameters["item"]

                    val resultPath = hackerPath.resolve(key).resolve(item)
                    if (!Files.exists(resultPath) || !Files.isRegularFile(resultPath)) {
                        call.respondText("Unknown item requested.", status = HttpStatusCode.BadRequest)
                    } else {
                        call.respondFile(resultPath.toFile())
                    }
                }
            }
        }
    }
}

