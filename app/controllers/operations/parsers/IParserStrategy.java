package controllers.operations.parsers;

import java.io.InputStream;

public interface IParserStrategy
{
	public <T> T parse(Class<T> typeToParse, InputStream stream);
}
