package model;

public class PageLibros {
    private Libros _embedded;
    private PageLinks _links;
    private PageMetadata page;

    public PageMetadata getPage(){
        return this.page;
    }

    public PageLinks get_links(){
        return this._links;
    }

    public Libros get_embedded(){
        return this._embedded;
    }
}
