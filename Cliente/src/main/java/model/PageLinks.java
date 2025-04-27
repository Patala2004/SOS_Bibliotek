package model;

public class PageLinks {
    private Href first;
    private Href self;
    private Href next;
    private Href last;

    public Href getFirst(){
        return this.first;
    }

    public Href getSelf(){
        return this.self;
    }

    public Href getNext(){
        return this.next;
    }

    public Href getLast(){
        return this.last;
    }

}
