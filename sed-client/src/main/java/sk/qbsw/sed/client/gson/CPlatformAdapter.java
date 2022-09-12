package sk.qbsw.sed.client.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import sk.qbsw.sed.client.model.EPlatform;

/**
 * Adapter serializes and deserializes type {@link EPlatform} to/from json.
 *
 * @author Podmajersky Lukas
 * @since 2.0.0
 * @version 2.0.0
 */
public class CPlatformAdapter implements JsonSerializer<EPlatform>, JsonDeserializer<EPlatform> {
	/*
	 * (non-Javadoc)
	 *
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
	 * java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(EPlatform src, Type srcType, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString().toLowerCase());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
	 * java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
	 */
	@Override
	public EPlatform deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		return EPlatform.valueOf(json.getAsString().toUpperCase());
	}
}
