package de.jkolodziej.hackollective

import io.ktor.application.*
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
        val hackerPath = repositoryManager.hackerPath
        val entityPath = repositoryManager.entityPath
        entityMapper = EntityMapper(entityPath.resolve("mapping.json"))

        intercept(ApplicationCallPipeline.Infrastructure) {
            val auth = call.request.authorization()
            val user = call.request.header("User")
            if (auth == null || user == null || entityMapper?.resolveEntityForKey(auth) == null) {
                call.respondText("Authorization needed.", status = HttpStatusCode.Unauthorized)
                this.finish()
                return@intercept
            }
            // TODO: log user and authorization

        }


        val entityController = EntityController(this, repositoryManager)
        val hackerController = HackerController(this, repositoryManager)
    }
}

private var entityMapper: EntityMapper? = null

val ApplicationCall.entity: EntityMapper.Entity?
    get() = entityMapper?.resolveEntityForKey(request.authorization())
