package com.edu.securoserve.securoserve;

import com.edu.securoserve.securoserve.rest.RestClient;

import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void generateUrl() throws Exception {

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

        map.add("test1", 1);
        map.add("test2", 2);
        map.add("test3", 3);

        RestClient rc = new RestClient();
        String output = rc.generateUrl("http://localhost:8080", map);

        // Order is not sorted because it's a hashmap, we expect the order 1, 2, 3.
        assertEquals("http://localhost:8080?test1=1&test2=2&test3=3", output);
    }
}