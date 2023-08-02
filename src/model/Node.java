package model;

public class Node {

        private Node next;
        private Node prev;
        private Task value;

        public Node(Node prev, Task value) {
                this.prev = prev;
                this.value = value;
        }

        public Node getNext() {
                return next;
        }

        public void setNext(Node next) {
                this.next = next;
        }

        public Node getPrev() {
                return prev;
        }

        public void setPrev(Node prev) {
                this.prev = prev;
        }

        public Task getValue() {
                return value;
        }

        public void setValue(Task value) {
                this.value = value;
        }

}
