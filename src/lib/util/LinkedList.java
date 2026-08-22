package lib.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedList<T> implements List<T> {

    private Node<T> head;
    private int size;

    public LinkedList() {
        head = null;
        size = 0;
    }

    @Override
    public void add(T item) {
        if (head == null) {
            head = new Node<>(item);
            size++;
            return;
        }

        Node<T> current = head;

        while (current.getNext() != null) {
            current = current.getNext();
        }

        current.setNext(new Node<>(item));
        size++;
    }

    @Override
    public void add(int index, T item) {
        if (index < 0 || index > size) {
            return;
        }

        if (index == 0) {
            Node<T> newNode = new Node<>(item);
            newNode.setNext(head);
            head = newNode;
            size++;
            return;
        }

        Node<T> previous = head;
        int i = 0;

        while (previous != null && i < index - 1) {
            previous = previous.getNext();
            i++;
        }

        if (previous == null) {
            return;
        }

        Node<T> newNode = new Node<>(item);
        newNode.setNext(previous.getNext());
        previous.setNext(newNode);
        size++;
    }

    @Override
    public boolean remove(int index) {
        if (index < 0 || index >= size) {
            return false;
        }

        if (head == null) {
            return false;
        }

        if (index == 0) {
            head = head.getNext();
            size--;
            return true;
        }

        Node<T> previous = head;
        int i = 0;

        while (previous.getNext() != null && i < index - 1) {
            previous = previous.getNext();
            i++;
        }

        if (previous.getNext() != null) {
            previous.setNext(previous.getNext().getNext());
            size--;
            return true;
        }

        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        Node<T> current = head;
        int i = 0;

        while (current != null && i != index) {
            current = current.getNext();
            i++;
        }

        if (current == null) {
            return null;
        }

        return current.getData();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T data = current.getData();
                current = current.getNext();
                return data;
            }
        };
    }


}
