package de.jkolodziej.hackollective

import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.authorization
import io.ktor.request.header
import io.ktor.response.respondText
import io.ktor.routing.routing

fun Application.startBackend() {
    install(DefaultHeaders)
    install(ContentNegotiation) {
        gson {
            configureGson(this)
        }
    }

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
        val entityMapper = EntityMapper(entityPath.resolve("mapping.json"))

        val hackerController = HackerController(this, repositoryManager)
        val entityController = EntityController(this, repositoryManager)
    }
}

