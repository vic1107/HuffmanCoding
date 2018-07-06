package com.shpp.p2p.cs.vicshymko.assignment15;


/**
 * Building block of Huffman tree.
 */
class Node implements Constants {
    private Byte value;
    private Node leftChild;
    private Node rightChild;
    private int weight;

    /**
     * Node without value, contain other nodes
     *
     * @param weight defines how often this node and it's members appears in the data
     * @param leftChild Node
     * @param rightChild Node
     */
    Node(int weight, Node leftChild, Node rightChild) {
        this.weight = weight;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        value = null;
    }

    /**
     * Node with value,
     * without children
     *
     * @param value byte value
     * @param weight frequency
     */
    Node(Byte value, int weight) {
        this.value = value;
        leftChild = null;
        rightChild = null;
        this.weight = weight;
    }

    public long getWeight() {
        return weight;
    }

    /**
     * Return left child of current Node.
     * Child can be accessed only once.
     *
     * @return left child Node
     */
    public Node popLeftChild() {
        if (leftChild == null) {
            return null;
        }
        else {
            Node tmp = leftChild;
            leftChild = null;
            return tmp;
        }
    }

    /**
     * Return left child of current Node.
     * Child can be accessed only once.
     *
     * @return left child Node
     */
    public Node popRightChild() {
        if (rightChild == null) {
            return null;
        }
        else {
            Node tmp = rightChild;
            rightChild = null;
            return tmp;
        }
    }

    public boolean hasValue() {
        return value != null;
    }

    public Byte getByteValue() {
        return value;
    }

    public int getByteFrequency() {
        return weight;
    }

    public boolean isSingle() {
        return rightChild == null && leftChild == null;
    }
}

