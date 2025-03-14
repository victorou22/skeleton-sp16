/**
 * This class wraps the functionality of the QTreeNodes.
 * @Author Victor Ou
 */
public class QuadTree {
    private QTreeNode root;

    public QuadTree(double ullon, double ullat, double lrlon, double lrlat) {
        this.root = new QTreeNode("root", ullon, ullat, lrlon, lrlat);

    }

    public QTreeNode getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return "QuadTree{" +
                "root=" + root +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuadTree quadTree = (QuadTree) o;

        return root != null ? root.equals(quadTree.root) : quadTree.root == null;

    }

    @Override
    public int hashCode() {
        return root != null ? root.hashCode() : 0;
    }
}
