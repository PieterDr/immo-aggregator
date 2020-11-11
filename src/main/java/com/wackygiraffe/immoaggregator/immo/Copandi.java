package com.wackygiraffe.immoaggregator.immo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@Component
class Copandi implements Immo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Copandi.class);

    private static final String BASE_URL = "https://copandi.be";
    private static final String RESULT_BASE_URL = "https://copandi.be/resultaten";
    private static final String SEARCH_URL = BASE_URL + "/api/estates?search=Mechelen&hideUnits=true&saleOrLet=sale&verkochtOptie=true&page=%d&perPage=%d";

    private final RestTemplate restTemplate;

    @Autowired
    Copandi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String name() {
        return "Copandi";
    }

    @Override
    public List<Property> query(ImmoCriteria criteria) {
        List<Property> properties = new ArrayList<>();
        int page = 1;
        Response response;
        do {
            String url = String.format(SEARCH_URL, page++, 100);
            LOGGER.info("Retrieving properties from: {}", url);
            response = restTemplate.getForObject(url, Response.class);

            response.getData().stream()
                    .filter(PropertyDto::isAvailable)
                    .filter(property -> property.price != null)
                    .map(property -> new Property(
                            name(),
                            property.getPrice(),
                            RESULT_BASE_URL + property.getPath(),
                            BASE_URL + property.getImage()
                    ))
                    .forEach(properties::add);
        } while (response.getNextPageUrl() != null);
        return properties;
    }

    private static class Response {

        private String nextPageUrl;
        private List<PropertyDto> data;

        @JsonCreator
        private Response(@JsonProperty("next_page_url") String nextPageUrl, @JsonProperty("data") List<PropertyDto> data) {
            this.nextPageUrl = nextPageUrl;
            this.data = data;
        }

        public String getNextPageUrl() {
            return nextPageUrl;
        }

        public List<PropertyDto> getData() {
            return data;
        }
    }

    private static class PropertyDto {

        private final static NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();

        private final String price;
        private final String status;
        private final String path;
        private final String image;

        @JsonCreator
        private PropertyDto(@JsonProperty("price_amount") String price,
                            @JsonProperty("status") String status,
                            @JsonProperty("slug") String path,
                            @JsonProperty("head_image") String image) {
            this.price = price;
            this.status = status;
            this.path = path;
            this.image = image;
        }

        public String getPrice() {
            return NUMBER_FORMAT.format(Integer.parseInt(price)).replaceAll(",", ".");
        }

        public String getPath() {
            return path;
        }

        public String getImage() {
            return image;
        }

        public boolean isAvailable() {
            return status.equalsIgnoreCase("available");
        }

    }
}
