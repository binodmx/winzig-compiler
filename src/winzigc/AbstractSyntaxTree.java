package winzigc;

public class AbstractSyntaxTree {
    Node root;

    public AbstractSyntaxTree(Node root) {
        this.root = root;
    }

    public void print() {
        this.preOrderTraverse(root, 0);
    }

    private void preOrderTraverse(Node node, int n) {
        if (node != null) {
            for (int i = 0; i < n; i++) {System.out.print(". ");}
            System.out.println(node.getData() + "(" + node.getN() + ")");
            this.preOrderTraverse(node.getLeftChild(), n + 1);
            this.preOrderTraverse(node.getRightChild(), n);
        }
    }

}
