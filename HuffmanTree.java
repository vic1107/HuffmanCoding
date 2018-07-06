package com.shpp.p2p.cs.vicshymko.assignment15;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * This class creates Huffman tree and generates Huffman codes.
 */
public class HuffmanTree implements Constants {
    /**
     * Frequencies of appearing every byte in file.
     */
    private int[] bytesFrequencies;
    /**
     * List of nodes sorted by their frequencies.
     */
    private PriorityQueue<Node> sortedNodes;

    HuffmanTree(int[] frequencies) {
        bytesFrequencies = frequencies;
    }

    /**
     * @return new associative table with byte - Huffman code entry
     */
    public HashMap<Byte, String> getDictionary() {
        sortedNodes = sortFrequencies(bytesFrequencies);
        Node treeRoot = buildTree(sortedNodes);
        return makeDictionary(treeRoot);
    }

    /**
     * @return nodes sorted by their frequency
     */
    public PriorityQueue<Node> getSortedNodes() {
        return sortedNodes;
    }

    /**
     * Adding all bytes and frequencies to PriorityQueue
     * List is sorted cause of given Comparator
     *
     * @param oneBytesFrequencies array of frequencies
     * @return sorted list
     */
    private PriorityQueue<Node> sortFrequencies(int[] oneBytesFrequencies) {
        PriorityQueue<Node> sortedFrequencies =
                new PriorityQueue<>(Comparator.comparingInt(Node::getByteFrequency));
        int frequency;
        for (int value = Byte.MIN_VALUE; value <= Byte.MAX_VALUE; value++) {
            frequency = oneBytesFrequencies[value+128];
            if (frequency != 0){
                sortedFrequencies.add(new Node((byte) value, frequency));
            }
        }
        return sortedFrequencies;
    }

    /**
     * Build tree from given list in accordance with static Huffman algorithm
     *
     * @param bytesFrequencies frequencies are sorted
     * @return reference to root Node of tree
     */
    private Node buildTree(PriorityQueue bytesFrequencies) {
        PriorityQueue<Node> frequencies = new PriorityQueue<Node>(bytesFrequencies);//we'll change copy

        /* building tree */
        while (frequencies.size() != 1) {
            Node entry1 = frequencies.poll();
            Node entry2 = frequencies.poll();
            int weight = entry1.getByteFrequency() + entry2.getByteFrequency();
            /* creating new Node with references to left and right child */
            frequencies.add(new Node(weight, entry1, entry2)); // putting new node to sorted list
        }
        /* there only one Node left in list that contains references to other Nodes and bytes */
        return frequencies.poll();
    }

    /**
     * Traversing tree using iterative dfs and creating hashMap of new values
     *
     * @param treeRoot reference to root of tree
     * @return hashMap with old - new value
     */
    private HashMap<Byte, String> makeDictionary(Node treeRoot) {
        HashMap<Byte, String> dict = new HashMap<>();

        /* if node has no children */
        if (treeRoot.isSingle()) {
            dict.put(treeRoot.getByteValue(), "0");
            return dict;
        }
        ArrayDeque<Node> stack = new ArrayDeque<>();//holds nodes
        StringBuilder path = new StringBuilder(); //holds 1s and 0s for new byte creating
        stack.push(treeRoot);

        while (!stack.isEmpty()) {
            Node current = stack.peek();
            Node child;
            if ((child = current.popLeftChild()) != null){
                if (child.hasValue())
                    dict.put(child.getByteValue(), path.toString() + "0");
                else {
                    stack.push(child);
                    path.append("0");
                    continue;
                }
            }
            if ((child = current.popRightChild()) != null){
                if (child.hasValue())
                    dict.put(child.getByteValue(), path.toString() + "1");
                else {
                    stack.push(child);
                    path.append("1");
                    continue;
                }
            }
            if (stack.size() > 1)
                path.deleteCharAt(path.length()-1);
            stack.pop();
        }
        return dict;
    }

}
