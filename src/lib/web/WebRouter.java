package lib.web;

import lib.util.LinkedList;
import lib.util.List;

public class WebRouter {
    private final List<Route> routes;

    public WebRouter(){
        routes = new LinkedList<>();
    }

    public void register(Route route){
        routes.add(route);
    }
}
