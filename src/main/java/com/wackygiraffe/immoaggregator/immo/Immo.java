package com.wackygiraffe.immoaggregator.immo;

import java.util.List;

public interface Immo {

    String name();
    List<Property> query(ImmoCriteria criteria);
}
