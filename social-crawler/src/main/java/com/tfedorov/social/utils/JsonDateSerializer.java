package com.tfedorov.social.utils;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.joda.time.base.BaseDateTime;
import org.springframework.stereotype.Component;

import com.tfedorov.social.utils.date.DateUtils;

@Component
public class JsonDateSerializer extends JsonSerializer<Object> {

	@Override
	public void serialize(Object date, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
		if (date == null) {
			gen.writeString("");

		}else if (date instanceof BaseDateTime) {
			 gen.writeString(DateUtils.printDate((BaseDateTime)date));			
		} else {
			gen.writeString("");
		}
	}
}
