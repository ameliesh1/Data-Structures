/**
 * Class that implements an AVL tree which implements the MyMap interface.
 * @author Amelie Sharples aes2367
 * @version 1.0 November 5, 2022
 */
public class AVLTreeMap<K extends Comparable<K>, V> extends BSTMap<K, V>
        implements MyMap<K, V> {
    private static final int ALLOWED_IMBALANCE = 1;

    /**
     * Creates an empty AVL tree map.
     */
    public AVLTreeMap() { }

    public AVLTreeMap(Pair<K, V>[] elements) {
        insertElements(elements);
    }

    /**
     * Creates a AVL tree map of the given key-value pairs. If
     * sorted is true, a balanced tree will be created via a divide-and-conquer
     * approach. If sorted is false, the pairs will be inserted in the order
     * they are received, and the tree will be rotated to maintain the AVL tree
     * balance property.
     * @param elements an array of key-value pairs
     */
    public AVLTreeMap(Pair<K, V>[] elements, boolean sorted) {
        if (!sorted) {
            insertElements(elements);
        } else {
            root = createBST(elements, 0, elements.length - 1);
        }
    }

    /**
     * Recursively constructs a balanced binary search tree by inserting the
     * elements via a divide-snd-conquer approach. The middle element in the
     * array becomes the root. The middle of the left half becomes the root's
     * left child. The middle element of the right half becomes the root's right
     * child. This process continues until low > high, at which point the
     * method returns a null Node.
     * @param pairs an array of <K, V> pairs sorted by key
     * @param low   the low index of the array of elements
     * @param high  the high index of the array of elements
     * @return      the root of the balanced tree of pairs
     */
    protected Node<K, V> createBST(Pair<K, V>[] pairs, int low, int high) {
        if (low > high) {
            return null;
        }
        int mid = low + (high - low) / 2;
        Pair<K, V> pair = pairs[mid];
        Node<K, V> parent = new Node<>(pair.key, pair.value);
        size++;
        parent.left = createBST(pairs, low, mid - 1);
        if (parent.left != null) {
            parent.left.parent = parent;
        }
        parent.right = createBST(pairs, mid + 1, high);
        if (parent.right != null) {
            parent.right.parent = parent;
        }
        // This line is critical for being able to add additional nodes or to
        // remove nodes. Forgetting this line leads to incorrectly balanced
        // trees.
        parent.height =
                Math.max(avlHeight(parent.left), avlHeight(parent.right)) + 1;
        return parent;
    }

    /**
     * Associates the specified value with the specified key in this map. If the
     * map previously contained a mapping for the key, the old value is replaced
     * by the specified value.
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no
     *         mapping for key
     */
    @Override
    public V put(K key, V value) {
        NodeOldValuePair nvp = new NodeOldValuePair(null, null);
        nvp = insertAndBalance(key, value, root, nvp);
        return nvp.oldValue;
    }

    private Node<K, V> removeHelper(K oriKey, K key, Node<K, V> t, NodeOldValuePair nvp) {
        if(t == null) {
            return t;
        }
        int comparison = key.compareTo(t.key);
        int compOriKey = oriKey.compareTo(t.key);
        if(compOriKey == 0) {
            nvp.node = t;
            nvp.oldValue = t.value;
        }
        if(comparison < 0) {
            t.left = removeHelper(oriKey, key, t.left, nvp);
            if(t.left != null) {
                t.left.parent = t;
            }
        } else if(comparison > 0) {
            t.right = removeHelper(oriKey, key, t.right, nvp);
            if(t.right != null) {
                t.right.parent = t;
            }
        } else if(t.left != null && t.right != null) {
            Node<K, V> p = treeMin(t.right);
            t.key = p.key;
            t.value = p.value;
            t.right = removeHelper(oriKey, t.key, t.right, nvp);
            if(t.right != null) {
                t.right.parent = t;
            }
        } else {
            if(t == root) {
                if(t.left != null && t.right == null) {
                    root = t.left;
                } else if(t.right != null && t.left == null) {
                    root = t.right;
                }
                root.parent = null;
            }
            t = (t.left != null) ? t.left : t.right;
        }
        return balance(t);
    }
    /**
     * Removes the mapping for a key from this map if it is present.
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no
     *         mapping for key
     */
    public V remove(K key) {
        if(size == 1) {
            size--;
            V val = root.value;
            root = null;
            return val;
        }
        NodeOldValuePair nvp = new NodeOldValuePair(root, root.value);
        removeHelper(key, key, root, nvp);
        size--;
        return nvp.oldValue;
    }

    public V removeIterative(K key) {
        if(iterativeSearch(key) == null) {
            return null;
        }
        Node<K, V> t = iterativeSearch(key);
        int comparison = key.compareTo(root.key);
        V val = t.value;
        if(t.left == null) {
            switchRem(t, t.right);
        } else if(t.right == null) {
            switchRem(t, t.left);
        } else {
            Node<K, V> y = treeMin(t.right);
            if (y.parent != t) {
                transplant(y, y.right);
                y.right = t.right;
                y.right.parent = y;
            }
            transplant(t, y);
            y.left = t.left;
            y.left.parent = y;
        }
        size--;
        if(t.parent == null) {
            t = root;
        } else {
            t = t.parent;
        }
        if(t == root) {
            if(size < 2) {
                return val;
            }
            if(size == 4) {
                balance(t.left);
            } else if(size == 3) {
                balance(t);
            } else {
                balance(t.right);
            }
        } else {
            balance(t);
        }
        return val;
    }
    protected Node<K, V> treeMin(Node<K, V> x) {
        while (x.left != null) {
            x = x.left;
        }
        return x;
    }
    protected void switchRem(Node<K, V> u, Node<K, V> v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        if (v != null) {
            v.parent = u.parent;
        }
    }

    private NodeOldValuePair insertAndBalance(
            K key, V value, Node<K, V> t, NodeOldValuePair nvp) {
        if (t == null) {
            size++;
            nvp.node = new Node<K, V>(key, value);
            if (root == null) {
                root = nvp.node;
            }
            return nvp;
        }
        int comparison = key.compareTo(t.key);
        Node<K, V> k = iterativeSearch(key);
        nvp.node = k;
        if(k != null) {
            nvp.oldValue = k.value;
            k.value = value;
        } else { //putting iteratively
            Node<K, V> a = root;
            Node<K, V> b = null;
            while (a != null) {
                b = a;
                comparison = key.compareTo(a.key);
                if (comparison < 0) {
                    a = a.left;
                } else if (comparison > 0) {
                    a = a.right;
                }
            }
            nvp.node = new Node<K, V>(key, value);
            nvp.node.parent = b;
            if (b == null) {
                root = nvp.node;
            } else if (key.compareTo(b.key) < 0) {
                b.left = nvp.node;
            } else {
                b.right = nvp.node;
            }
            size++;
        }
        Node<K, V> n = balance(t);
        nvp.node = n;
        return nvp;
    }

    private Node<K, V> balance(Node<K, V> t)
    {
        if(t == null)
        {
            return null;
        }
        else if((height(t.left) - height(t.right)) > 1)
        {
            if (t.left != null &&
                    height(t.left.left) >= height(t.left.right))
            {
                t = rotateWithLeftChild(t);
            } else {
                t = doubleWithLeftChild(t);
            }
        }
        else if(height(t.right) - height(t.left) > 1)
        {
            if(t.left != null && t.right != null &&
                    height(t.right.right) >= height(t.right.left))
            {
                t = rotateWithRightChild(t);
            }
            else {
                t = doubleWithRightChild(t);
            }
        }

        t.height = Math.max(height(t.left), height(t.right));
        return t;
    }

    private int avlHeight(Node<K, V> t) {
        return t == null ? -1 : t.height;
    }

    private Node<K, V> rotateWithLeftChild(Node<K, V> k2)
    {
        if(k2.left == null) {
            return k2;
        }
        Node<K, V> k1 = k2.left;

        k2.left = k1.right;
        if(k2.left != null) {
            k2.left.parent = k2;
        }

        k1.right = k2;
        k1.parent = k2.parent;
        k2.parent = k1;

        k2.height = Math.max(height(k2.left), height(k2.right)) + 1;
        k1.height = Math.max(height(k1.left), k2.height) + 1;

        if(k1.parent == null) {
            root = k1;
        }
        return k1;
    }

    private Node<K, V> rotateWithRightChild(Node<K, V> k1)
    {
        if(k1.right == null) {
            return k1;
        }
        Node<K, V> k2 = k1.right;

        k1.right = k2.left;
        if(k1.right != null) {
            k1.right.parent = k1;
        }

        k2.left = k1;
        k2.parent = k1.parent;
        k1.parent = k2;

        k1.height = Math.max(height(k1.right), height(k1.left)) + 1;
        k2.height = Math.max(height(k2.right), height(k1)) + 1;

        if(k2.parent == null) {
            root = k2;
        }
        return k2;
    }
    private Node<K, V> doubleWithLeftChild(Node<K, V> k3) {
        k3.left = rotateWithRightChild(k3.left);
        return rotateWithLeftChild(k3);
    }

    private Node<K, V> doubleWithRightChild(Node<K, V> k3) {
        k3.right = rotateWithLeftChild(k3.right);
        return rotateWithRightChild(k3);
    }

    private class NodeOldValuePair {
        Node<K, V> node;
        V oldValue;

        NodeOldValuePair(Node<K, V> n, V oldValue) {
            this.node = n;
            this.oldValue = oldValue;
        }
    }

    public static void main(String[] args) {
        Pair<Integer, Integer>[] pairs1 = new Pair[0];
        pairs1 = new Pair[10];
        for (int i = 0; i < 10; i++) {
            pairs1[i] = new Pair(i, i+1);
        }
        AVLTreeMap<Integer, Integer> map1 = new AVLTreeMap<>(pairs1, true);

        Integer retVal1 = map1.remove(4);
        retVal1 = map1.remove(0);
        retVal1 = map1.remove(5);
        retVal1 = map1.remove(7);
        retVal1 = map1.remove(8);
        retVal1 = map1.remove(9);
        retVal1 = map1.remove(1);
        retVal1 = map1.remove(3);
        retVal1 = map1.remove(6);
        retVal1 = map1.remove(2);

        AVLTreeMap<Integer, Integer> map = new AVLTreeMap<>();
        Integer retVal = map.put(10, 10);
        retVal = map.put(2, 2);
        retVal = map.put(2, 3);
        retVal = map.put(5, 2);
        retVal = map.put(12, 13);
        int k = map.iterativeSearch(2).parent.key;

        boolean usingInts = true;
        if (args.length > 0) {
            try {
                Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                usingInts = false;
            }
        }

        AVLTreeMap avlTree;
        if (usingInts) {
            @SuppressWarnings("unchecked")
            Pair<Integer, Integer>[] pairs = new Pair[args.length];
            for (int i = 0; i < args.length; i++) {
                try {
                    int val = Integer.parseInt(args[i]);
                    pairs[i] = new Pair<>(val, val);
                } catch (NumberFormatException nfe) {
                    System.err.println("Error: Invalid integer '" + args[i]
                            + "' found at index " + i + ".");
                    System.exit(1);
                }
            }
            avlTree = new AVLTreeMap<Integer, Integer>(pairs);
        } else {
            @SuppressWarnings("unchecked")
            Pair<String, String>[] pairs = new Pair[args.length];
            for (int i = 0; i < args.length; i++) {
                pairs[i] = new Pair<>(args[i], args[i]);
            }
            avlTree = new AVLTreeMap<String, String>(pairs);
        }

        System.out.println(avlTree.toAsciiDrawing());
        System.out.println();
        System.out.println("Height:                   " + avlTree.height());
        System.out.println("Total nodes:              " + avlTree.size());
        System.out.printf("Successful search cost:   %.3f\n",
                avlTree.successfulSearchCost());
        System.out.printf("Unsuccessful search cost: %.3f\n",
                avlTree.unsuccessfulSearchCost());
        avlTree.printTraversal(PREORDER);
        avlTree.printTraversal(INORDER);
        avlTree.printTraversal(POSTORDER);
    }
}