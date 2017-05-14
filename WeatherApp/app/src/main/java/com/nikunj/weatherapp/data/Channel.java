package com.nikunj.weatherapp.data;

import org.json.JSONObject;

/**
 * Created by nnn on 8/29/2015.
 */
public class Channel implements JSONPopulator {

    private Item item;
    private Units units;
    private Atmosphere atmosphere;


    public Atmosphere getAtmosphere() {
        return atmosphere;
    }

    public Units getUnits() {
        return units;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public void populate(JSONObject data) {

        units = new Units();
        units.populate(data.optJSONObject("units"));

        atmosphere = new Atmosphere();
        atmosphere.populate(data.optJSONObject("atmosphere"));

        item = new Item();
        item.populate(data.optJSONObject("item"));

    }
}
