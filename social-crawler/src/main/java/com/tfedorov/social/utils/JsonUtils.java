package com.tfedorov.social.utils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ser.StdSerializerProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JsonUtils {

  private JsonUtils() {}

  private static JsonFactory jsonFactory = new JsonFactory();
  private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

  private static final String JSON_ROWS_FIELD = "rows";
  private static final String JSON_COLUMNS_FIELD = "columns";

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static JSONObject wrapTableResponse(List objects) throws JsonGenerationException, IOException, JSONException {
    JSONObject result = new JSONObject();

    StdSerializerProvider sp = new StdSerializerProvider();
    sp.setNullValueSerializer(new NullSerializer());

    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializerProvider(sp);
    String s = mapper.writeValueAsString(objects);

    List<LinkedHashMap<String, Object>> list = mapper.readValue(s, List.class);

    JSONArray columns = new JSONArray();
    JSONArray rows = new JSONArray();

    if (list.size() > 0) {
      LinkedHashMap<String, Object> o1 = list.get(0);

      for (Object key : o1.keySet().toArray()) {
        columns.put(key);
      }

      for (LinkedHashMap<String, Object> o2 : list) {

        JSONArray row = new JSONArray();
        for (Object value : o2.values()) {
          row.put(value);

        }
        rows.put(row);
      }
    }

    result.put(JSON_COLUMNS_FIELD, columns).put(JSON_ROWS_FIELD, rows);

    return result;

  }

  /**
   * Calling this method with parameters
   * 
   * @param json = { "name" : { "first" : "Joe", "last" : "Sixpack" }, "gender" : "MALE", "verified"
   *        : false, "userImage" : "Rm9vYmFyIQ==" }
   * @param subObject = "name"
   * @param field = "last" Will retun @return value = 'Sixpack', any null values in parameters
   *        caused null in return
   * 
   * @param json - String where extracting data
   * @param subObject - sub object (fist level )in json
   * @param field - field in subObject
   * @return
   */
  public static String getFieldFromSubObject(String json, String subObject, String field) {

    if (json == null || subObject == null || field == null) {
      logger.error("Improper Null value = " + json + ", subObject = " + subObject + ", field = "
          + field);
      return null;
    }
    try {
      JsonParser parser = jsonFactory.createJsonParser(json);

      while (parser.nextToken() != null) {

        if (parser.getCurrentToken() == JsonToken.FIELD_NAME
            && field.equalsIgnoreCase(parser.getText())
            && subObject.equalsIgnoreCase(parser.getParsingContext().getParent().getCurrentName())) {
          parser.getCurrentToken().asString();
          parser.nextToken();
          return parser.getText();
        }

      }
    } catch (JsonParseException e) {
      logger.error("Json parser error for json = " + json + ", subObject = " + subObject
          + ", field = " + field, e);
    } catch (IOException e) {
      logger.error("IOException for json = " + json + ", subObject = " + subObject + ", field = "
          + field, e);
    }
    return null;
  }

  /**
   * Calling this method with parameters
   * 
   * @param json = { "name" : { "first" : "Joe", "last" : "Sixpack" }, "gender" : "MALE", "verified"
   *        : false, "userImage" : "Rm9vYmFyIQ==" }
   * @param field = "userImage" Will retun @return value = 'Rm9vYmFyIQ==', any null values in
   *        parameters caused null in return
   * 
   * @param json - String where extracting data
   * @param field - field in subObject
   * @return
   */
  public static String getInFirstChildField(String json, String field) {

    if (json == null || field == null) {
      logger.error("Improper Null value = " + json + ", field = " + field);
      return null;
    }
    try {
      JsonParser parser = jsonFactory.createJsonParser(json);

      while (parser.nextToken() != null) {

        if (parser.getCurrentToken() == JsonToken.FIELD_NAME
            && field.equalsIgnoreCase(parser.getText())) {

          if (parser.getParsingContext().getParent().inRoot()) {
            parser.getCurrentToken().asString();
            parser.nextToken();
            return parser.getText();
          }
        }

      }
    } catch (JsonParseException e) {
      logger.error("Json parser error for json = " + json + ", field = " + field, e);
    } catch (IOException e) {
      logger.error("IOException for json = " + json + ", field = " + field, e);
    }
    return null;
  }


}
