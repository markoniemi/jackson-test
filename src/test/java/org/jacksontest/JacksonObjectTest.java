package org.jacksontest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

public class JacksonObjectTest {
    private ObjectMapper jsonMapper;
    private XmlMapper xmlMapper;

    @Before
    public void init() {
        jsonMapper = new ObjectMapper();
        xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JaxbAnnotationModule());
    }

    @Test
    public void object() throws JsonProcessingException {
        User user = new User("name");
        String json = "{\"name\":\"name\"}";
        assertEquals(json, jsonMapper.writeValueAsString(user));
        assertEquals(user, jsonMapper.readValue(json, User.class));
    }

    @Test
    public void extendingObject() throws JsonProcessingException {
        Owner owner = new Owner("name", null);
        String json = "{\"name\":\"name\",\"id\":null}";
        assertEquals(json, jsonMapper.writeValueAsString(owner));
        assertEquals(owner, jsonMapper.readValue(json, Owner.class));
    }

    @Test
    public void singleQuotes() throws JsonMappingException, JsonProcessingException {
        jsonMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        assertEquals(1, parseUsers("{'users':[{'name':'name0'}]}").size());
        assertEquals(0, parseUsers("{'users':[]}").size());
    }

    @Test
    public void unquotedFieldNames() throws JsonMappingException, JsonProcessingException {
        jsonMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        jsonMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        assertEquals(1, parseUsers("{users:[{name:'name0'}]}").size());
        assertEquals(0, parseUsers("{users:[]}").size());
    }

    private List<User> parseUsers(String json) throws JsonMappingException, JsonProcessingException {
        UserWrapper userWrapper = jsonMapper.readValue(json, UserWrapper.class);
        return userWrapper.getUsers();
    }

    public static User createUser(int i) {
        return new User("name" + i);
    }

    public static Owner createOwner(int i) {
        return new Owner("owner" + i, Long.valueOf(i));
    }
}
