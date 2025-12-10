package com.fantinteo.nataillebavaleapp.model;

import java.util.List;

public class Cruiser extends Ship {
    public static final int CRUISER_SIZE = 3;
    public Cruiser(List<ShipElement> elements) {
        super(CRUISER_SIZE);
        this.elements = elements;
    }
    @Override
    public String getName() {
        return "Croiseur";
    }
}