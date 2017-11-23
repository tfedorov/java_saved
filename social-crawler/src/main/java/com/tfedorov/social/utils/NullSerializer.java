package com.tfedorov.social.utils;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.springframework.stereotype.Component;

@Component
public class NullSerializer extends JsonSerializer<Object> {

  @Override
  public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException, JsonProcessingException {
    if (value == null) {
      jgen.writeString("");
    } else {
      jgen.writeString(value.toString());
    }
  }
}
