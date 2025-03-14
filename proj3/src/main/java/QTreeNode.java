import java.io.File;
import java.util.Arrays;

/**
 * Each tree node represents a node in the quadTree, used to raster a complete map
 * @Author Victor Ou
 */
public class QTreeNode implements Comparable<QTreeNode> {
    private String id;
    private double ullon, ullat, lrlon, lrlat;
    /* Array of size 4, corresponding to the 4 quadrants, from upper left to bottom right, in that order */
    private QTreeNode[] children;

    public QTreeNode(String id, double ullon, double ullat, double lrlon, double lrlat) {
        this.id = id;
        this.ullon = ullon;
        this.ullat = ullat;
        this.lrlon = lrlon;
        this.lrlat = lrlat;
        this.children = new QTreeNode[4];

        /* Recursively add the tiles to the QuadTree if the file exists in the /img directory */
        if (id.equals("root")) {
            id = "";
        }
        if (id.length() < 7) {
            children[0] = new QTreeNode(id + "1", ullon, ullat, (ullon + lrlon)/2, (ullat + lrlat)/2);
            children[1] = new QTreeNode(id + "2", (ullon + lrlon)/2, ullat, lrlon, (ullat + lrlat)/2);
            children[2] = new QTreeNode(id + "3", ullon, (ullat + lrlat)/2, (ullon + lrlon)/2, lrlat);
            children[3] = new QTreeNode(id + "4", (ullon + lrlon)/2, (ullat + lrlat)/2, lrlon, lrlat);
        }
    }

    /* Checks if the other QTreeNode (a rectangle) will intersect with this QTreeNode */
    public boolean intersects(QTreeNode other) {
        return (ullon < other.lrlon) && (lrlon > other.ullon) && (ullat > other.lrlat) && (lrlat < other.ullat);
    }

    public String getId() {
        return this.id;
    }

    public double getUllon() {
        return this.ullon;
    }

    public double getUllat() {
        return this.ullat;
    }

    public double getLrlon() {
        return this.lrlon;
    }

    public double getLrlat() {
        return this.lrlat;
    }

    public QTreeNode getTile1() {
        return children[0];
    }

    public QTreeNode getTile2() {
        return children[1];
    }

    public QTreeNode getTile3() {
        return children[2];
    }

    public QTreeNode getTile4() {
        return children[3];
    }

    @Override
    public int compareTo(QTreeNode other) {
        int ret = Double.compare(other.ullat, this.ullat);
        if (ret == 0) {
            ret = Double.compare(this.ullon, other.ullon);
        }
        return ret;
    }

    @Override
    public String toString() {
        return "QTreeNode{" +
                "id='" + id + '\'' +
                ", \nullon=" + ullon +
                ", \nullat=" + ullat +
                ", \nlrlon=" + lrlon +
                ", \nlrlat=" + lrlat +
                ", \nchildren=" + Arrays.toString(children) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QTreeNode qTreeNode = (QTreeNode) o;

        if (Double.compare(qTreeNode.ullon, ullon) != 0) return false;
        if (Double.compare(qTreeNode.ullat, ullat) != 0) return false;
        if (Double.compare(qTreeNode.lrlon, lrlon) != 0) return false;
        if (Double.compare(qTreeNode.lrlat, lrlat) != 0) return false;
        if (id != null ? !id.equals(qTreeNode.id) : qTreeNode.id != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(children, qTreeNode.children);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        temp = Double.doubleToLongBits(ullon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(ullat);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lrlon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lrlat);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + Arrays.hashCode(children);
        return result;
    }
}
