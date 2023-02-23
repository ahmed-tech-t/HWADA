package com.example.hwada.Model;

public class FilterModel {
    String Sort ;
    boolean open ;

    public FilterModel(String sort) {
        Sort = sort;
        this.open = open;
    }

    public String getSort() {
        return Sort;
    }

    public void setSort(String sort) {
        Sort = sort;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
