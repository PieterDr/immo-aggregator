package com.wackygiraffe.immoaggregator.immo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
class CarlMarien implements Immo {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarlMarien.class);

    public static final String BASE_URL = "https://www.carlmarien.be";
    public static final String SEARCH_URL = BASE_URL + "/nl/te-koop?view=list&page=1&ptype=1&cities=MECHELEN";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    CarlMarien(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() throws JsonProcessingException {
        List<Property> properties = query(new ImmoCriteria());
        for (Property property : properties) {
            LOGGER.info(objectMapper.writeValueAsString(property));
        }
    }

    @Override
    public List<Property> query(ImmoCriteria criteria) {
        List<Property> properties = new ArrayList<>();

        Element body = Jsoup.parse(restTemplate.getForObject(SEARCH_URL, String.class)).body();
        Elements results = body.select(".property");
        for (Element property : results) {
            Element info = property.selectFirst(".property-info");
            Element image = property.selectFirst(".pic").selectFirst(".image");
            String price = info.selectFirst(".prop-price").text();
            if (!price.isEmpty()) {
                properties.add(new Property(
                        price.split(" ")[1],
                        BASE_URL + image.getAllElements().get(1).attr("href"),
                        image.getAllElements().get(2).attr("src")
                ));
            }
        }

        return properties;
    }
}
