public class TestQuadTree {
    public static final double ROOT_ULLAT = 37.892195547244356, ROOT_ULLON = -122.2998046875,
            ROOT_LRLAT = 37.82280243352756, ROOT_LRLON = -122.2119140625;
            
    public static void main(String []args) {
        QuadTree quad = new QuadTree(ROOT_ULLON, ROOT_ULLAT, ROOT_LRLON, ROOT_LRLAT);;
        System.out.println(quad);

    }
}