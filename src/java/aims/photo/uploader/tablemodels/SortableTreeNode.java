package aims.photo.uploader.tablemodels;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: Timothy Hart
 * Date: 12/13/12
 * Time: 12:56 PM
 * <p/>
 * <code>SortableTreeNode</code> is a specific <code>DefaultMutableTreeNode</code> node object that contains
 * the full functionality of a DefaultMutableTreeNode and incoporates an auto-sorting method for use when merging new Tree Nodes
 * into existing Nodes.
 * <p/>
 * <code>SortableTreeNode</code> achieves this by using a <code>Comparator</code> inside the overridden insert method.
 *
 * @author Timothy Hart
 */
public class SortableTreeNode extends DefaultMutableTreeNode {

    public SortableTreeNode() {
        super();
    }

    public SortableTreeNode(Object userObject) {
        this(userObject, true);
    }

    public SortableTreeNode(Object userObject, boolean allowsChildren) {
        super();
        parent = null;
        this.allowsChildren = allowsChildren;
        this.userObject = userObject;
    }

    @Override
    public void insert(MutableTreeNode newChild, int childIndex) {
        super.insert(newChild, childIndex);
        Collections.sort(this.children, nodeComparator);
    }

    protected Comparator nodeComparator = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            return o1.toString().compareToIgnoreCase(o2.toString());
        }

        @Override
        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        public boolean equals(Object obj) {
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            return hash;
        }
    };
}
