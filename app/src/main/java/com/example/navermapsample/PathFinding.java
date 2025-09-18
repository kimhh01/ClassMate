package com.example.navermapsample;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class PathFinding {

    private Bitmap mapBitmap;
    private int width, height;

    public PathFinding(Bitmap mapBitmap) {
        this.mapBitmap = mapBitmap;
        this.width = mapBitmap.getWidth();
        this.height = mapBitmap.getHeight();
    }

    public List<Node> findPath(float startRatioX, float startRatioY, float endRatioX, float endRatioY, int roomNumber) {
        int startX = (int) (startRatioX * width);
        int startY = (int) (startRatioY * height);
        int endX = (int) (endRatioX * width);
        int endY = (int) (endRatioY * height);

        List<Node> path = new ArrayList<>();


        if(roomNumber == RoomConstants.ROOM_206){
            Node waypoint = WayPoint.Building1Floor2WayPoints.get(0); // 첫 번째 노드
            int waypointX = (int) (waypoint.x * width);
            int waypointY = (int) (waypoint.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
            startX = waypointX;
            startY = waypointY;
        }// 강의실 번호가 209일 경우에만 경유지 추가
        else if (roomNumber == RoomConstants.ROOM_209) {
            Node waypoint = WayPoint.Building1Floor2WayPoints.get(1); // 두 번째 노드
            int waypointX = (int) (waypoint.x * width);
            int waypointY = (int) (waypoint.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
            startX = waypointX;
            startY = waypointY;
        }
        // 강의실 번호가 202일 경우에만 경유지 추가
        else if (roomNumber == RoomConstants.ROOM_202) {
            Node waypoint = WayPoint.Building1Floor2WayPoints.get(2); // 두 번째 노드
            int waypointX = (int) (waypoint.x * width);
            int waypointY = (int) (waypoint.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
            startX = waypointX;
            startY = waypointY;
        }
        // 강의실 번호가 204일 경우에만 경유지 추가
        else if (roomNumber == RoomConstants.ROOM_204) {
            Node waypoint1 = WayPoint.Building1Floor2WayPoints.get(0); // 첫 번째 노드
            int waypoint1X = (int) (waypoint1.x * width);
            int waypoint1Y = (int) (waypoint1.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
            startX = waypoint1X;
            startY = waypoint1Y;

            // 두 번째 노드 (0.5f, 0.45f)
            Node waypoint2 = WayPoint.Building1Floor2WayPoints.get(1);
            int waypoint2X = (int) (waypoint2.x * width);
            int waypoint2Y = (int) (waypoint2.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
            startX = waypoint2X;
            startY = waypoint2Y;
        }
        // 강의실 번호가 203일 경우에만 경유지 추가
        else if (roomNumber == RoomConstants.ROOM_203) {
            // 첫 번째 노드
            Node waypoint1 = WayPoint.Building1Floor2WayPoints.get(0);
            int waypoint1X = (int) (waypoint1.x * width);
            int waypoint1Y = (int) (waypoint1.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
            startX = waypoint1X;
            startY = waypoint1Y;

            // 두 번째 노드
            Node waypoint2 = WayPoint.Building1Floor2WayPoints.get(1);
            int waypoint2X = (int) (waypoint2.x * width);
            int waypoint2Y = (int) (waypoint2.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
            startX = waypoint2X;
            startY = waypoint2Y;

            // 세 번째 노드
            Node waypoint3 = WayPoint.Building1Floor2WayPoints.get(3); // 추가된 노드3
            int waypoint3X = (int) (waypoint3.x * width);
            int waypoint3Y = (int) (waypoint3.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypoint3X, waypoint3Y));
            startX = waypoint3X;
            startY = waypoint3Y;
        }
        else if (roomNumber == RoomConstants.ROOM_201){
            Node waypoint = WayPoint.Building1Floor2WayPoints.get(4); // 다섯 번째 노드
            int waypointX = (int) (waypoint.x * width);
            int waypointY = (int) (waypoint.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
            startX = waypointX;
            startY = waypointY;
        }
        else if (roomNumber == RoomConstants.ROOM_205){
            Node waypoint = WayPoint.Building1Floor2WayPoints.get(3); // 두 번째 노드
            int waypointX = (int) (waypoint.x * width);
            int waypointY = (int) (waypoint.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
            startX = waypointX;
            startY = waypointY;
        }

        // 마지막 경유지에서 최종 목적지까지의 경로를 추가합니다
        path.addAll(getPathBetweenPoints(startX, startY, endX, endY));

        return path; // 전체 경로를 반환

    }

    private List<Node> getPathBetweenPoints(int startX, int startY, int endX, int endY) {
        List<Node> subPath = new ArrayList<>();
        int dx = endX - startX;
        int dy = endY - startY;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        float xIncrement = (float) dx / steps;
        float yIncrement = (float) dy / steps;

        for (int i = 0; i <= steps; i++) {
            int x = (int) (startX + xIncrement * i);
            int y = (int) (startY + yIncrement * i);
            if (isValidLocation(x, y) && isWalkable(x, y)) {
                subPath.add(getNode(x, y));
            }
        }

        return subPath; // 두 점 사이의 경로를 반환
    }

    private boolean isValidLocation(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private boolean isWalkable(int x, int y) {
        return true;
    }


    private Node getNode(int x, int y) {
        return new Node(x, y); // Node 객체 생성
    }
}
