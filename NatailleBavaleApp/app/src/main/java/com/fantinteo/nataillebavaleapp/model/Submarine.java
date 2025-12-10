package com.fantinteo.nataillebavaleapp.model;

import java.util.ArrayList;

public class Submarine extends Ship {
    public static final int SUBMARINE_SIZE = 1;
    public Submarine(ShipElement element) {
        super(SUBMARINE_SIZE);
        this.elements = new ArrayList<>();
        this.elements.add(element);
    }
    @Override
    public String getName() {
        return "Sous-marin";
    }
}