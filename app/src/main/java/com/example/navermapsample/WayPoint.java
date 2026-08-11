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

    public static final List<Node> Building2Floor1LeftEntryWayPoints = new ArrayList<>();
    public static final List<Node> Building2Floor1RightEntryWayPoints = new ArrayList<>();
    public static final List<Node> Building2Floor1CenterEntryWayPoints = new ArrayList<>();
    public static final List<Node> Building2Floor2WayPoints = new ArrayList<>();
    public static final List<Node> Building2Floor3WayPoints = new ArrayList<>();
    public static final List<Node> Building2Floor4WayPoints = new ArrayList<>();
    public static final List<Node> Building2Floor5WayPoints = new ArrayList<>();

    static {
        // 예시: Building1(공학관)- 1층
        Building1Floor1WayPoints.add(new Node(0.235f, 0.421f)); // 노드1. 106호 앞
        Building1Floor1WayPoints.add(new Node(0.44f, 0.421f)); // 노드2. 104호 앞
        Building1Floor1WayPoints.add(new Node(0.460f, 0.421f)); // 노드3. 107호 앞
        Building1Floor1WayPoints.add(new Node(0.497f, 0.421f)); // 노드4. 105호 앞
        Building1Floor1WayPoints.add(new Node(0.554f, 0.421f)); // 노드5. 103호 앞
        Building1Floor1WayPoints.add(new Node(0.625f, 0.421f)); // 노드6. 102호 앞
        Building1Floor1WayPoints.add(new Node(0.801f, 0.421f)); // 노드6. 109호 앞
        Building1Floor1WayPoints.add(new Node(0.113f, 0.421f)); // 노드7. 왼쪽 계단 앞
        Building1Floor1WayPoints.add(new Node(0.812f, 0.421f)); // 노드8. 오른쪽 계단 앞
        // 예시: Building1(공학관) - 2층
        Building1Floor2WayPoints.add(new Node(0.431f, 0.474f));  // 노드0. 206호 앞
        Building1Floor2WayPoints.add(new Node(0.462f, 0.474f));  // 노드1. 204호 앞
        Building1Floor2WayPoints.add(new Node(0.521f, 0.474f));  // 노드2. 202호 앞
        Building1Floor2WayPoints.add(new Node(0.631f, 0.474f));  // 노드3. 205호 앞
        Building1Floor2WayPoints.add(new Node(0.75f, 0.474f));  // 노드4. 203호 앞
        Building1Floor2WayPoints.add(new Node(0.845f, 0.474f));  // 노드5. 201호 앞
        Building1Floor2WayPoints.add(new Node(0.145f, 0.474f));  // 노드6. 왼쪽 계단 앞
        Building1Floor2WayPoints.add(new Node(0.847f, 0.474f));  // 노드7. 오른쪽 계단 앞

        // 예시: Building2(상경학관) - 1층 좌측 입구 입장시 노드
        Building2Floor1LeftEntryWayPoints.add(new Node(0.127f, 0.49f));    // 노드0. 3101호 앞
        Building2Floor1LeftEntryWayPoints.add(new Node(0.173f, 0.49f));    // 노드1. 3102호 앞
        Building2Floor1LeftEntryWayPoints.add(new Node(0.217f, 0.49f));    // 노드2. 3103호 앞
        Building2Floor1LeftEntryWayPoints.add(new Node(0.347f, 0.49f));    // 노드3. 3104호 앞
        Building2Floor1LeftEntryWayPoints.add(new Node(0.375f, 0.49f));    // 노드4. 3106호 앞
        Building2Floor1LeftEntryWayPoints.add(new Node(0.396f, 0.49f));    // 노드5. 3105호 앞
        Building2Floor1LeftEntryWayPoints.add(new Node(0.443f, 0.49f));    // 노드6. 3108, 3109호 앞
        Building2Floor1LeftEntryWayPoints.add(new Node(0.553f, 0.49f));    // 노드7. 3110, 3111호 앞
        Building2Floor1LeftEntryWayPoints.add(new Node(0.605f, 0.49f));    // 노드8. 3113호 앞
        Building2Floor1LeftEntryWayPoints.add(new Node(0.620f, 0.49f));    // 노드9. 3112호 앞
        Building2Floor1LeftEntryWayPoints.add(new Node(0.665f, 0.49f));    // 노드10. 3114호 앞
        Building2Floor1LeftEntryWayPoints.add(new Node(0.693f, 0.49f));    // 노드11. 3116호 앞
        Building2Floor1LeftEntryWayPoints.add(new Node(0.774f, 0.49f));    // 노드12. 3115호 앞
        Building2Floor1LeftEntryWayPoints.add(new Node(0.892f, 0.49f));    // 노드13. 3117호 앞

        // 예시: Building2(상경학관) - 1층 우측 입구 입장시 노드
        Building2Floor1RightEntryWayPoints.add(new Node(0.127f, 0.49f));    // 노드0. 3101호 앞
        Building2Floor1RightEntryWayPoints.add(new Node(0.217f, 0.49f));    // 노드1. 3102호 앞
        Building2Floor1RightEntryWayPoints.add(new Node(0.248f, 0.49f));    // 노드2. 3103호 앞
        Building2Floor1RightEntryWayPoints.add(new Node(0.375f, 0.49f));    // 노드3. 3104호 앞
        Building2Floor1RightEntryWayPoints.add(new Node(0.4f, 0.49f));    // 노드4. 3106호 앞
        Building2Floor1RightEntryWayPoints.add(new Node(0.416f, 0.49f));    // 노드5. 3105호 앞
        Building2Floor1RightEntryWayPoints.add(new Node(0.457f, 0.49f));    // 노드6. 3108, 3109호 앞
        Building2Floor1RightEntryWayPoints.add(new Node(0.555f, 0.49f));    // 노드7. 3110, 3111호 앞
        Building2Floor1RightEntryWayPoints.add(new Node(0.6f, 0.49f));    // 노드8. 3113호 앞
        Building2Floor1RightEntryWayPoints.add(new Node(0.618f, 0.49f));    // 노드9. 3112호 앞
        Building2Floor1RightEntryWayPoints.add(new Node(0.659f, 0.49f));    // 노드10. 3114호 앞
        Building2Floor1RightEntryWayPoints.add(new Node(0.681f, 0.49f));    // 노드11. 3116호 앞
        Building2Floor1RightEntryWayPoints.add(new Node(0.76f, 0.49f));    // 노드12. 3115호 앞
        Building2Floor1RightEntryWayPoints.add(new Node(0.87f, 0.49f));    // 노드13. 3117호 앞

        // 예시: Building2(상경학관) - 1층 중앙 입구 입장시 노드
        Building2Floor1CenterEntryWayPoints.add(new Node(0.5f, 0.49f));   // 노드0. 중앙입구 앞(나머지는 image가 같으니 Building2Floor1RightEntryWayPoints리스트 활용.)


        // 예시: Building2 - 3층
        Building2Floor3WayPoints.add(new Node(0.65f, 0.55f));  // 노드0

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
                        return Building2Floor1LeftEntryWayPoints;
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
