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
class Era implements Immo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Era.class);

    private static final String BASE_URL = "https://www.era.be";
    private static final String SEARCH_URL = BASE_URL + "/nl/te-koop/mechelen?page=%d";

    private final RestTemplate restTemplate;

    @Autowired
    Era(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String name() {
        return "Era";
    }

    @Override
    public List<Property> query(ImmoCriteria criteria) {
        List<Property> result = new ArrayList<>();
        int page = 1;
        Element body;
        do {
            String url = String.format(SEARCH_URL, page++);
            LOGGER.info("Retrieving properties from: " + url);
            body = Jsoup.parse(restTemplate.getForObject(url, String.class)).body();
            Elements properties = body.getElementsByClass("node-property");
            for (Element property : properties) {
                if (!property.getElementsByClass("sold").isEmpty()) continue;
                if (!property.getElementsByClass("in-option").isEmpty()) continue;
                Element imageLink = property.getElementsByClass("field-name-field-property-main-visual").get(0).getElementsByClass("field-item").get(0);
                result.add(new Property(
                        name(),
                        property.getElementsByClass("field-name-era-actuele-vraagprijs--c").get(0).getElementsByClass("field-item").get(0).text(),
                        BASE_URL + imageLink.getElementsByTag("a").get(0).attr("href"),
                        imageLink.getElementsByTag("img").get(0).attr("src")
                ));
            }
        } while (page < body.getElementsByClass("pager").get(0).getElementsByTag("li").size());
        return result;
    }
}
