package sk.qbsw.sed.client.gson;

import java.lang.reflect.Type;
import java.util.Calendar;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CCalendarAdapter implements JsonSerializer<Calendar>, JsonDeserializer<Calendar> {

	@Override
	public JsonElement serialize(Calendar src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.getTimeInMillis());
	}

	@Override
	public Calendar deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(json.getAsJsonPrimitive().getAsLong());
		return cal;
	}
}
