package org.shino.model.adapter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class DescriptionSerializer extends JsonSerializer<String> {

  @Override
  public void serialize(String description, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    String formattedDescription = description.replaceAll("\\\\n", "\n");
    SerializedString string = new SerializedString(description);
    jsonGenerator.writeString(string);
  }
}
