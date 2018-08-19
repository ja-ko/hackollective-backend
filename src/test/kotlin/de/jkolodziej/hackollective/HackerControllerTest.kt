package de.jkolodziej.hackollective

import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.matchers.string.shouldNotContain
import io.kotlintest.should
import io.kotlintest.shouldNot
import io.ktor.http.HttpMethod
import io.ktor.server.testing.handleRequest
import org.junit.jupiter.api.Test
import java.net.URLEncoder

internal class HackerControllerTest : ControllerTest() {

    @Test
    fun testGetHackerItem(): Unit = withConfiguredTestApplication {
        with(handleRequest(HttpMethod.Get, "/hacker") {
            addAuthentication()
        }) {
            requestHandled.shouldBeTrue()
            response.status() should indicateSuccess()
            response.content.shouldContain("WH")
            response.content.shouldContain("BH")
        }

        with(handleRequest(HttpMethod.Get, "/hacker/WH") {
            addAuthentication()
        }) {
            requestHandled.shouldBeTrue()
            response.status() should indicateSuccess()
            response.content.shouldContain("item.json")
        }
        with(handleRequest(HttpMethod.Get, "/hacker/WH/item.json") {
            addAuthentication()
        }) {
            requestHandled.shouldBeTrue()
            response.status() should indicateSuccess()
            response.content.shouldContain("key\": \"some-random-words")
        }
    }

    @Test
    fun testUnauthorized(): Unit = withConfiguredTestApplication {
        with(handleRequest(HttpMethod.Get, "/hacker") {
            addHeader("Authorization", "some-value")
            addHeader("User", "UUID")
        }) {
            requestHandled.shouldBeTrue()
            response.status() shouldNot indicateSuccess()
            response.content.shouldNotContain("WH")
        }
        with(handleRequest(HttpMethod.Get, "/hacker/WH/item.json") {
            addHeader("Authorization", "some-value")
            addHeader("User", "UUID")
        }) {
            requestHandled.shouldBeTrue()
            response.status() shouldNot indicateSuccess()
            response.content.shouldNotContain("key")
        }
    }

    @Test
    fun testDirectoryTraversalListing(): Unit = withConfiguredTestApplication {
        val path = "../entities/"
        with(handleRequest(HttpMethod.Get, "/hacker/$path") {
            addAuthentication()
        }) {
            response.status() shouldNot indicateSuccess()
            response.content.shouldNotContain("json")
        }
        with(handleRequest(HttpMethod.Get, "/hacker/${URLEncoder.encode(path, "UTF-8")}") {
            addAuthentication()
        }) {
            println(response.content)
            response.status() shouldNot indicateSuccess()
            response.content.shouldNotContain("json")
        }
    }

    @Test
    fun testDirectoryTraversalFileContent(): Unit = withConfiguredTestApplication {
        val path = "../entities/mapping.json"
        with(handleRequest(HttpMethod.Get, "/hacker/$path") {
            addAuthentication()
        }) {
            response.status() shouldNot indicateSuccess()
            response.content.shouldNotContain("json")
        }
        with(handleRequest(HttpMethod.Get, "/hacker/${URLEncoder.encode(path, "UTF-8")}") {
            addAuthentication()
        }) {
            println(response.content)
            response.status() shouldNot indicateSuccess()
            response.content.shouldNotContain("key")
        }
    }


}