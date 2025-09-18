package com.example.navermapsample;

public class Node {
    public float x; // x 좌표 (비율)
    public float y; // y 좌표 (비율)
    public Node parent;
    public int g, h, f;

    // Node 생성자
    public Node(float x, float y) {
        this.x = x;
        this.y = y;
        this.parent = null;
        this.g = 0;
        this.h = 0;
        this.f = 0;
    }

    // x 좌표를 반환하는 메소드
    public float getX() {
        return x;
    }

    // y 좌표를 반환하는 메소드
    public float getY() {
        return y;
    }

    // Node의 equals 메서드 오버라이드
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Node)) return false;
        Node other = (Node) obj;
        return Float.compare(this.x, other.x) == 0 && Float.compare(this.y, other.y) == 0;
    }

    // Node의 hashCode 메서드 오버라이드
    @Override
    public int hashCode() {
        return Float.floatToIntBits(x) ^ Float.floatToIntBits(y);
    }
}
