package org.jacksontest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class JacksonMapperTest {
    private ObjectMapper jsonMapper;
    private XmlMapper xmlMapper;

    @Before
    public void init() {
        jsonMapper = new ObjectMapper();
        xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JaxbAnnotationModule());
//        JacksonJaxbJsonProvider jsonProvider = new JacksonJaxbJsonProvider();
//        JacksonJsonProvider jsonProvider = new JacksonJsonProvider();
//        jsonProvider.setMapper(objectMapper);
    }

    @Test
    public void writeUserWrapper() throws JsonProcessingException {
        assertEquals("{\"users\":[{\"name\":\"name0\"}]}", writeUsers(new UserWrapper(createUsers(1))));
        assertEquals("{\"users\":[]}", writeUsers(new UserWrapper(createUsers(0))));
        assertEquals("{\"users\":[]}", writeUsers(new UserWrapper(new ArrayList<User>())));
        assertEquals("{\"users\":null}", writeUsers(new UserWrapper(null)));
    }


    @Test
    public void writeOwnerWrapper() throws JsonProcessingException {
        OwnerWrapper ownerWrapper = new OwnerWrapper(createOwners(2));
        String json = jsonMapper.writeValueAsString(ownerWrapper);
        log.debug("ownerWrapper: {}", json);
        String xml = xmlMapper.writeValueAsString(ownerWrapper);
        log.debug("ownerWrapper: {}", xml);
    }

    @Test
    public void parseUserWrapper() throws JsonProcessingException {
        assertEquals(1, parseUsers("{\"users\":[{\"name\":\"name0\"}]}").size());
        assertEquals(0, parseUsers("{\"users\":[]}").size());
        assertNull(parseUsers("{}"));
        assertNull(parseUsers("{\"users\":null}"));
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

    private String writeUsers(UserWrapper userWrapper) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(userWrapper);
    }
    private List<User> createUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(createUser(i));
        }
        return users;
    }

    public List<Owner> createOwners(int count) {
        List<Owner> owners = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            owners.add(createOwner(i));
        }
        return owners;
    }

    public User createUser(int i) {
        return new User("name" + i);
    }

    public Owner createOwner(int i) {
        return new Owner("owner" + i, null);
    }
}
