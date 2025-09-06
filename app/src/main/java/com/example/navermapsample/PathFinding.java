package com.example.navermapsample;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class PathFinding {

    private Bitmap mapBitmap;
    private int width, height;
    private Set<String> closedList;
    private Map<String, Node> nodeCache;

    public PathFinding(Bitmap mapBitmap) {
        this.mapBitmap = mapBitmap;
        this.width = mapBitmap.getWidth();
        this.height = mapBitmap.getHeight();
        this.closedList = new HashSet<>();
        this.nodeCache = new HashMap<>();
    }

    public List<Node> findPath(float startRatioX, float startRatioY, float endRatioX, float endRatioY) {
        int startX = (int) (startRatioX * width);
        int startY = (int) (startRatioY * height);
        int endX = (int) (endRatioX * width);
        int endY = (int) (endRatioY * height);

        Node startNode = getNode(startX, startY);
        Node endNode = getNode(endX, endY);
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(node -> node.f));
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();
            String currentKey = currentNode.x + "," + currentNode.y;
            closedList.add(currentKey);

            if (currentNode.equals(endNode)) {
                return constructPath(currentNode);
            }

            for (Node successor : identifySuccessors(currentNode, endNode)) {
                String successorKey = successor.x + "," + successor.y;
                if (!closedList.contains(successorKey)) {
                    int tentativeG = currentNode.g + heuristic(currentNode, successor);

                    if (tentativeG < successor.g || !openList.contains(successor)) {
                        successor.g = tentativeG;
                        successor.h = heuristic(successor, endNode);
                        successor.f = successor.g + successor.h;
                        successor.parent = currentNode;

                        if (!openList.contains(successor)) {
                            openList.add(successor);
                        }
                    }
                }
            }
        }

        return null; // No path found
    }

    private List<Node> identifySuccessors(Node node, Node endNode) {
        List<Node> successors = new ArrayList<>();
        int dx, dy;

        for (int[] dir : getDirections(node)) {
            dx = dir[0];
            dy = dir[1];
            Node jumpNode = jump(node.x, node.y, dx, dy, endNode);
            if (jumpNode != null) {
                successors.add(jumpNode);
            }
        }

        return successors;
    }

    private Node jump(int x, int y, int dx, int dy, Node endNode) {
        int newX = x + dx;
        int newY = y + dy;

        if (!isValidLocation(newX, newY) || !isWalkable(newX, newY)) {
            return null;
        }

        Node currentNode = getNode(newX, newY);
        if (currentNode.equals(endNode)) {
            return currentNode;
        }

        if (dx != 0 && dy != 0) {
            // 대각선 방향일 때
            if (!isWalkable(x, newY) || !isWalkable(newX, y)) {
                return null; // 두 방향 중 하나라도 막혀 있으면 점프하지 않음
            }
        }

        return currentNode;
    }

    private int[][] getDirections(Node node) {
        int dx = 0, dy = 0;

        if (node.parent == null) {
            return new int[][]{
                    {-1, 0}, {1, 0}, {0, -1}, {0, 1}, // 상하좌우
                    {-1, -1}, {1, 1}, {-1, 1}, {1, -1} // 대각선
            };
        } else {
            dx = (node.x - node.parent.x) / Math.max(Math.abs(node.x - node.parent.x), 1);
            dy = (node.y - node.parent.y) / Math.max(Math.abs(node.y - node.parent.y), 1);
            if (dx != 0 && dy != 0) {
                return new int[][]{{dx, dy}, {0, dy}, {dx, 0}}; // 대각선 방향
            } else if (dx != 0) {
                return new int[][]{{dx, dy}, {0, 1}, {0, -1}}; // 좌우 방향
            } else if (dy != 0) {
                return new int[][]{{dx, dy}, {1, 0}, {-1, 0}}; // 상하 방향
            } else {
                return new int[][]{{dx, dy}}; // 정지
            }
        }
    }

    private List<Node> constructPath(Node endNode) {
        List<Node> path = new ArrayList<>();
        Node currentNode = endNode;

        while (currentNode != null) {
            path.add(0, currentNode);
            currentNode = currentNode.parent;
        }

        return path;
    }

    private boolean isValidLocation(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private boolean isWalkable(int x, int y)
    {
        return mapBitmap.getPixel(x, y) == Color.WHITE;
    }

    private int heuristic(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); // 맨해튼 거리
    }

    private Node getNode(int x, int y) {
        String key = x + "," + y;
        if (!nodeCache.containsKey(key)) {
            nodeCache.put(key, new Node(x, y));
        }
        return nodeCache.get(key);
    }
}
