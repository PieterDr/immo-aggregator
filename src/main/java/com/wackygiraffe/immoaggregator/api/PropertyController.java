package com.wackygiraffe.immoaggregator.api;

import com.wackygiraffe.immoaggregator.immo.Immo;
import com.wackygiraffe.immoaggregator.immo.ImmoCriteria;
import com.wackygiraffe.immoaggregator.immo.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Controller
public class PropertyController {

    private final List<Immo> immos;

    @Autowired
    public PropertyController(List<Immo> immos) {
        this.immos = immos;
    }

    @GetMapping("/properties")
    public String listProperties(Model model) {
        LinkedMultiValueMap<String, Property> properties = new LinkedMultiValueMap<>();
        immos.parallelStream()
                .map(immo -> immo.query(new ImmoCriteria()))
                .flatMap(Collection::stream)
                .forEach(property -> properties.add(property.getImmo(), property));
        model.addAttribute("immos", properties.keySet());
        model.addAttribute("properties", properties.values().stream().flatMap(List::stream).collect(toList()));
        return "properties";
    }
}
