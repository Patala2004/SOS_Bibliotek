package model;

public class PageMetadata {
    private int size;
    private int totalElements;
    private int totalPages;
    private int number;

    public int getTotalElements(){
        return this.totalElements;
    }

    public int getNumber(){
        return this.number;
    }

    public int getSize(){
        return this.size;
    }

    public int getTotalPages(){
        return this.totalPages;
    }
}
