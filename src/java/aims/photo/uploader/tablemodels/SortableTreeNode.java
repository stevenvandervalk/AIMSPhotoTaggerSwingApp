package aims.photo.uploader.tablemodels;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: thart
 * Date: 12/13/12
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class SortableTreeNode extends DefaultMutableTreeNode {

    public SortableTreeNode(){
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
    public void insert(MutableTreeNode newChild, int childIndex)    {
        super.insert(newChild, childIndex);
        Collections.sort(this.children, nodeComparator);
    }

    protected Comparator nodeComparator = new Comparator () {
        @Override
        public int compare(Object o1, Object o2) {
            return o1.toString().compareToIgnoreCase(o2.toString());
        }

        @Override
        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        public boolean equals(Object obj)    {
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            return hash;
        }
};}
