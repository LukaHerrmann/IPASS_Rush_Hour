package com.example.application.objects;

import com.example.application.R;
import com.example.application.objects.Vehicle;

import java.util.HashMap;
import java.util.Map;

public class Vehicles {

    private static Map<Character, Vehicle> vehicles = new HashMap<Character, Vehicle>();

    public static Map<Character, Vehicle> getVehicles() {
        vehicles.put('x', new Vehicle('x', R.color.red, 2, true));
        vehicles.put('a', new Vehicle('a', R.color.dark_green, 2, false));
        vehicles.put('b', new Vehicle('b', R.color.ocean_green, 2, false));
        vehicles.put('c', new Vehicle('c', R.color.light_yellow, 2, false));
        vehicles.put('d', new Vehicle('d', R.color.pink, 2, false));
        vehicles.put('e', new Vehicle('e', R.color.dark_gray, 2, false));
        vehicles.put('f', new Vehicle('f', R.color.violet, 2, false));
        vehicles.put('g', new Vehicle('g', R.color.light_green, 2, false));
        vehicles.put('h', new Vehicle('h', R.color.light_blue, 2, false));
        vehicles.put('i', new Vehicle('i', R.color.orange, 2, false));
        vehicles.put('j', new Vehicle('j', R.color.brown, 2, false));
        vehicles.put('k', new Vehicle('k', R.color.beige, 2, false));
        vehicles.put('1', new Vehicle('1', R.color.lila, 3, false));
        vehicles.put('2', new Vehicle('2', R.color.yellow, 3, false));
        vehicles.put('3', new Vehicle('3', R.color.blue, 3, false));
        vehicles.put('4', new Vehicle('4', R.color.ocean_blue, 3, false));
        return vehicles;
    }
}
