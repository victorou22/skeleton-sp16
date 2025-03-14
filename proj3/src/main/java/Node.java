import java.util.HashSet;
import java.util.Set;

/**
 * Each node represents a point on the map, which can be linked together to create a path
 * @Author Victor Ou
 */
public class Node implements Comparable<Node> {
    private Long id;
    private Node previousNode;
    private double distanceFromStart;
    private double distanceFromEnd;
    private double lon, lat;

    public Node(long id, double lon, double lat) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        previousNode = null;
        distanceFromStart = 0;
        distanceFromEnd = 0;
    }

    public long getId() {
        return this.id;
    }

    public double getLon() {
        return this.lon;
    }

    public double getLat() {
        return this.lat;
    }

    public double getDistanceFromStart() { return this.distanceFromStart; }

    public void setDistanceFromStart(double distance) {
        this.distanceFromStart = distance;
    }

    public void setDistanceFromEnd(double distance) {
        this.distanceFromEnd = distance;
    }

    public void resetDistances() {
        this.distanceFromStart = 0;
        this.distanceFromEnd = 0;
    }

    public void setPreviousNode(Node n) {
        this.previousNode = n;
    }

    public Node getPreviousNode() {
        return this.previousNode;
    }

    public double distanceTo(Node other) {
        double a2 = Math.pow(this.lon - other.getLon(), 2);
        double b2 = Math.pow(this.lat - other.getLat(), 2);
        return Math.sqrt(a2 + b2);
    }

    /* Compares nodes using the heuristic of the sum of how far it is from starting point and
    /* how close it is to ending point */
    public int compareTo(Node other) {
        double d1 = this.distanceFromStart + this.distanceFromEnd;
        double d2 = other.distanceFromStart + other.distanceFromEnd;
        return Double.compare(d1, d2);
    }

    @Override
    public String toString() {
        return this.id.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Node n = (Node) o;

        return id.equals(n.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
