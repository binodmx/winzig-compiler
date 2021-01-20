public class Node {
    private String data;
    private Node leftChild;
    private Node rightChild;
    private int n;

    public Node(String data, Node leftChild, Node rightChild, int n) {
        this.data = data;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.n = n;
    }

    public String getData() {
        return data;
    }

    public Node getLeftChild() {
        return leftChild;
    }

    public Node getRightChild() {
        return rightChild;
    }

    public int getN() {
        return n;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
    }

}
