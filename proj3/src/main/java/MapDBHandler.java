import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;

/**
 *  Parses OSM XML files using an XML SAX parser. Used to construct the graph of roads for
 *  pathfinding, under some constraints.
 *  See OSM documentation on
 *  <a href="http://wiki.openstreetmap.org/wiki/Key:highway">the highway tag</a>,
 *  <a href="http://wiki.openstreetmap.org/wiki/Way">the way XML element</a>,
 *  <a href="http://wiki.openstreetmap.org/wiki/Node">the node XML element</a>,
 *  and the java
 *  <a href="https://docs.oracle.com/javase/tutorial/jaxp/sax/parsing.html">SAX parser tutorial</a>.
 *  @author Alan Yao
 *  @author Victor Ou
 */
public class MapDBHandler extends DefaultHandler {
    /**
     * Only allow for non-service roads; this prevents going on pedestrian streets as much as
     * possible. Note that in Berkeley, many of the campus roads are tagged as motor vehicle
     * roads, but in practice we walk all over them with such impunity that we forget cars can
     * actually drive on them.
     */
    private static final Set<String> ALLOWED_HIGHWAY_TYPES = new HashSet<>(Arrays.asList
            ("motorway", "trunk", "primary", "secondary", "tertiary", "unclassified",
                    "residential", "living_street", "motorway_link", "trunk_link", "primary_link",
                    "secondary_link", "tertiary_link"));
    private String activeState = "";
    private Map<Long, Node> nodeMap;
    private List<Node> wayNodes;
    private boolean isValidHighwayType;
    private Map<Node, Set<Node>> connections;


    public MapDBHandler(GraphDB g) {
        nodeMap = new HashMap<Long, Node>();
        wayNodes = new ArrayList<Node>();
        isValidHighwayType = false;
        connections = new HashMap<Node, Set<Node>>();
    }

    public Map<Long, Node> getNodeMap() {
        return this.nodeMap;
    }

    public Map<Node, Set<Node>> getConnections() {
        return this.connections;
    }

    /**
     * Called at the beginning of an element. Typically, you will want to handle each element in
     * here, and you may want to track the parent element.
     * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or
     *            if Namespace processing is not being performed.
     * @param localName The local name (without prefix), or the empty string if Namespace
     *                  processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty string if qualified names are
     *              not available. This tells us which element we're looking at.
     * @param attributes The attributes attached to the element. If there are no attributes, it
     *                   shall be an empty Attributes object.
     * @throws SAXException Any SAX exception, possibly wrapping another exception.
     * @see Attributes
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (qName.equals("node")) {
            activeState = "node";
            Node n = new Node(Long.parseLong(attributes.getValue("id")), Double.parseDouble(attributes.getValue("lon")),
                    Double.parseDouble(attributes.getValue("lat")));
            nodeMap.put(Long.parseLong(attributes.getValue("id")), n);
        } else if (qName.equals("way")) {
            isValidHighwayType = false;
            wayNodes.clear();
            activeState = "way";
        } else if (activeState.equals("way") && qName.equals("nd")) {
            Node wayNode = nodeMap.get(Long.parseLong(attributes.getValue("ref")));
            wayNodes.add(wayNode);
        } else if (activeState.equals("way") && qName.equals("tag")) {
            String k = attributes.getValue("k");
            String v = attributes.getValue("v");
            if (k.equals("highway") && ALLOWED_HIGHWAY_TYPES.contains(v)) {
                isValidHighwayType = true;
            }
        } else if (activeState.equals("node") && qName.equals("tag") && attributes.getValue("k")
                .equals("name")) {
        }
    }

    /**
     * Receive notification of the end of an element. You may want to take specific terminating
     * actions here, like finalizing vertices or edges found.
     * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or
     *            if Namespace processing is not being performed.
     * @param localName The local name (without prefix), or the empty string if Namespace
     *                  processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty string if qualified names are
     *              not available.
     * @throws SAXException  Any SAX exception, possibly wrapping another exception.
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("way") && isValidHighwayType) {
            if (wayNodes.size() > 1) {
                for (int i = 1; i < wayNodes.size(); i++) {
                    Node n1 = wayNodes.get(i-1);
                    Node n2 = wayNodes.get(i);
                    Set<Node> n1Connections = connections.get(n1);
                    Set<Node> n2Connections = connections.get(n2);
                    if (n1Connections == null) {
                        n1Connections = new HashSet<Node>();
                        connections.put(n1, n1Connections);
                    }
                    if (n2Connections == null) {
                        n2Connections = new HashSet<Node>();
                        connections.put(n2, n2Connections);
                    }
                    n1Connections.add(n2);
                    n2Connections.add(n1);
                    }
            }
            wayNodes.clear();
            isValidHighwayType = false;
        }
    }

}
