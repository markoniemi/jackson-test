package org.jacksontest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

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

    @Before
    public void init() {
        jsonMapper = new ObjectMapper();
        xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JaxbAnnotationModule());
    }
    
    @Test
    public void objectWithList() throws JsonProcessingException {
        assertEquals("{\"users\":[{\"name\":\"name0\"}]}", writeUsers(new UserWrapper(createUsers(1))));
        assertEquals(1, parseUsers("{\"users\":[{\"name\":\"name0\"}]}").size());
    }
    @Test
    public void objectWithEmptyList() throws JsonProcessingException {
        assertEquals("{\"users\":[]}", writeUsers(new UserWrapper(new ArrayList<User>())));
        assertEquals(0, parseUsers("{\"users\":[]}").size());
        assertNull(parseUsers("{}"));
    }
    @Test
    public void objectWithNullList() throws JsonProcessingException {
        assertEquals("{\"users\":null}", writeUsers(new UserWrapper(null)));
        assertNull(parseUsers("{\"users\":null}"));
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
            users.add(JacksonObjectTest.createUser(i));
        }
        return users;
    }



}
