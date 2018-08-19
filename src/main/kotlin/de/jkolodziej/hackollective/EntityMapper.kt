package de.jkolodziej.hackollective

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption


class EntityMapper(private val mappingFile: Path) {
    data class Entity(var dir: Path?, val key: String?)

    companion object {
        val gson: Gson = {
            configureGson(GsonBuilder()).create()
        }.invoke()
    }

    var entities: List<Entity>
    val mappingPath: Path = mappingFile.parent

    init {
        entities = readEntities()
    }

    fun readEntities(): List<Entity> {
        if (Files.exists(mappingFile)) {
            return gson.fromJson<List<Entity>?>(Files.newBufferedReader(mappingFile))?.map {
                Entity(mappingPath.resolve(it.dir), it.key)
            }.orEmpty()
        } else {
            return emptyList()
        }
    }

    fun writeEntities() {
        Files.newBufferedWriter(mappingFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use {
            gson.toJson(entities.map { Entity(mappingPath.relativize(it.dir), it.key) }, it)
        }
    }

    fun resolveEntityPathForKey(key: String): Entity? {
        return entities.find { it.key == key }
    }

    private fun addEntity(name: String, key: String) {
        entities += Entity(mappingPath.resolve(name), key)
    }

    fun checkForMissingMappings(): Boolean {
        var result = false
        Files.newDirectoryStream(mappingPath).use { directory ->
            directory.filter { Files.isDirectory(it) }.filter { !entities.filter { entity -> entity.dir == it }.any() }.forEach {
                result = true
                addEntity(it.toString(), KeyGenerator.createKey(4))
            }
        }
        return result
    }
}
