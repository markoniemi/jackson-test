package org.jacksontest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JacksonDateTest {
    private ObjectMapper jsonMapper;
    private LocalDate expectedLocalDate = LocalDate.of(2020, 02, 04);
    private LocalTime expectedLocalTime = LocalTime.of(20, 02, 04);
    private LocalDateTime expectedLocalDateTime = LocalDateTime.of(expectedLocalDate, expectedLocalTime);
    private ZonedDateTime expectedZonedDateTime = ZonedDateTime.of(expectedLocalDateTime, ZoneId.of("UTC"));

    @BeforeEach
    public void init() {
        jsonMapper = new ObjectMapper();
        jsonMapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    public void date() throws ParseException, JsonProcessingException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date expectedDate = simpleDateFormat.parse("2020-02-04");
        assertEquals("\"2020-02-04T00:00:00.000+00:00\"", jsonMapper.writeValueAsString(expectedDate));
        Date date = jsonMapper.readValue("\"2020-02-04T00:00:00.000+00:00\"", Date.class);
        assertTrue(date.compareTo(expectedDate) == 0);
    }

    @Test
    public void localDate() throws ParseException, JsonProcessingException {
        assertEquals("\"2020-02-04\"", jsonMapper.writeValueAsString(expectedLocalDate));
        LocalDate localDate = jsonMapper.readValue("\"2020-02-04\"", LocalDate.class);
        assertTrue(expectedLocalDate.equals(localDate));
    }

    @Test
    public void localTime() throws ParseException, JsonProcessingException {
        assertEquals("\"20:02:04\"", jsonMapper.writeValueAsString(expectedLocalTime));
        LocalTime localTime = jsonMapper.readValue("\"20:02:04\"", LocalTime.class);
        assertEquals(expectedLocalTime, localTime);
    }

    @Test
    public void localDateTime() throws ParseException, JsonProcessingException {
        assertEquals("\"2020-02-04T20:02:04\"", jsonMapper.writeValueAsString(expectedLocalDateTime));
        LocalDateTime localDateTime = jsonMapper.readValue("\"2020-02-04T20:02:04\"", LocalDateTime.class);
        assertEquals(LocalDateTime.of(expectedLocalDate, expectedLocalTime), localDateTime);
    }

    @Test
    public void zonedDateTime() throws ParseException, JsonProcessingException {
        assertEquals("\"2020-02-04T20:02:04Z\"", jsonMapper.writeValueAsString(expectedZonedDateTime));
        ZonedDateTime zonedDateTime = jsonMapper.readValue("\"2020-02-04T20:02:04Z\"", ZonedDateTime.class);
        assertEquals(ZonedDateTime.of(expectedLocalDateTime, ZoneId.of("UTC")), zonedDateTime);
    }
}
