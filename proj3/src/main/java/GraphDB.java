import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Wraps the parsing functionality of the MapDBHandler as an example.
 * You may choose to add to the functionality of this class if you wish.
 * @author Alan Yao
 * @Author Victor Ou
 */
public class GraphDB {
    private MapDBHandler maphandler;
    /**
     * Example constructor shows how to create and start an XML parser.
     * @param db_path Path to the XML file to be parsed.
     */
    public GraphDB(String db_path) {
        try {
            //File inputFile = new File(db_path);
            InputStream in = getClass().getClassLoader().getResourceAsStream(db_path);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            maphandler = new MapDBHandler(this);
            //saxParser.parse(inputFile, maphandler);
            saxParser.parse(in, maphandler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean(maphandler.getNodeMap());
    }

    public Map<Long, Node> getNodeMap() {
        return maphandler.getNodeMap();
    }

    public Map<Node, Set<Node>> getConnections() {
        return maphandler.getConnections();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean(Map<Long, Node> nodeMap) {
        Map<Node, Set<Node>> connections = getConnections();
        for (Iterator<Map.Entry<Long, Node>> it = nodeMap.entrySet().iterator(); it.hasNext(); ) {
            Node currentNode = it.next().getValue();
            if (!connections.containsKey(currentNode)) {
                it.remove();
            }
        }
    }
}
