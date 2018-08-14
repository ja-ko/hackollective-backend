package de.jkolodziej.hackollective

import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.DefaultHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.request.authorization
import io.ktor.request.header
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.routing() {
    install(DefaultHeaders)

    routing {
        intercept(ApplicationCallPipeline.Infrastructure) {
            val auth = call.request.authorization()
            val user = call.request.header("User")
            if (auth == null || user == null) {
                call.respondText("Authorization needed.", status = HttpStatusCode.Unauthorized)
            }

        }

        route("/hacker") {
            get("") {
                call.respondText { "success" }
            }
            route("/{key}") {
                get("") {

                }
                get("/{item}") {

                }
            }
        }
    }
}