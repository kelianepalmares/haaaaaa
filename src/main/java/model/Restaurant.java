package model;

import java.util.ArrayList;

public class Restaurant {

    private int id;
    private String name;
    private String location;
    private ArrayList<MenuItem> menuItems;

    public Restaurant(int id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.menuItems = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public ArrayList<MenuItem> getMenuItems() { return menuItems; }
    public void setMenuItems(ArrayList<MenuItem> menuItems) { this.menuItems = menuItems; }

    public void addMenuItem(MenuItem item) {
        menuItems.add(item);
    }

    @Override
    public String toString() {
        return name + " (" + location + ")";
    }
}