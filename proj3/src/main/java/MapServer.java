import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

/* Maven is used to pull in these dependencies. */
import com.google.gson.Gson;

import javax.imageio.ImageIO;

import static spark.Spark.*;

/**
 * This MapServer class is the entry point for running the JavaSpark web server for the BearMaps
 * application project, receiving API calls, handling the API call processing, and generating
 * requested images and routes.
 * @author Alan Yao
 * @author Victor Ou
 */
public class MapServer {
    /**
     * The root upper left/lower right longitudes and latitudes represent the bounding box of
     * the root tile, as the images in the img/ folder are scraped.
     * Longitude == x-axis; latitude == y-axis.
     */
    public static final double ROOT_ULLAT = 37.892195547244356, ROOT_ULLON = -122.2998046875,
            ROOT_LRLAT = 37.82280243352756, ROOT_LRLON = -122.2119140625;
    /** Each tile is 256x256 pixels. */
    public static final int TILE_SIZE = 256;
    /** HTTP failed response. */
    private static final int HALT_RESPONSE = 403;
    /** Route stroke information: typically roads are not more than 5px wide. */
    public static final float ROUTE_STROKE_WIDTH_PX = 5.0f;
    /** Route stroke information: Cyan with half transparency. */
    public static final Color ROUTE_STROKE_COLOR = new Color(108, 181, 230, 200);
    /** The tile images are in the IMG_ROOT folder. */
    private static final String IMG_ROOT = "static/img/";
    /**
     * The OSM XML file path. Downloaded from <a href="http://download.bbbike.org/osm/">here</a>
     * using custom region selection.
     **/
    private static final String OSM_DB_PATH = "static/berkeley.osm";
    /**
     * Each raster request to the server will have the following parameters
     * as keys in the params map accessible by,
     * i.e., params.get("ullat") inside getMapRaster(). <br>
     * ullat -> upper left corner latitude,<br> ullon -> upper left corner longitude, <br>
     * lrlat -> lower right corner latitude,<br> lrlon -> lower right corner longitude <br>
     * w -> user viewport window width in pixels,<br> h -> user viewport height in pixels.
     **/
    private static final String[] REQUIRED_RASTER_REQUEST_PARAMS = {"ullat", "ullon", "lrlat",
        "lrlon", "w", "h"};
    /**
     * Each route request to the server will have the following parameters
     * as keys in the params map.<br>
     * start_lat -> start point latitude,<br> start_lon -> start point longitude,<br>
     * end_lat -> end point latitude, <br>end_lon -> end point longitude.
     **/
    private static final String[] REQUIRED_ROUTE_REQUEST_PARAMS = {"start_lat", "start_lon",
        "end_lat", "end_lon"};

    private static GraphDB g;
    private static QuadTree quadTree;
    private static Map<Long, Node> nodeMap;
    private static Map<Node, Set<Node>> connections;
    private static LinkedList<Long> routeIds;
    private static LinkedList<Node> routeNodes;

    public static void initialize() {
        g = new GraphDB(OSM_DB_PATH);
        quadTree = new QuadTree(ROOT_ULLON, ROOT_ULLAT, ROOT_LRLON, ROOT_LRLAT);
        nodeMap = g.getNodeMap();
        connections = g.getConnections();
        routeIds = new LinkedList<Long>();
        routeNodes = new LinkedList<Node>();
    }

    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

    public static void main(String[] args) {
        initialize();
        port(getHerokuAssignedPort());
        staticFileLocation("/page");
        /* Allow for all origin requests (since this is not an authenticated server, we do not
         * care about CSRF).  */
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Request-Method", "*");
            response.header("Access-Control-Allow-Headers", "*");
        });

        /* Define the raster endpoint for HTTP GET requests. I use anonymous functions to define
         * the request handlers. */
        get("/raster", (req, res) -> {
            HashMap<String, Double> params =
                    getRequestParams(req, REQUIRED_RASTER_REQUEST_PARAMS);
            /* The png image is written to the ByteArrayOutputStream */
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            /* getMapRaster() does almost all the work for this API call */
            Map<String, Object> rasteredImgParams = getMapRaster(params, os);
            /* On an image query success, add the image data to the response */
            if (rasteredImgParams.containsKey("query_success")
                    && (Boolean) rasteredImgParams.get("query_success")) {
                String encodedImage = Base64.getEncoder().encodeToString(os.toByteArray());
                rasteredImgParams.put("b64_encoded_image_data", encodedImage);
            }
            /* Encode response to Json */
            Gson gson = new Gson();
            return gson.toJson(rasteredImgParams);
        });

        /* Define the routing endpoint for HTTP GET requests. */
        get("/route", (req, res) -> {
            HashMap<String, Double> params =
                    getRequestParams(req, REQUIRED_ROUTE_REQUEST_PARAMS);
            LinkedList<Long> route = findAndSetRoute(params);
            return !route.isEmpty();
        });

        /* Define the API endpoint for clearing the current route. */
        get("/clear_route", (req, res) -> {
            clearRoute();
            return true;
        });

        /* Define the API endpoint for search */
        get("/search", (req, res) -> {
            Set<String> reqParams = req.queryParams();
            String term = req.queryParams("term");
            Gson gson = new Gson();
            /* Search for actual location data. */
            if (reqParams.contains("full")) {
                List<Map<String, Object>> data = getLocations(term);
                return gson.toJson(data);
            } else {
                /* Search for prefix matching strings. */
                List<String> matches = getLocationsByPrefix(term);
                return gson.toJson(matches);
            }
        });

        /* Define map application redirect */
        get("/", (request, response) -> {
            response.redirect("/map.html", 301);
            return true;
        });
    }

    /**
     * Validate & return a parameter map of the required request parameters.
     * Requires that all input parameters are doubles.
     * @param req HTTP Request
     * @param requiredParams TestParams to validate
     * @return A populated map of input parameter to it's numerical value.
     */
    private static HashMap<String, Double> getRequestParams(
            spark.Request req, String[] requiredParams) {
        Set<String> reqParams = req.queryParams();
        HashMap<String, Double> params = new HashMap<>();
        for (String param : requiredParams) {
            if (!reqParams.contains(param)) {
                halt(HALT_RESPONSE, "Request failed - parameters missing.");
            } else {
                try {
                    params.put(param, Double.parseDouble(req.queryParams(param)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    halt(HALT_RESPONSE, "Incorrect parameters - provide numbers.");
                }
            }
        }
        return params;
    }


    /**
     * Handles raster API calls, queries for tiles and rasters the full image. <br>
     * <p>
     *     The rastered photo must have the following properties:
     *     <ul>
     *         <li>Has dimensions of at least w by h, where w and h are the user viewport width
     *         and height.</li>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *         <li>If a current route exists, lines of width ROUTE_STROKE_WIDTH_PX and of color
     *         ROUTE_STROKE_COLOR are drawn between all nodes on the route in the rastered photo.
     *         </li>
     *     </ul>
     *     Additional image about the raster is returned and is to be included in the Json response.
     * </p>
     * @param params Map of the HTTP GET request's query parameters - the query bounding box and
     *               the user viewport width and height.
     * @param os     An OutputStream that the resulting png image should be written to.
     * @return A map of parameters for the Json response as specified:
     * "raster_ul_lon" -> Double, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Double, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Double, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Double, the bounding lower right latitude of the rastered image <br>
     * "raster_width"  -> Double, the width of the rastered image <br>
     * "raster_height" -> Double, the height of the rastered image <br>
     * "depth"         -> Double, the 1-indexed quadtree depth of the nodes of the rastered image.
     * Can also be interpreted as the length of the numbers in the image string. <br>
     * "query_success" -> Boolean, whether an image was successfully rastered. <br>
     * @see #REQUIRED_RASTER_REQUEST_PARAMS
     */
    public static Map<String, Object> getMapRaster(Map<String, Double> params, OutputStream os) {
        double q_ullon = params.get("ullon");
        double q_ullat = params.get("ullat");
        double q_lrlon = params.get("lrlon");
        double q_lrlat = params.get("lrlat");

        /* Calculate the distance per pixel for the root frame and the requested frame */
        double dppCurrent = Math.abs(ROOT_LRLON - ROOT_ULLON) / TILE_SIZE;
        double dppRequested = Math.abs(q_lrlon - q_ullon)/params.get("w");

        /* Calculate what level of the quadTree to recurse to */
        int depth = 0;
        while (dppCurrent > dppRequested) {
            depth++;
            dppCurrent /= 2;
        }
        /* Max depth is 7 */
        if (depth > 7) {
            depth = 7;
        }

        /* List to store the QTreeNodes that we find at the target depth within the query */
        List<QTreeNode> requestedTiles = new ArrayList<QTreeNode>();
        QTreeNode queryTile = new QTreeNode("query", q_ullon, q_ullat, q_lrlon, q_lrlat);
        /* Add all of the tiles filling the query criteria to the requestedTiles list */
        collectRequestedTiles(requestedTiles, quadTree.getRoot(), queryTile, depth);
        /* Sort/arrange the tiles using their longitudes and latitudes such that they create a grid */
        sortRequestedTiles(requestedTiles);

        /* Create a new BufferedImage with the dimensions of the fully rastered image */
        QTreeNode firstTile = requestedTiles.get(0);
        QTreeNode lastTile = requestedTiles.get(requestedTiles.size() - 1);
        int imgWidth = (int) (TILE_SIZE * Math.abs(lastTile.getLrlon() - firstTile.getUllon()) /
                Math.abs(firstTile.getLrlon() - firstTile.getUllon()));
        int imgHeight = (int) (TILE_SIZE * Math.abs(lastTile.getLrlat() - firstTile.getUllat())/
                Math.abs(firstTile.getLrlat() - firstTile.getUllat()));
        BufferedImage bi = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();

        /* Draw each tile in the list onto its correct position in the rastered image */
        rasterTiles(g, requestedTiles, imgWidth);

        /* Draw route of the shortest path */
        drawRoute(g, firstTile.getUllon(), firstTile.getUllat(),
                Math.abs(lastTile.getLrlon() - firstTile.getUllon()), Math.abs(lastTile.getLrlat() - firstTile.getUllat()),
                imgHeight, imgWidth);

        try {
            ImageIO.write(bi, "png", os);
        } catch (IOException ioe) {
            System.out.println("File write error: Could not write to OutputStream.");
        }

        g.dispose();

        /* Put all of the final return parameters into the returned map */
        HashMap<String, Object> rasteredImageParams = new HashMap<String, Object>();
        rasteredImageParams.put("raster_ul_lon", firstTile.getUllon());
        rasteredImageParams.put("raster_ul_lat", firstTile.getUllat());
        rasteredImageParams.put("raster_lr_lon", lastTile.getLrlon());
        rasteredImageParams.put("raster_lr_lat", lastTile.getLrlat());
        rasteredImageParams.put("raster_width", (double) imgWidth);
        rasteredImageParams.put("raster_height", (double) imgHeight);
        rasteredImageParams.put("depth", depth);
        rasteredImageParams.put("query_success", true);

        return rasteredImageParams;
    }

    /* Recursively search for the tiles in the quadTree that fit the query criteria */
    private static void collectRequestedTiles(List<QTreeNode> requestedTiles, QTreeNode currTile, QTreeNode queryTile, int depth) {
        if (depth == 0 && currTile.intersects(queryTile)) {
            requestedTiles.add(currTile);
        } else if (depth > 0) {
            if (currTile.getTile1().intersects(queryTile)) {
                collectRequestedTiles(requestedTiles, currTile.getTile1(), queryTile, depth - 1);
            }
            if (currTile.getTile2().intersects(queryTile)) {
                collectRequestedTiles(requestedTiles, currTile.getTile2(), queryTile, depth - 1);
            }
            if (currTile.getTile3().intersects(queryTile)) {
                collectRequestedTiles(requestedTiles, currTile.getTile3(), queryTile, depth - 1);
            }
            if (currTile.getTile4().intersects(queryTile)) {
                collectRequestedTiles(requestedTiles, currTile.getTile4(), queryTile, depth - 1);
            }
        }
    }

    private static void sortRequestedTiles(List<QTreeNode> requestedTiles) {
        Collections.sort(requestedTiles);
    }

    /* Combine the separate tiles into one large image */
    private static void rasterTiles(Graphics g, List<QTreeNode> requestedTiles, int imgWidth) {
        int x = 0;
        int y = 0;
        for (QTreeNode tile : requestedTiles) {
            try {
                //BufferedImage tileImage = ImageIO.read(new File(IMG_ROOT + tile.getId() + ".png"));
                String imgPath = IMG_ROOT + tile.getId() + ".png";
                InputStream in = MapServer.class.getClassLoader().getResourceAsStream(imgPath);
                BufferedImage tileImage = ImageIO.read(in);
                g.drawImage(tileImage, x, y, null);
                x += TILE_SIZE;
                if (x >= imgWidth) {
                    x = 0;
                    y += TILE_SIZE;
                }
            } catch (IOException ioe) {
                System.out.println("File read error: No such file.");
            }
        }

    }

    /* Draw the route that was found using the A* search algorithm */
    private static void drawRoute(Graphics g, double xOrigin, double yOrigin, double lonWidth, double latHeight,
                                  int imgHeight, int imgWidth) {
        ((Graphics2D) g).setStroke(new BasicStroke(MapServer.ROUTE_STROKE_WIDTH_PX,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(ROUTE_STROKE_COLOR);

        if (!routeNodes.isEmpty()) {
            Iterator<Node> iter = routeNodes.iterator();
            Node currentNode = iter.next();
            Node previousNode = null;
            while (iter.hasNext()) {
                previousNode = currentNode;
                currentNode = iter.next();
                int previousNodeX = (int) Math.floor((imgWidth * Math.abs(previousNode.getLon() - xOrigin) / lonWidth));
                int previousNodeY = (int) Math.floor((imgHeight * Math.abs(yOrigin - previousNode.getLat()) / latHeight));
                int currentNodeX = (int) Math.floor((imgWidth * Math.abs(currentNode.getLon() - xOrigin) / lonWidth));
                int currentNodeY = (int) Math.floor((imgHeight * Math.abs(yOrigin - currentNode.getLat()) / latHeight));

                g.drawLine(previousNodeX, previousNodeY, currentNodeX, currentNodeY);
            }
        }
    }

    /**
     * Searches for the shortest route satisfying the input request parameters, sets it to be the
     * current route, and returns a <code>LinkedList</code> of the route's node ids for testing
     * purposes. <br>
     * The route should start from the closest node to the start point and end at the closest node
     * to the endpoint. Distance is defined as the euclidean between two points (lon1, lat1) and
     * (lon2, lat2).
     * @param params from the API call described in REQUIRED_ROUTE_REQUEST_PARAMS
     * @return A LinkedList of node ids from the start of the route to the end.
     */
    public static LinkedList<Long> findAndSetRoute(Map<String, Double> params) {
        double startLon = params.get("start_lon");
        double startLat = params.get("start_lat");
        double endLon = params.get("end_lon");
        double endLat = params.get("end_lat");

        clearRoute();
        /* Find the closest real node from the queried point*/
        Node startNode = null;
        Node endNode = null;
        Node tempStartNode = new Node(0, startLon, startLat);
        Node tempEndNode = new Node(0, endLon, endLat);
        double distanceFromStartNode = Double.MAX_VALUE;
        double distanceFromEndNode = Double.MAX_VALUE;
        for (Map.Entry<Long, Node> e : nodeMap.entrySet()) {
            Node n = e.getValue();
            double distanceFromTempStartNode = tempStartNode.distanceTo(n);
            if (distanceFromTempStartNode < distanceFromStartNode) {
                startNode = n;
                distanceFromStartNode = distanceFromTempStartNode;
            }
            double distanceFromTempEndNode = tempEndNode.distanceTo(n);
            if (distanceFromTempEndNode < distanceFromEndNode) {
                endNode = n;
                distanceFromEndNode = distanceFromTempEndNode;
            }
        }

        /* Implements the A* search algorithm to find the shortest path between
        /* the queried points. */
        PriorityQueue<Node> pq = new PriorityQueue<Node>();
        Set<Node> seenBefore = new HashSet<Node>();
        startNode.resetDistances();
        pq.add(startNode);
        while (!pq.peek().equals(endNode)) {
            Node currentNode = pq.poll();
            seenBefore.add(currentNode);
            for (Node neighbor : connections.get(currentNode)) {
                if (!seenBefore.contains(neighbor)) {
                    Node toBeQueued = new Node(neighbor.getId(), neighbor.getLon(), neighbor.getLat());
                    double newDistanceFromStart = currentNode.getDistanceFromStart() + neighbor.distanceTo(currentNode);
                    toBeQueued.setDistanceFromStart(newDistanceFromStart);
                    double newDistanceFromEnd = neighbor.distanceTo(endNode);
                    toBeQueued.setDistanceFromEnd(newDistanceFromEnd);
                    toBeQueued.setPreviousNode(currentNode);
                    pq.add(toBeQueued);
                }
            }
        }

        /* After reaching the end, add the nodes in the shortest path into the routeNodes list */
        Node currentNode = pq.poll();
        while (currentNode != null) {
            routeNodes.addFirst(currentNode);
            routeIds.addFirst(currentNode.getId());
            currentNode = currentNode.getPreviousNode();
        }
        return routeIds;
    }

    /**
     * Clear the current found route, if it exists.
     */
    public static void clearRoute() {
        routeNodes.clear();
        routeIds.clear();
    }

    /**
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public static List<String> getLocationsByPrefix(String prefix) {
        return new LinkedList<>();
    }

    /**
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public static List<Map<String, Object>> getLocations(String locationName) {
        return new LinkedList<>();
    }
}
