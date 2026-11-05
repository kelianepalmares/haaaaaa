package structures;

import java.util.ArrayList;
import model.Order;

public class OrderPriorityQueue {

    private Order[] heap;
    private int size;

    public OrderPriorityQueue() {
        heap = new Order[10];
        size = 0;
    }

    // True kung mas mataas ang priority ni a kaysa kay b
    private boolean isHigher(Order a, Order b) {
        if (a.getUrgency() != b.getUrgency()) {
            return a.getUrgency() < b.getUrgency();
        }
        if (a.getDeliveryTime() != b.getDeliveryTime()) {
            return a.getDeliveryTime() < b.getDeliveryTime();
        }
        return a.getOrderId() < b.getOrderId();
    }

    private void swap(int i, int j) {
        Order temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    public void insert(Order order) {
        if (size == heap.length) {
            Order[] bigger = new Order[heap.length * 2];
            for (int i = 0; i < size; i++) {
                bigger[i] = heap[i];
            }
            heap = bigger;
        }
        heap[size] = order;
        int i = size;
        size++;

        // heapify up
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (isHigher(heap[i], heap[parent])) {
                swap(i, parent);
                i = parent;
            } else {
                break;
            }
        }
    }

    public Order extractMin() {
        if (size == 0) {
            return null;
        }
        Order top = heap[0];
        size--;
        heap[0] = heap[size];
        heap[size] = null;

        // heapify down
        int i = 0;
        while (true) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int best = i;
            if (left < size && isHigher(heap[left], heap[best])) {
                best = left;
            }
            if (right < size && isHigher(heap[right], heap[best])) {
                best = right;
            }
            if (best == i) {
                break;
            }
            swap(i, best);
            i = best;
        }
        return top;
    }

    public Order peek() {
        if (size == 0) {
            return null;
        }
        return heap[0];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    // Listahan na naka-order ayon sa priority (hindi ginagalaw ang original)
    public ArrayList<Order> toSortedList() {
        OrderPriorityQueue copy = new OrderPriorityQueue();
        for (int i = 0; i < size; i++) {
            copy.insert(heap[i]);
        }
        ArrayList<Order> list = new ArrayList<>();
        while (!copy.isEmpty()) {
            list.add(copy.extractMin());
        }
        return list;
    }
}