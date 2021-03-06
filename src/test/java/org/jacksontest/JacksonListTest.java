package org.jacksontest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class JacksonListTest {
    private ObjectMapper jsonMapper;
    private XmlMapper xmlMapper;

    @BeforeEach
    public void init() {
        jsonMapper = new ObjectMapper();
        xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JaxbAnnotationModule());
    }

    @Test
    public void objectWithList() throws JsonProcessingException {
        String json = "{\"users\":[{\"name\":\"name0\"}]}";
        UserWrapper userWrapper = new UserWrapper(createUsers(1));
        assertEquals(json, writeUsers(userWrapper));
        assertEquals(userWrapper, parseUserWrapper(json));
    }

    @Test
    public void objectWithEmptyList() throws JsonProcessingException {
        String json = "{\"users\":[]}";
        UserWrapper userWrapper = new UserWrapper(new ArrayList<User>());
        assertEquals(json, writeUsers(userWrapper));
        assertEquals(userWrapper, parseUserWrapper(json));
    }

    @Test
    public void objectWithNullList() throws JsonProcessingException {
        String json = "{\"users\":null}";
        assertEquals(json, writeUsers(new UserWrapper(null)));
        assertNull(parseUserWrapper(json).getUsers());
        assertNull(parseUserWrapper("{}").getUsers());
    }

    private UserWrapper parseUserWrapper(String json) throws JsonMappingException, JsonProcessingException {
        UserWrapper userWrapper = jsonMapper.readValue(json, UserWrapper.class);
        return userWrapper;
    }

    private String writeUsers(UserWrapper userWrapper) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(userWrapper);
    }

    private List<User> createUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(JacksonObjectTest.createUser(i));
        }
        return users;
    }

}
