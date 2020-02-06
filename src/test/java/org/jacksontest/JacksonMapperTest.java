package org.jacksontest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class JacksonMapperTest {
    private ObjectMapper jsonMapper;
    private XmlMapper xmlMapper;

    @Before
    public void init() {
        jsonMapper = new ObjectMapper();
        jsonMapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JaxbAnnotationModule());
    }

    @Test
    public void writeUserWrapper() throws JsonProcessingException {
        assertEquals("{\"users\":[{\"name\":\"name0\"}]}", writeUsers(new UserWrapper(createUsers(1))));
        assertEquals("{\"users\":[]}", writeUsers(new UserWrapper(createUsers(0))));
        assertEquals("{\"users\":[]}", writeUsers(new UserWrapper(new ArrayList<User>())));
        assertEquals("{\"users\":null}", writeUsers(new UserWrapper(null)));
    }

    @Test
    @Ignore
    public void writeOwner() throws JsonProcessingException {
        assertEquals("{\"users\":[{\"name\":\"name0\"}]}", writeOwners(new OwnerWrapper(createOwners(1))));
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

    @Test
    public void writeDate() throws ParseException, JsonProcessingException {
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2020-02-04");
        assertEquals("\"2020-02-03T22:00:00.000+00:00\"", jsonMapper.writeValueAsString(date));
        LocalDate localDate = LocalDate.of(2020, 02, 04);
        assertEquals("\"2020-02-04\"", jsonMapper.writeValueAsString(localDate));
        LocalTime localTime = LocalTime.of(20, 02, 04);
        assertEquals("\"20:02:04\"", jsonMapper.writeValueAsString(localTime));
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        assertEquals("\"2020-02-04T20:02:04\"", jsonMapper.writeValueAsString(localDateTime));
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"));
        assertEquals("\"2020-02-04T20:02:04Z\"", jsonMapper.writeValueAsString(zonedDateTime));
    }

    @Test
    public void parseDate() throws ParseException, JsonProcessingException {
        Date date = jsonMapper.readValue("\"2020-02-03T22:00:00.000+00:00\"", Date.class);
        Date expectedDate = new SimpleDateFormat("yyyy-MM-dd").parse("2020-02-04");
        assertTrue(date.compareTo(expectedDate) == 0);
        LocalDate localDate = jsonMapper.readValue("\"2020-02-04\"", LocalDate.class);
        assertEquals(LocalDate.of(2020, 02, 04), localDate);
        LocalTime localTime = jsonMapper.readValue("\"20:02:04\"", LocalTime.class);
        assertEquals(LocalTime.of(20, 02, 04), localTime);
        LocalDateTime localDateTime = jsonMapper.readValue("\"2020-02-04T20:02:04\"", LocalDateTime.class);
        assertEquals(LocalDateTime.of(localDate, localTime), localDateTime);
        ZonedDateTime zonedDateTime = jsonMapper.readValue("\"2020-02-04T20:02:04Z\"", ZonedDateTime.class);
        assertEquals(ZonedDateTime.of(localDateTime, ZoneId.of("UTC")), zonedDateTime);
    }

    private List<User> parseUsers(String json) throws JsonMappingException, JsonProcessingException {
        UserWrapper userWrapper = jsonMapper.readValue(json, UserWrapper.class);
        return userWrapper.getUsers();
    }

    private String writeUsers(UserWrapper userWrapper) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(userWrapper);
    }

    private String writeOwners(OwnerWrapper ownerWrapper) throws JsonProcessingException {
        String json = jsonMapper.writeValueAsString(ownerWrapper);
        log.debug(json);
        return json;
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
        return new Owner("owner" + i, LocalDate.parse("2020-02-04"));
    }
}
