package com.seven10.update_guy.common;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonFactory
{
	public static class PathConverter implements JsonDeserializer<Path>, JsonSerializer<Path>
	{
		@Override
		public Path deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
				throws JsonParseException
		{
			return Paths.get(jsonElement.getAsString());
		}
		
		@Override
		public JsonElement serialize(Path path, Type type, JsonSerializationContext jsonSerializationContext)
		{
			return new JsonPrimitive(path.toString());
		}
	}
	public static final String encodingType = "UTF-8";
	
	public static Gson getGson()
	{
		GsonBuilder builder = new GsonBuilder()
			.registerTypeHierarchyAdapter(Path.class, new PathConverter())	// fix an issue with deserializing Path objects
			.setDateFormat("yyyy-MM-dd HH:mm:ss.S")							// fixes an issue with deserializing dates
			.setPrettyPrinting();											// enable the json to be readable
		return builder.create();
	}
	
	public static JsonObject createJsonFromString(String keyName, String value)
	{
		JsonParser parser = new JsonParser();
		String json = String.format("{\"%s\": \"%s\"}", keyName, value);
		JsonObject jsonObj = parser.parse(json).getAsJsonObject();
		return jsonObj;
	}

}
