package structures;

import java.util.ArrayList;
import model.Restaurant;

public class RestaurantHashTable {

    private ArrayList<ArrayList<Restaurant>> buckets;
    private int capacity;

    public RestaurantHashTable(int capacity) {
        this.capacity = capacity;
        buckets = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            buckets.add(new ArrayList<Restaurant>());
        }
    }

    // Hash function: batay sa location (hindi case-sensitive)
    private int hash(String location) {
        return (location.trim().toLowerCase().hashCode() & 0x7fffffff) % capacity;
    }

    public void insert(Restaurant r) {
        buckets.get(hash(r.getLocation())).add(r);
    }

    // Lahat ng restaurants sa isang location (mabilis dahil isang bucket lang ang tinitingnan)
    public ArrayList<Restaurant> getByLocation(String location) {
        ArrayList<Restaurant> result = new ArrayList<>();
        ArrayList<Restaurant> bucket = buckets.get(hash(location));
        for (int i = 0; i < bucket.size(); i++) {
            if (bucket.get(i).getLocation().equalsIgnoreCase(location.trim())) {
                result.add(bucket.get(i));
            }
        }
        return result;
    }

    public ArrayList<Restaurant> getAll() {
        ArrayList<Restaurant> all = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            all.addAll(buckets.get(i));
        }
        return all;
    }

    public void clear() {
        for (int i = 0; i < capacity; i++) {
            buckets.get(i).clear();
        }
    }
}