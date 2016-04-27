package io.advantageous.config;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static io.advantageous.boon.core.Maps.map;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class ConfigImplTest {


    Map map;
    Config config;

    @Before
    public void setUp() throws Exception {


        map = map("int1", 1,
                "float1", 1.0,
                "double1", 1.0,
                "long1", 1L,
                "string1", "rick",
                "stringList", asList("Foo", "Bar"),
                "configInner", map(
                        "int2", 2,
                        "float2", 2.0
                ),
                "uri", URI.create("http://localhost:8080/foo"),
                "employee", map("id", 123, "name", "Geoff"),
                "employees", asList(
                        map("id", 123, "name", "Geoff"),
                        map("id", 456, "name", "Rick"),
                        map("id", 789, "name", "Paul")
                )
        );
        config = new ConfigImpl(map);
    }

    @Test
    public void testSimple() throws Exception {

        assertEquals(URI.create("http://localhost:8080/foo"), config.get("uri", URI.class));
        assertEquals(1, config.getInt("int1"));
        assertEquals(asList("Foo", "Bar"), config.getStringList("stringList"));
        assertEquals("rick", config.getString("string1"));
        assertEquals(1.0, config.getDouble("double1"), 0.001);
        assertEquals(1L, config.getLong("long1"));
        assertEquals(1.0f, config.getFloat("float1"), 0.001);
        config.toString();
    }

    @Test
    public void testReadClass() throws Exception {
        final Employee employee = config.get("employee", Employee.class);
        assertEquals("Geoff", employee.name);
        assertEquals("123", employee.id);
    }


    @Test
    public void testReadListOfClass() throws Exception {
        final List<Employee> employees = config.getList("employees", Employee.class);
        assertEquals("Geoff", employees.get(0).name);
        assertEquals("123", employees.get(0).id);
    }

    @Test
    public void testSimplePath() throws Exception {

        assertTrue(config.hasPath("configInner.int2"));
        assertFalse(config.hasPath("configInner.foo.bar"));
        assertEquals(2, config.getInt("configInner.int2"));
        assertEquals(2.0f, config.getFloat("configInner.float2"), 0.001);
    }

    @Test
    public void testGetConfig() throws Exception {
        final Config configInner = config.getConfig("configInner");
        assertEquals(2, configInner.getInt("int2"));
        assertEquals(2.0f, configInner.getFloat("float2"), 0.001);
    }

    @Test
    public void testGetMap() throws Exception {
        final Map<String, Object> map = config.getMap("configInner");
        assertEquals(2, (int) map.get("int2"));
        assertEquals(2.0f, (double) map.get("float2"), 0.001);
    }

    @Test(expected = java.lang.IllegalArgumentException.class)
    public void testNoPath() throws Exception {
        config.getInt("department.employees");
    }

    public static class Employee {
        private String id;
        private String name;
    }
}