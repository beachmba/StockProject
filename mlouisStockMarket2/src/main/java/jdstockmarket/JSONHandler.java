package jdstockmarket;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The {@code JSONHandler} interface defines a contract for handling JSON data. It declares methods
 * for parsing a JSON string and retrieving a value associated with a specified key from a
 * {@code JSONObject}.
 *
 * <p>This interface abstracts the JSON data handling operations, allowing different implementations
 * to provide specific JSON handling strategies. Implementing classes are expected to provide
 * concrete implementations for parsing JSON data and retrieving values from JSON objects.
 *
 * @author David Martindale
 * @author Jamshaid Ali
 * @version 2.0 (7 December 2023)
 * @see JSONObject
 */

public interface JSONHandler {
	
    /**
     * Parses a JSON string and returns a {@code JSONObject}.
     *
     * @param jsonData The JSON string to be parsed.
     * @return A {@code JSONObject} representing the parsed JSON data.
     */
    abstract JSONObject parseJSON(String jsonData);
    
    abstract JSONArray parseJSONArray(String jsonData);

    /**
     * Retrieves a value associated with a specified key from a {@code JSONObject}.
     *
     * @param jsonObject The {@code JSONObject} from which to retrieve the value.
     * @param key The key whose associated value is to be returned.
     * @return A {@code JSONObject} representing the value associated with the specified key.
     */
    abstract JSONObject getValue(JSONObject jsonObject, String key);
}