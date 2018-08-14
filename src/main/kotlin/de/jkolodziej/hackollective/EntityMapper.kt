package de.jkolodziej.hackollective

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.nio.file.Files
import java.nio.file.Path

data class Entity(val dir: Path?, val key: String?)
data class SerializedEntities(val entities: List<Entity>)

class EntityMapper {
    private val gson: Gson = GsonBuilder().create()

    fun readEntities(path: Path): SerializedEntities {
        return gson.fromJson(Files.newBufferedReader(path), SerializedEntities::class.java)
    }

    fun writeEntities(path: Path, entities: SerializedEntities) {

    }
}
