package com.company;

import java.util.ArrayList;
import java.util.Scanner;

/*
Basic console minesweeper game, has basic ground reveal, flagging, and random mine generation:
Possible immediate improvements:
* OOP for the grid locations, so no need to use sloppy integer arrays
* Resizeable grid and mines, only needs user input to enable it
* Input validation, system currently relies on perfect data entry
* Starting location should be on empty grid location, no adjacent mines
 */
public class Main {
    static final int X=0,Y=1;//array indices
    static final int MINE=-1;
    static final int NEIGHBORS=8;
    static final int UL=-1,NONE=0,BR=1;//left,right,up,down directions
    static final int FLAG=1,HIDDEN=0,REVEALED=2;
    static final String FLAGUNICODE="\u2691";
    static boolean start=false,quit=false;
    static int[][] grid;
    static int[][] revealed;
    static int[][] neighbors = {{UL,UL},{NONE,UL},{BR,UL},{UL,NONE},{BR,NONE},{UL,BR},{NONE,BR},{BR,BR}};
    //neighbor list for looping
    static int sizeX,sizeY;
    static int mines;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        sizeX=10;
        sizeY=10;
        mines=10;
        while(!quit){
            String[]inputArray=new String[3];
            if(start) {
                System.out.println("select square (x,y [,[f]lag])");
                inputArray = scanner.next().split(",");
                int x = Integer.parseInt(inputArray[0])-1;
                int y = Integer.parseInt(inputArray[1])-1;
                if(inputArray.length == 3 && inputArray[2].matches("f(lag)?")){
                    if(revealed[x][y]==FLAG){
                        revealed[x][y]=HIDDEN;
                    }
                    else if(revealed[x][y]==HIDDEN){
                        revealed[x][y] = FLAG;
                    }
                    displayGrid();
                }
                else{
                    if(grid[x][y]==MINE){
                        System.out.println("GAME OVER");
                        start=false;
                    }
                    else{
                         reveal(x,y);
                         displayGrid();
                         checkVictory();
                    }
                }
            }
            else{
                grid=new int[sizeX][sizeY];
                revealed = new int[sizeX][sizeY];
                System.out.println("select square (x,y)");
                inputArray = scanner.next().split(",");
                generate(mines,Integer.parseInt(inputArray[0])-1,Integer.parseInt(inputArray[1])-1);
                displayGrid();
                start=true;
            }
        }
    }
    public static void displayGrid(){
        StringBuffer border=new StringBuffer();
        /*
        for(int i=0;i<sizeX;i++){
            border.append("--");
        }
        border.append("-");
        */
        for(int i=0;i<sizeX;i++){
            border.append("|");
            border.append((i+1));
        }
        border.append("|");
        System.out.println("   "+border.toString());
        System.out.println("----------------------------------------------");
        for(int y=0; y<sizeY; y++){
            System.out.print(String.format("%3d",y+1));
            for(int x=0;x<sizeX;x++) {
                System.out.print("|");
                if (revealed[x][y] == REVEALED) {
                    if (grid[x][y] == 0)
                        System.out.print(".");
                    else {
                        System.out.print(grid[x][y]);
                    }
                } else if (revealed[x][y] == HIDDEN) {
                    System.out.print("#");
                } else {
                    System.out.print(FLAGUNICODE);
                }
            }
            System.out.println("|");
           // System.out.println("|\n"+border.toString());
        }
    }
    public static void generate(int mines, int pickX, int pickY) {
        //generation method for the board
        grid = new int[sizeX][sizeY];
        ArrayList<int[]> mineList = new ArrayList<>();
        for (int i = 0; i < mines; i++) {
            int randX =pickX;
            int randY = pickY;
            //hack to enter while loop first time without repeating random logic
            while ((pickX == randX && pickY == randY) || grid[randX][randY] == MINE) {
                //logic to not place mines in duplicate locations or where player picked
                randX = (int) (Math.random() * sizeX);
                randY = (int) (Math.random() * sizeY);
            }
            mineList.add(new int[]{randX,randY});
            grid[randX][randY] = MINE;
        }
        for(int[]mineLoc : mineList){
            int x = mineLoc[X], y = mineLoc[Y];
            incAdj(x,y);

        }
        reveal(pickX,pickY);
    }
    public static void incAdj(int x,int y){
        //utility method to increment adjacent locations
        for(int i=0 ; i < NEIGHBORS ; i++){
            int curX = x + neighbors[i][X];
            int curY = y + neighbors[i][Y];
            if(curX >= 0 && curX < sizeX && curY >= 0 && curY < sizeY && grid[curX][curY] != MINE){
                grid[curX][curY]++;
            }
        }
    }
    public static void reveal(int x, int y){
        //recursive method to show squares when a 0 is
        if(x <0 || x == sizeX || y<0 || y == sizeY ||revealed[x][y]==REVEALED){
            return;
        }
        revealed[x][y]=REVEALED;
        if(grid[x][y] == 0){
            for(int i=0;i<NEIGHBORS;i++){
                reveal(x + neighbors[i][X], y + neighbors[i][Y]);
            }
        }
    }
    public static void checkVictory(){
        int count=0;
        for(int x=0;x<sizeX;x++){
            for(int y=0;y<sizeY;y++){
                if(revealed[x][y]!=REVEALED){
                    count++;
                }
            }
        }
        if(count==mines){
            System.out.println("VICTORY!");
            start=false;
        }
    }
}
