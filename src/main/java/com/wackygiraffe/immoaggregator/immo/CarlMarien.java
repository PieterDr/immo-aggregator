package com.wackygiraffe.immoaggregator.immo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
class CarlMarien implements Immo {

    public static final String BASE_URL = "https://www.carlmarien.be";
    public static final String SEARCH_URL = BASE_URL + "/nl/te-koop?view=list&page=1&ptype=1&cities=MECHELEN";

    private final RestTemplate restTemplate;

    @Autowired
    CarlMarien(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
