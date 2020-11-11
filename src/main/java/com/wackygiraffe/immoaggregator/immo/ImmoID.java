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

import static java.lang.Integer.parseInt;

@Component
class ImmoID implements Immo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImmoID.class);

    private static final String BASE_URL = "https://www.immoid.be";
    private static final String SEARCH_URL = BASE_URL + "/nl/te-koop?view=list&page=%d&view=list&goal=0&ptype=1&cities=MECHELEN";

    private final RestTemplate restTemplate;

    @Autowired
    ImmoID(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String name() {
        return "Immo ID";
    }

    @Override
    public List<Property> query(ImmoCriteria criteria) {
        List<Property> properties = new ArrayList<>();

        int page = 1;
        Element body;
        Elements results;
        Elements titleElements;
        do {
            String searchUrl = page(page++);
            LOGGER.info("Retrieving properties from: {}", searchUrl);
            body = Jsoup.parse(restTemplate.getForObject(searchUrl, String.class)).body();
            results = body.select(".property");
            titleElements = body.getElementsByClass("title");

            addResults(properties, results);
        } while (!titleElements.isEmpty() && page <= parseInt(titleElements.get(0).text().split(" ")[3]));
        // title element contains "pagina 1 van 3, so we take the 4th word to determine nrOfPages

        return properties;
    }

    private void addResults(List<Property> properties, Elements results) {
        for (Element property : results) {
            Element info = property.selectFirst(".property-info");
            Element image = property.selectFirst(".pic").selectFirst(".image");
            String price = info.selectFirst(".prop-price").text();
            // Price is empty when property has been sold
            if (!price.isEmpty()) {
                properties.add(new Property(
                        name(),
                        price.split(" ")[1],
                        BASE_URL + image.getElementsByTag("a").get(0).attr("href"),
                        image.getElementsByTag("img").get(0).attr("src")
                ));
            }
        }
    }

    private String page(int page) {
        return String.format(SEARCH_URL, page);
    }

}
