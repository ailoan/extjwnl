package net.sf.extjwnl.data.list;

import net.sf.extjwnl.data.PointerTarget;
import net.sf.extjwnl.data.Synset;

import java.util.ArrayList;
import java.util.List;

/**
 * A container for the root node of a pointer target tree.
 *
 * @author John Didion <jdidion@users.sourceforge.net>
 */
public class PointerTargetTree {
    private PointerTargetTreeNode rootNode;

    public PointerTargetTree(PointerTargetTreeNode rootNode) {
        this.rootNode = rootNode;
    }

    public PointerTargetTree(Synset synset, PointerTargetTreeNodeList list) {
        rootNode = new PointerTargetTreeNode(synset);
        rootNode.setChildTreeList(list);
    }

    public PointerTargetTreeNode getRootNode() {
        return rootNode;
    }

    /**
     * Two PointerTargetTree's are equal if their root nodes are equal.
     */
    public boolean equals(Object obj) {
        return (obj instanceof PointerTargetTree) && rootNode.equals(((PointerTargetTree) obj).getRootNode());
    }

    /**
     * Walk the tree and perform the operation <var>opr</var> on
     * each node. Continues until either opr returns a non-null
     * value, or it reaches the last node in the tree.
     */
    public Object getFirstMatch(PointerTargetTreeNodeList.Operation opr) {
        Object obj = opr.execute(getRootNode());
        if (obj == null && getRootNode().hasValidChildTreeList()) {
            obj = getRootNode().getChildTreeList().getFirstMatch(opr);
        }
        return obj;
    }

    /**
     * Walk the tree and perform the operation <var>opr</var> on each node.
     * Searches the tree exhaustively and returns a List containing all nodes
     * that are returned by <var>opr</var>.
     */
    public List getAllMatches(PointerTargetTreeNodeList.Operation opr) {
        List list = new ArrayList();
        if (opr.execute(getRootNode()) != null) {
            list.add(getRootNode());
        }
        if (getRootNode().hasValidChildTreeList()) {
            getRootNode().getChildTreeList().getAllMatches(opr, list);
        }
        return list;
    }

    /**
     * Find the first occurrence of <var>node</var> in the tree.
     */
    public PointerTargetTreeNode findFirst(PointerTargetTreeNode node) {
        return (PointerTargetTreeNode) getFirstMatch(new PointerTargetTreeNodeList.FindNodeOperation(node));
    }

    /**
     * Find the first node in the tree whose target is <var>target</var>
     */
    public PointerTargetTreeNode findFirst(PointerTarget target) {
        return (PointerTargetTreeNode) getFirstMatch(new PointerTargetTreeNodeList.FindTargetOperation(target));
    }

    /**
     * Find all occurrences of <var>node</var> in the tree.
     */
    public PointerTargetTreeNode[] findAll(PointerTargetTreeNode node) {
        List list = getAllMatches(new PointerTargetTreeNodeList.FindNodeOperation(node));
        return (PointerTargetTreeNode[]) list.toArray(new PointerTargetTreeNode[list.size()]);
    }

    /**
     * Find all  nodes in the tree whose target is <var>target</var>
     */
    public PointerTargetTreeNode[] findAll(PointerTarget target) {
        List list = getAllMatches(new PointerTargetTreeNodeList.FindTargetOperation(target));
        return (PointerTargetTreeNode[]) list.toArray(new PointerTargetTreeNode[list.size()]);
    }

    public void print() {
        if (getRootNode() != null) {
            System.out.println(getRootNode());
            getRootNode().getChildTreeList().print();
        }
    }

    //
    // Conversion functions
    //

    /**
     * Reverse this tree. A reversal is done by converting this tree to lists
     * and then reversing each of the lists. The structure of the tree is
     * unaffected by this operation.
     */
    public PointerTargetNodeList[] reverse() {
        List list = toList();
        if (list != null) {
            PointerTargetNodeList[] reversedLists = new PointerTargetNodeList[list.size()];
            for (int i = 0; i < reversedLists.length; i++) {
                reversedLists[i] = ((PointerTargetNodeList) list.get(i)).reverse();
            }
            return reversedLists;
        }
        return null;
    }

    /**
     * Convert this tree to an List of PointerTargetNodeLists. This creates one list for each
     * unique path through the tree.
     */
    public List toList() {
        List list = getRootNode().toList(new PointerTargetNodeList());
        // since the tree could have been made up of multiple types, we need to set the type of
        // the root node now that we're breaking the tree down into lists that can only be of
        // one type
        for (Object aList : list) {
            PointerTargetNodeList l = (PointerTargetNodeList) aList;
            if (l.size() >= 2) {
                PointerTargetNode root = (PointerTargetNode) l.get(0);
                PointerTargetNode node = (PointerTargetNode) l.get(1);
                root.setType(node.getType());
            }
        }
        return list;
    }
}