package com.wackygiraffe.immoaggregator.api;

import com.wackygiraffe.immoaggregator.immo.Immo;
import com.wackygiraffe.immoaggregator.immo.ImmoCriteria;
import com.wackygiraffe.immoaggregator.immo.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PropertyController {

    private final List<Immo> immos;

    @Autowired
    public PropertyController(List<Immo> immos) {
        this.immos = immos;
    }

    @GetMapping("/properties")
    public String listProperties(Model model) {
        List<Property> properties = immos.parallelStream()
                .map(immo -> immo.query(new ImmoCriteria()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        model.addAttribute("properties", properties);
        return "properties";
    }
}
