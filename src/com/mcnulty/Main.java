package com.mcnulty;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {

        File file = new File("input.txt");
        System.out.println(file.getAbsolutePath());
        MazeFileReader mazeFileReader = new MazeFileReader(file);
        MazeSolver mazeSolver = new MazeSolver(mazeFileReader.getMaze());

        mazeSolver.solve();

    }
}

class MazeFileReader {
    private File mazeFile;

    MazeFileReader(File mazeFile) {

        this.mazeFile = mazeFile;
    }


    public Maze getMaze() throws IOException {

        Maze maze;
        try (BufferedReader br = new BufferedReader(new FileReader(mazeFile))) {

            String widthHeightLine = br.readLine();
            int width, height;
            width = Integer.parseInt(widthHeightLine.split(" ")[0]);
            height = Integer.parseInt(widthHeightLine.split(" ")[1]);

            String startPosLine = br.readLine();
            int startX, startY;
            startX = Integer.parseInt(startPosLine.split(" ")[0]);
            startY = Integer.parseInt(startPosLine.split(" ")[1]);

            String endPosLine = br.readLine();
            int endX, endY;
            endX = Integer.parseInt(endPosLine.split(" ")[0]);
            endY = Integer.parseInt(endPosLine.split(" ")[1]);

            String line;
            Set<Position> walls = new HashSet<>();
            int counter = 0;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(" ");
                for (int i = 0; i < split.length; i++) {
                    String item = split[i];
                    if (item.equals("1"))
                        walls.add(new Position(i, counter));
                }
                counter++;
            }

            maze = new Maze(new Position(startX, startY), new Position(endX, endY), walls, width, height);
        }

        return maze;
    }
}

class MazeSolver {

    private Maze maze;
    private Set<Position> solutionPositions;

    MazeSolver(Maze maze) {
        this.maze = maze;
        this.solutionPositions = new HashSet<>();
    }

    public void solve() {

        findPath(maze.getStart());

        if (solutionPositions.size() == 0) {
            System.out.println("No route found.");
            System.exit(1);
        }

        //

        String[][] output = new String[maze.getHeight()][maze.getWidth()];

        for (Position position : maze.getWalls()) {
            output[position.getY()][position.getX()] = "1 ";
        }
        for (Position solutionPosition : solutionPositions) {
            output[solutionPosition.getY()][solutionPosition.getX()] = "X ";
        }
        output[maze.getStart().getY()][maze.getStart().getX()] = "S ";
        output[maze.getEnd().getY()][maze.getEnd().getX()] = "E ";

        for (String[] strings : output) {
            for (String string : strings) {
                System.out.print(string != null ? string : "  ");
            }
            System.out.println();
        }
    }


    private boolean findPath(Position position) {

        if (maze.getEnd().equals(position)) return true;
        if (maze.getWalls().contains(position) //hit a wall
                || position.getY() < 0 || position.getX() < 0 //outside bounds
                || solutionPositions.contains(position)) // previously traversed
            return false;

        solutionPositions.add(position); //potentially correct route ?

        Position north = new Position(position.getX(), position.getY() - 1);
        Position east = new Position(position.getX() + 1, position.getY());
        Position south = new Position(position.getX(), position.getY() + 1);
        Position west = new Position(position.getX() - 1, position.getY());

        if (findPath(north) || findPath(east) || findPath(south) || findPath(west)) {
            return true; //correct route
        }

        solutionPositions.remove(position); //incorrect route
        return false;

    }


}

class Maze {

    private Position start;
    private Position end;
    private Set<Position> walls;
    private int width;
    private int height;

    Maze(Position start, Position end, Set<Position> walls, int width, int height) {
        this.start = start;
        this.end = end;
        this.walls = walls;
        this.width = width;
        this.height = height;
    }

    public Position getStart() {
        return start;
    }

    public Position getEnd() {
        return end;
    }

    public Set<Position> getWalls() {
        return walls;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

class Position {
    private int x;
    private int y;

    Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x &&
                y == position.y;
    }

    @Override
    public int hashCode() {

        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}