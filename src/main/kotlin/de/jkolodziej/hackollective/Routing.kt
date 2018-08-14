package de.jkolodziej.hackollective

import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.routing() {
    routing {
        intercept(ApplicationCallPipeline.Infrastructure) {

        }
        route("/hacker") {
            get("") {

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