package com.example.navermapsample;

import android.graphics.Bitmap;

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
        int currentStartX = startX;
        int currentStartY = startY;

        if (roomNumber == RoomConstants.ROOM_106){
            Node waypoint = WayPoint.Building1Floor1WayPoints.get(0);
            int waypointX = (int) (waypoint.x * width);
            int waypointY = (int) (waypoint.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
            startX = waypointX;
            startY = waypointY;
        }
        else if (roomNumber == RoomConstants.ROOM_104){
            Node waypoint = WayPoint.Building1Floor1WayPoints.get(1);
            int waypointX = (int) (waypoint.x * width);
            int waypointY = (int) (waypoint.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
            startX = waypointX;
            startY = waypointY;
        }else if (roomNumber == RoomConstants.ROOM_107){
            Node waypoint = WayPoint.Building1Floor1WayPoints.get(2);
            int waypointX = (int) (waypoint.x * width);
            int waypointY = (int) (waypoint.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
            startX = waypointX;
            startY = waypointY;
        }else if (roomNumber == RoomConstants.ROOM_105){
            Node waypoint = WayPoint.Building1Floor1WayPoints.get(3);
            int waypointX = (int) (waypoint.x * width);
            int waypointY = (int) (waypoint.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
            startX = waypointX;
            startY = waypointY;
        }else if (roomNumber == RoomConstants.ROOM_103){
            Node waypoint = WayPoint.Building1Floor1WayPoints.get(4);
            int waypointX = (int) (waypoint.x * width);
            int waypointY = (int) (waypoint.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
            startX = waypointX;
            startY = waypointY;
        }else if (roomNumber == RoomConstants.ROOM_102){
            Node waypoint = WayPoint.Building1Floor1WayPoints.get(5);
            int waypointX = (int) (waypoint.x * width);
            int waypointY = (int) (waypoint.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
            startX = waypointX;
            startY = waypointY;
        }
        else if (roomNumber == RoomConstants.ROOM_109){
            Node waypoint = WayPoint.Building1Floor1WayPoints.get(6);
            List<Node> entryWayPoints;
            int waypointX = (int) (waypoint.x * width);
            int waypointY = (int) (waypoint.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
            startX = waypointX;
            startY = waypointY;
        }
        else if(roomNumber == RoomConstants.ROOM_206){
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
            // 두 번째 노드 (0.5f, 0.45f)
            Node waypoint = WayPoint.Building1Floor2WayPoints.get(1);
            int waypointX = (int) (waypoint.x * width);
            int waypointY = (int) (waypoint.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
            startX = waypointX;
            startY = waypointY;
        }
        // 강의실 번호가 203일 경우에만 경유지 추가
        else if (roomNumber == RoomConstants.ROOM_203) {
            Node waypoint = WayPoint.Building1Floor2WayPoints.get(4);
            int waypointX = (int) (waypoint.x * width);
            int waypointY = (int) (waypoint.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
            startX = waypointX;
            startY = waypointY;
        }
        else if (roomNumber == RoomConstants.ROOM_201){
            Node waypoint = WayPoint.Building1Floor2WayPoints.get(5); // 다섯 번째 노드
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
        else if(roomNumber >= RoomConstants.ROOM_3001 && roomNumber <= RoomConstants.ROOM_3007){
            float startXRatio = (float) startX / width;
            if(startXRatio < 0.45f){
                if (roomNumber<RoomConstants.ROOM_3006){ // 3006호보다 작은, 즉 3001~3005호를 입력했을 때
                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(0);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
                else{                                  // 3006호보다 큰, 즉 3006,3007호를 입력했을 때
                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(13);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
            }
            else if (startXRatio >= 0.45f && startXRatio<0.55){ // 상경학관 1층 중앙입구에서
                if (roomNumber<RoomConstants.ROOM_3006){ // 3006호보다 작은, 즉 3001~3005호를 입력했을 때
                    // 첫 번째 노드
                    Node waypoint1 = WayPoint.Building2Floor1CenterEntryWayPoints.get(0);
                    int waypoint1X = (int) (waypoint1.x * width);
                    int waypoint1Y = (int) (waypoint1.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
                    startX = waypoint1X;
                    startY = waypoint1Y;

                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(0);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
                else{                                  // 3006호보다 큰, 즉 3006,3007호를 입력했을 때
                    // 첫 번째 노드
                    Node waypoint1 = WayPoint.Building2Floor1CenterEntryWayPoints.get(0);
                    int waypoint1X = (int) (waypoint1.x * width);
                    int waypoint1Y = (int) (waypoint1.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
                    startX = waypoint1X;
                    startY = waypoint1Y;

                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(13);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
            }
            else{
                if (roomNumber<RoomConstants.ROOM_3006){ // 3006호보다 작은, 즉 3001~3005호를 입력했을 때
                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(0);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
                else{                                  // 3006호보다 큰, 즉 3006,3007호를 입력했을 때
                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(13);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
            }
        }
        else if (roomNumber >= RoomConstants.ROOM_3101 && roomNumber <= RoomConstants.ROOM_3117){
            List<Node> entryWayPoints;
            float startXRatio = (float) startX / width;

            if (startXRatio < 0.33f) { // 상경학관 좌측 입구에서 강의실을 입력했을 경우
                entryWayPoints = WayPoint.Building2Floor1LeftEntryWayPoints;

                if (roomNumber == RoomConstants.ROOM_3101){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(0);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3102){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(1);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3103){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(2);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3104){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(3);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3106){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(4);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3105){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(5);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3109){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(6);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }else if (roomNumber == RoomConstants.ROOM_3108){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(6);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3111){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(7);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3110){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(7);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3113){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(8);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3112){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(9);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3114){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(10);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3116){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(11);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3115){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(12);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3117){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(13);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
            }
            else if(startXRatio < 0.66f){ // 상경학관 중앙 입구에서 강의실을 입력했을 경우
                if (roomNumber == RoomConstants.ROOM_3101){
                    // 첫 번째 노드
                    Node waypoint1 = WayPoint.Building2Floor1CenterEntryWayPoints.get(0);
                    int waypoint1X = (int) (waypoint1.x * width);
                    int waypoint1Y = (int) (waypoint1.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
                    startX = waypoint1X;
                    startY = waypoint1Y;

                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(0);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
                else if(roomNumber == RoomConstants.ROOM_3102){
                    // 첫 번째 노드
                    Node waypoint1 = WayPoint.Building2Floor1CenterEntryWayPoints.get(0);
                    int waypoint1X = (int) (waypoint1.x * width);
                    int waypoint1Y = (int) (waypoint1.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
                    startX = waypoint1X;
                    startY = waypoint1Y;

                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(1);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
                else if(roomNumber == RoomConstants.ROOM_3103){
                    // 첫 번째 노드
                    Node waypoint1 = WayPoint.Building2Floor1CenterEntryWayPoints.get(0);
                    int waypoint1X = (int) (waypoint1.x * width);
                    int waypoint1Y = (int) (waypoint1.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
                    startX = waypoint1X;
                    startY = waypoint1Y;

                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(2);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
                else if(roomNumber == RoomConstants.ROOM_3104){
                    // 첫 번째 노드
                    Node waypoint1 = WayPoint.Building2Floor1CenterEntryWayPoints.get(0);
                    int waypoint1X = (int) (waypoint1.x * width);
                    int waypoint1Y = (int) (waypoint1.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
                    startX = waypoint1X;
                    startY = waypoint1Y;

                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(3);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
                else if(roomNumber == RoomConstants.ROOM_3106){
                    // 첫 번째 노드
                    Node waypoint1 = WayPoint.Building2Floor1CenterEntryWayPoints.get(0);
                    int waypoint1X = (int) (waypoint1.x * width);
                    int waypoint1Y = (int) (waypoint1.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
                    startX = waypoint1X;
                    startY = waypoint1Y;

                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(4);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
                else if(roomNumber == RoomConstants.ROOM_3105){
                    // 첫 번째 노드
                    Node waypoint1 = WayPoint.Building2Floor1CenterEntryWayPoints.get(0);
                    int waypoint1X = (int) (waypoint1.x * width);
                    int waypoint1Y = (int) (waypoint1.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
                    startX = waypoint1X;
                    startY = waypoint1Y;

                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(5);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
                else if(roomNumber == RoomConstants.ROOM_3108){
                    // 첫 번째 노드
                    Node waypoint1 = WayPoint.Building2Floor1CenterEntryWayPoints.get(0);
                    int waypoint1X = (int) (waypoint1.x * width);
                    int waypoint1Y = (int) (waypoint1.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
                    startX = waypoint1X;
                    startY = waypoint1Y;

                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(6);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
                else if(roomNumber == RoomConstants.ROOM_3109){
                    // 첫 번째 노드
                    Node waypoint1 = WayPoint.Building2Floor1CenterEntryWayPoints.get(0);
                    int waypoint1X = (int) (waypoint1.x * width);
                    int waypoint1Y = (int) (waypoint1.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
                    startX = waypoint1X;
                    startY = waypoint1Y;

                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(6);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
                else if(roomNumber == RoomConstants.ROOM_3110){
                    // 첫 번째 노드
                    Node waypoint1 = WayPoint.Building2Floor1CenterEntryWayPoints.get(0);
                    int waypoint1X = (int) (waypoint1.x * width);
                    int waypoint1Y = (int) (waypoint1.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
                    startX = waypoint1X;
                    startY = waypoint1Y;

                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(7);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
                else if(roomNumber == RoomConstants.ROOM_3111){
                    // 첫 번째 노드
                    Node waypoint1 = WayPoint.Building2Floor1CenterEntryWayPoints.get(0);
                    int waypoint1X = (int) (waypoint1.x * width);
                    int waypoint1Y = (int) (waypoint1.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
                    startX = waypoint1X;
                    startY = waypoint1Y;

                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(7);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
                else if(roomNumber == RoomConstants.ROOM_3113){
                    // 첫 번째 노드
                    Node waypoint1 = WayPoint.Building2Floor1CenterEntryWayPoints.get(0);
                    int waypoint1X = (int) (waypoint1.x * width);
                    int waypoint1Y = (int) (waypoint1.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
                    startX = waypoint1X;
                    startY = waypoint1Y;

                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(8);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
                else if(roomNumber == RoomConstants.ROOM_3112){
                    // 첫 번째 노드
                    Node waypoint1 = WayPoint.Building2Floor1CenterEntryWayPoints.get(0);
                    int waypoint1X = (int) (waypoint1.x * width);
                    int waypoint1Y = (int) (waypoint1.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
                    startX = waypoint1X;
                    startY = waypoint1Y;

                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(9);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
                else if(roomNumber == RoomConstants.ROOM_3114){
                    // 첫 번째 노드
                    Node waypoint1 = WayPoint.Building2Floor1CenterEntryWayPoints.get(0);
                    int waypoint1X = (int) (waypoint1.x * width);
                    int waypoint1Y = (int) (waypoint1.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
                    startX = waypoint1X;
                    startY = waypoint1Y;

                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(10);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
                else if(roomNumber == RoomConstants.ROOM_3116){
                    // 첫 번째 노드
                    Node waypoint1 = WayPoint.Building2Floor1CenterEntryWayPoints.get(0);
                    int waypoint1X = (int) (waypoint1.x * width);
                    int waypoint1Y = (int) (waypoint1.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
                    startX = waypoint1X;
                    startY = waypoint1Y;

                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(11);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
                else if(roomNumber == RoomConstants.ROOM_3115){
                    // 첫 번째 노드
                    Node waypoint1 = WayPoint.Building2Floor1CenterEntryWayPoints.get(0);
                    int waypoint1X = (int) (waypoint1.x * width);
                    int waypoint1Y = (int) (waypoint1.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
                    startX = waypoint1X;
                    startY = waypoint1Y;

                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(12);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }
                else if(roomNumber == RoomConstants.ROOM_3117){
                    // 첫 번째 노드
                    Node waypoint1 = WayPoint.Building2Floor1CenterEntryWayPoints.get(0);
                    int waypoint1X = (int) (waypoint1.x * width);
                    int waypoint1Y = (int) (waypoint1.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint1X, waypoint1Y));
                    startX = waypoint1X;
                    startY = waypoint1Y;

                    // 두 번째 노드
                    Node waypoint2 = WayPoint.Building2Floor1LeftEntryWayPoints.get(13);
                    int waypoint2X = (int) (waypoint2.x * width);
                    int waypoint2Y = (int) (waypoint2.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypoint2X, waypoint2Y));
                    startX = waypoint2X;
                    startY = waypoint2Y;
                }


            }
            else{ // 상경학관 우측 입구에서 강의실을 입력했을 경우
                if (roomNumber == RoomConstants.ROOM_3101){
                    Node waypoint = WayPoint.Building2Floor1RightEntryWayPoints.get(0);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3102){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(1);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3103){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(2);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3104){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(3);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3106){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(4);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3105){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(5);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3109){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(6);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }else if (roomNumber == RoomConstants.ROOM_3108){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(6);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3111){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(7);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3110){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(7);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3113){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(8);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3112){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(9);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3114){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(10);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3116){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(11);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3115){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(12);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }
                else if (roomNumber == RoomConstants.ROOM_3117){
                    Node waypoint = WayPoint.Building2Floor1LeftEntryWayPoints.get(13);
                    int waypointX = (int) (waypoint.x * width);
                    int waypointY = (int) (waypoint.y * height);
                    path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
                    startX = waypointX;
                    startY = waypointY;
                }

            }

        }
        else if(roomNumber == 1){  //공학L관 1층에서 약도 중앙 기준 왼쪽 다른층을 입력했을 때 왼쪽 계단으로 안내.
            Node waypoint = WayPoint.Building1Floor1WayPoints.get(7);
            List<Node> entryWayPoints;
            int waypointX = (int) (waypoint.x * width);
            int waypointY = (int) (waypoint.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
            startX = waypointX;
            startY = waypointY;

        }
        else if(roomNumber == 2){  //공학L관 1층에서 약도 중앙 기준 왼쪽 다른층을 입력했을 때 왼쪽 계단으로 안내.
            Node waypoint = WayPoint.Building1Floor1WayPoints.get(8);
            List<Node> entryWayPoints;
            int waypointX = (int) (waypoint.x * width);
            int waypointY = (int) (waypoint.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
            startX = waypointX;
            startY = waypointY;
        }
        else if(roomNumber == 3){  //공학L관 2층에서 약도 중앙 기준 왼쪽 다른층을 입력했을 때 왼쪽 계단으로 안내.
            Node waypoint = WayPoint.Building1Floor2WayPoints.get(6);
            List<Node> entryWayPoints;
            int waypointX = (int) (waypoint.x * width);
            int waypointY = (int) (waypoint.y * height);
            path.addAll(getPathBetweenPoints(startX, startY, waypointX, waypointY));
            startX = waypointX;
            startY = waypointY;
        }
        else if(roomNumber == 4){  //공학L관 2층에서 약도 중앙 기준 오른쪽 다른층을 입력했을 때 오른쪽 계단으로 안내.
            Node waypoint = WayPoint.Building1Floor2WayPoints.get(7);
            List<Node> entryWayPoints;
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
