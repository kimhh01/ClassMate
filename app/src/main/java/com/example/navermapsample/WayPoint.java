package com.example.navermapsample;

import java.util.ArrayList;
import java.util.List;

public class WayPoint {

    // Building1의 각 층에 대한 경유지 리스트
    public static final List<Node> Building1Floor1WayPoints = new ArrayList<>();
    public static final List<Node> Building1Floor2WayPoints = new ArrayList<>();
    public static final List<Node> Building1Floor3WayPoints = new ArrayList<>();
    public static final List<Node> Building1Floor4WayPoints = new ArrayList<>();
    public static final List<Node> Building1Floor5WayPoints = new ArrayList<>();

    // Building2의 각 층에 대한 경유지 리스트
    public static final List<Node> Building2Floor1WayPoints = new ArrayList<>();
    public static final List<Node> Building2Floor2WayPoints = new ArrayList<>();
    public static final List<Node> Building2Floor3WayPoints = new ArrayList<>();
    public static final List<Node> Building2Floor4WayPoints = new ArrayList<>();
    public static final List<Node> Building2Floor5WayPoints = new ArrayList<>();

    static {
        // 예시: Building1 - 2층
        Building1Floor2WayPoints.add(new Node(0.43f, 0.47f));  // 노드1. 206호 앞
        Building1Floor2WayPoints.add(new Node(0.46f, 0.47f));  // 노드2. 204호 앞
        Building1Floor2WayPoints.add(new Node(0.51f, 0.47f));  // 노드3. 202호 앞
        Building1Floor2WayPoints.add(new Node(0.71f, 0.47f));  // 노드4. 205호 앞
        Building1Floor2WayPoints.add(new Node(0.96f, 0.47f));  // 노드5. 201호 앞

        // 예시: Building2 - 3층
        Building2Floor3WayPoints.add(new Node(0.6f, 0.5f));    // 노드3
        Building2Floor3WayPoints.add(new Node(0.65f, 0.55f));  // 노드4

        // 필요한 만큼 경유지 추가
    }

    // 특정 노드를 수정할 때 사용하는 메서드
    public static void updateWaypoint(List<Node> waypointList, int index, float x, float y) {
        if (index >= 0 && index < waypointList.size()) {
            waypointList.set(index, new Node(x, y));
        }
    }

    // 특정 건물과 층의 경유지 리스트를 반환하는 메서드
    public static List<Node> getWaypoints(String building, int floor) {
        switch (building) {
            case "Building1":
                switch (floor) {
                    case 1:
                        return Building1Floor1WayPoints;
                    case 2:
                        return Building1Floor2WayPoints;
                    case 3:
                        return Building1Floor3WayPoints;
                    case 4:
                        return Building1Floor4WayPoints;
                    case 5:
                        return Building1Floor5WayPoints;
                }
                break;
            case "Building2":
                switch (floor) {
                    case 1:
                        return Building2Floor1WayPoints;
                    case 2:
                        return Building2Floor2WayPoints;
                    case 3:
                        return Building2Floor3WayPoints;
                    case 4:
                        return Building2Floor4WayPoints;
                    case 5:
                        return Building2Floor5WayPoints;
                }
                break;
            // 필요한 만큼 추가 가능
        }
        return new ArrayList<>(); // 빈 목록 반환
    }

    // 예시로 Building1의 2층의 첫 번째 노드(노드1)를 수정하는 코드
    static {
        //updateWaypoint(Building1Floor2WayPoints, 0, 0.475f, 0.47f);
    }
}
