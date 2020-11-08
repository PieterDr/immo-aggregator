package com.wackygiraffe.immoaggregator.immo;

import java.util.List;

public interface Immo {

    List<Property> query(ImmoCriteria criteria);
}
