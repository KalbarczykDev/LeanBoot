package lib.web;

import lib.util.HashMap;
import lib.util.Map;
import lib.util.Optional;

public class WebRouter {
    private final Map<RouteKey, Route> routes;

    public WebRouter() {
        routes = new HashMap<>();
    }

    public void registerRoute(Route route) {
        RouteKey key =
                new RouteKey(route.httpMethod(), route.path());

        if (routes.containsKey(key)) {
            throw new RuntimeException(
                    "Duplicate route: "
                            + route.httpMethod()
                            + " "
                            + route.path()
            );
        }

        routes.put(key, route);
    }

    public Optional<Route> findRoute(HttpMethod method, String path) {
        Route route = routes.get(new RouteKey(method, path));
        return route == null ? Optional.empty() : Optional.of(route);
    }

    private record RouteKey(HttpMethod method, String path) {
    }
}
