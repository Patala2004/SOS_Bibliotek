package model;

public class PagePrestamo {
    private Prestamos _embedded;
    private PageLinks _links;
    private PageMetadata page;

    public PageMetadata getPage(){
        return this.page;
    }

    public PageLinks get_links(){
        return this._links;
    }

    public Prestamos get_embedded(){
        return this._embedded;
    }
}
