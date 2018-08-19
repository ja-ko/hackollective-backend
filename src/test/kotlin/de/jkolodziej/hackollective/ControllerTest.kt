package de.jkolodziej.hackollective

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.ktor.config.MapApplicationConfig
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.TestApplicationRequest
import io.ktor.server.testing.withTestApplication
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

open class ControllerTest {
    protected fun setupTestFixture(workingDir: Path) {
        val hackerDir = workingDir.resolve("hacker")
        Files.createDirectory(hackerDir)
        Files.createDirectory(hackerDir.resolve("WH"))
        Files.createDirectory(hackerDir.resolve("BH"))
        putMappingJsonTo(hackerDir.resolve(Paths.get("WH", "item.json")))

        val entityDir = workingDir.resolve("entities")
        Files.createDirectory(entityDir)
        putMappingJsonTo(entityDir.resolve("mapping.json"))


    }

    private fun putMappingJsonTo(path: Path) {
        Files.newBufferedWriter(path).use { writer ->
            this::class.java.getResourceAsStream("/testmapping.json").use {
                writer.write(it.reader().readText())
            }
        }
    }

    protected fun indicateSuccess(): Matcher<HttpStatusCode?> = object : Matcher<HttpStatusCode?> {
        override fun test(value: HttpStatusCode?): Result = Result(value?.isSuccess()
                ?: false, "$value should indicate successful", "$value should not indicate successful")
    }

    protected fun <R> withConfiguredTestApplication(test: TestApplicationEngine.() -> R): R {
        return withTestApplication({
            val localDir = Files.createTempDirectory("hackertest")
            (environment.config as MapApplicationConfig).apply {
                put("git.local.path", localDir.toString())
            }
            setupTestFixture(localDir)

            startRepository()
            startBackend()
        }, test)
    }

    fun TestApplicationRequest.addAuthentication() {
        addHeader("Authorization", "some-random-words")
        addHeader("User", "UUID")
    }

}