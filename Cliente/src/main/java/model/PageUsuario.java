package model;

import org.springframework.hateoas.PagedModel.PageMetadata;

public class PageUsuario {
    private Usuarios _embedded;
    private PageLinks _links;
    private PageMetadata page;

    public PageMetadata getPage(){
        return this.page;
    }

    public PageLinks get_links(){
        return this._links;
    }

    public Usuarios get_embedded(){
        return this._embedded;
    }
}
