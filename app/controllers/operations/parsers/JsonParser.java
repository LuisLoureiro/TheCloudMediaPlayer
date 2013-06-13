package controllers.operations.parsers;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.MappingJsonFactory;

public class JsonParser implements IParserStrategy
{
	// Read this: http://wiki.fasterxml.com/JacksonStreamingApi
	// http://wiki.fasterxml.com/JacksonInFiveMinutes#Streaming_API_Example
	public <T> T parse(Class<T> typeToParse, InputStream stream)
	{
		T value = null;
		org.codehaus.jackson.JsonParser jsonParser = null;
		try {
			JsonFactory jsonFactory = new MappingJsonFactory();
			jsonParser = jsonFactory.createJsonParser(stream); // or URL, Reader, String, byte[]
			// Read this: Also, if you happen to have an ObjectMapper, there is also ObjectMapper.getJsonFactory() that you can use to reuse factory it has
			//  (since (re)using a JsonFactory instances is one Performance Best Practices).
			if (jsonParser.nextToken() != null) { // end-of-input
				value = jsonParser.readValueAs(typeToParse);
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(jsonParser != null)
				try {
					jsonParser.close(); // important to close both parser and underlying File reader
				} catch (IOException e) {}
		}
		return value;
	}
}
