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
}
