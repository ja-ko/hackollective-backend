package de.jkolodziej.hackollective

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.matchers.file.shouldBeADirectory
import io.kotlintest.matchers.file.shouldBeAFile
import io.kotlintest.matchers.file.shouldBeNonEmptyDirectory
import io.kotlintest.matchers.file.shouldNotExist
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.should
import io.kotlintest.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

internal class EntityMapperTest {

    private var mappingDirectory: Path
    private var mappingFile: Path
    private var systemUnderTest: EntityMapper

    init { // TODO: figure out how to avoid this duplication without making the vars nullable
        mappingDirectory = Files.createTempDirectory("test-mapping")
        mappingFile = mappingDirectory.resolve("mapping.json")
        systemUnderTest = EntityMapper(mappingFile)
    }

    @BeforeEach
    fun setupTestDirectory() {
        mappingDirectory = Files.createTempDirectory("test-mapping")
        mappingFile = mappingDirectory.resolve("mapping.json")
        systemUnderTest = EntityMapper(mappingFile)
    }

    @Test
    fun canWriteData() {
        mappingDirectory.shouldBeADirectory()
        mappingDirectory should beEmptyDirectory()
        mappingFile.shouldNotExist()

        systemUnderTest.writeEntities()

        mappingDirectory.shouldBeNonEmptyDirectory()
        mappingFile.shouldBeAFile()
        readMapping().shouldBe("[]")

        systemUnderTest.entities += EntityMapper.Entity(mappingDirectory.resolve("some-directory"), "asd")
        systemUnderTest.writeEntities()

        val content = readMapping()
        content.shouldContain("some-directory")
        content.shouldContain("asd")

        systemUnderTest.readEntities()[0].key.shouldBe("asd")
    }

    @Test
    fun canUpdateMappingOnFsChange() {
        mappingDirectory.shouldBeADirectory()
        mappingDirectory should beEmptyDirectory()

        val entityDirectory = mappingDirectory.resolve("entity")
        Files.createDirectory(entityDirectory)

        systemUnderTest.checkForMissingMappings().shouldBeTrue()
        systemUnderTest.entities.shouldHaveSize(1)
        val directory = systemUnderTest.entities[0].dir!!
        val key = systemUnderTest.entities[0].key!!
        directory.shouldBeADirectory()
        directory.endsWith("entity").shouldBeTrue()
        key.length.shouldBeGreaterThan(10)

        systemUnderTest.writeEntities()

        val content = readMapping()
        content.shouldContain("entity")
        content.shouldContain(key)
    }

    private fun readMapping() = String(Files.readAllBytes(mappingFile), StandardCharsets.UTF_8)

    private fun beEmptyDirectory(): Matcher<Path> = object : Matcher<Path> {
        override fun test(value: Path): Result = Result(value.toFile().isDirectory && value.toFile().list().isEmpty(), "$value should be an empty directory", "$value should not be an empty directory")
    }
}