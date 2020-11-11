package com.wackygiraffe.immoaggregator.immo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
class CarlMarien implements Immo {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarlMarien.class);

    private static final String BASE_URL = "https://www.carlmarien.be";
    private static final String SEARCH_URL = BASE_URL + "/nl/te-koop?view=list&page=1&ptype=1&cities=MECHELEN";

    private final RestTemplate restTemplate;

    @Autowired
    CarlMarien(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Property> query(ImmoCriteria criteria) {
        List<Property> properties = new ArrayList<>();

        LOGGER.info("Retrieving properties from: {}", SEARCH_URL);
        Element body = Jsoup.parse(restTemplate.getForObject(SEARCH_URL, String.class)).body();
        Elements results = body.select(".property");
        for (Element property : results) {
            Element image = property.selectFirst(".image");
            Element info = property.selectFirst(".property-info");
            String price = info.selectFirst(".prop-price").text();
            // Price is empty when property has been sold
            if (!price.isEmpty()) {
                properties.add(new Property(
                        "Carl Mariën",
                        price.split(" ")[1],
                        BASE_URL + image.getElementsByTag("a").get(0).attr("href"),
                        image.getElementsByTag("img").get(0).attr("src")
                ));
            }
        }

        return properties;
    }
}
