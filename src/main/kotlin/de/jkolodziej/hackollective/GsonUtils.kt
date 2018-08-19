package de.jkolodziej.hackollective

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.io.Reader
import java.lang.reflect.Type
import java.nio.file.Path
import java.nio.file.Paths


fun configureGson(builder: GsonBuilder): GsonBuilder {
    builder.registerTypeHierarchyAdapter(Path::class.java, PathConverter())
    return builder
}

inline fun <reified T> Gson.fromJson(json: String): T? = this.fromJson<T>(json, object : TypeToken<T>() {}.type)
inline fun <reified T> Gson.fromJson(json: Reader): T? = this.fromJson<T>(json, object : TypeToken<T>() {}.type)


class PathConverter : JsonDeserializer<Path>, JsonSerializer<Path> {
    @Throws(JsonParseException::class)
    override fun deserialize(jsonElement: JsonElement, type: Type, jsonDeserializationContext: JsonDeserializationContext): Path {
        return Paths.get(jsonElement.asString)
    }

    override fun serialize(path: Path, type: Type, jsonSerializationContext: JsonSerializationContext): JsonElement {
        return JsonPrimitive(path.toString())
    }
}