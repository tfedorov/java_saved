package com.tfedorov.example.SpringBoot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class DemoController {

    @Autowired
    JdbcTemplate template;

    @RequestMapping(value = "/getRandomData", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> getRandomData() {
        template.execute("INSERT INTO PERSONS (LastName,FirstName ,Address ,City) VALUES ('Fedorov', 'Taras', 'Skagen 21', 'Lviv');");
        Map<String, String> myMap = new HashMap<String, String>();
        myMap.put("a", "b");
        myMap.put("c", "d");
        return myMap;
    }
}
