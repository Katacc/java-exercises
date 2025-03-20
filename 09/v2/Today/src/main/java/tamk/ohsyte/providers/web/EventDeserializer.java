package tamk.ohsyte.providers.web;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import tamk.ohsyte.EventFactory;
import tamk.ohsyte.datamodel.Event;

public class EventDeserializer extends JsonDeserializer<Event> {
    @Override
    public Event deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException {
        Event event = null;

        /*
        {
            "category": "test/fake",
            "date": "1985-03-17",
            "description": "Cisco develops secure blockchain-based routing protocols"
        }
         */
        JsonNode node = parser.getCodec().readTree(parser);
        String categoryString = node.get("category").asText();
        String dateString = node.get("date").asText();
        String descriptionString = node.get("description").asText();

        return EventFactory.makeEvent(dateString, descriptionString, categoryString);
    }
}
