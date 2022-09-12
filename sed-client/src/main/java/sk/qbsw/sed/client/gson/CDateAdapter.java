package sk.qbsw.sed.client.gson;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class CDateAdapter implements JsonDeserializer<Date> {
	public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		String s = json.getAsJsonPrimitive().getAsString();

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
		Date date;
		try {
			date = format.parse(s);
		} catch (ParseException e) {
			throw new JsonParseException(e.getMessage());
		}

		return date;
	}
}
