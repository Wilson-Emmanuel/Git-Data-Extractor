package com.wilcotech.dataextractor.core.core.domain.models;

import java.util.List;

/**
 * Created by Wilson
 * on Sun, 18/04/2021.
 */
public class PagedData <T> {
    private List<T> items;
    private long totalItems;
    private int totalPages;
    public PagedData(List<T> items, long totalItems, int totalPages){
        this.items = items;
        this.totalItems = totalItems;
        this.totalPages = totalPages;
    }
 public boolean isEmpty(){
        return items == null || items.isEmpty();
 }
    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
