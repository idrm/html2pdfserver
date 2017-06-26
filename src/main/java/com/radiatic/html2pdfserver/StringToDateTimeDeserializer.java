package com.radiatic.html2pdfserver;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.DateTime;

import java.io.IOException;

public class StringToDateTimeDeserializer extends JsonDeserializer<DateTime> {
	@Override
	public DateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		return p.getValueAsString() != null ? new DateTime(p.getValueAsString()) : null;
	}
}
