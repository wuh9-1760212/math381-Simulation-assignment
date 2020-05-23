package com.company;

import java.math.RoundingMode;
import java.util.*;
import java.io.*;

public class Main {

    public static final int ROUND = 1000;//total rounds of simulation
    public static final int TIMES = 10;//total times of ROUNDS runs simulation to test convergence

    public static void main(String[] args) throws
                                            FileNotFoundException{
        Random r = new Random();
        outputPlotData(r, "firstThreeMax");
        outputPlotData(r, "conservative");
        //outputPlotDataVersionTwo(r, "firstThreeMax");
        //outputPlotDataVersionTwo(r, "conservative");
        //playGameVersionOne(r);//let conservative against strategy 2
        //playGameVersionTwo(r, "conservative");//let conservative against player 2
        //playGameVersionTwo(r, "firstThreeMax");//let strategy 2 against player 2
        //playGameVersionThree(r, "conservative");//let conservative against player 3
        //playGameVersionThree(r, "firstThreeMax");//let conservative against player 3
    }

    //params{Random} r, random object used to generate random rolling
    //params{String} playerOneStrate, the strategy that player one used
    //params{String} playerTwoStrate, the strategy that player two used
    //out put a txt file that record the win rate of player 1.(the diagram to show normal distribution)
    public static void outputPlotData(Random r, String playerOneStrate) throws
            FileNotFoundException{
        String fileName = "";
        if (playerOneStrate.equals("conservative")) {
            fileName = "conserve vs p2.txt";
        } else if (playerOneStrate.equals("firstThreeMax")) {
            fileName = "firstThree vs p2.txt";
        }
        PrintStream output = new PrintStream(new File(fileName));
        PrintStream output2 = new PrintStream(new File("p2 vs " + playerOneStrate + ".txt"));
        PrintStream output3 = new PrintStream(new File("Score for " + playerOneStrate + " " + fileName));
        for (int i = 1; i <= ROUND * 5; i++) {
            int[]result = new int[3];
            for (int j = 1; j <= ROUND; j++) {
                ArrayList<Integer>playerOne = new ArrayList<>();
                if (playerOneStrate.equals("conservative")) {
                    playerOne = conservative(r);
                } else if (playerOneStrate.equals("firstThreeMax")) {
                    playerOne = maxFirstThree(r);
                }
                int score = sumResult(playerOne);
                output3.print(score + " ");
                playerTwoStrategy(score, result, r);
            }
            double playOneWinProb = result[0] * 100.0 / ROUND;
            double playTwoWinPRob = result[1] * 100.0 / ROUND;
            output.print(playOneWinProb + " ");
            output2.print(playTwoWinPRob + " ");
        }
    }

    //params{Random} r, random object used to generate random rolling
    //params{String} playerOneStrate, the strategy that player one used
    //params{String} playerTwoStrate, the strategy that player two used
    //out put a txt file that record the win rate of player 1.(the diagram to show the convergence)
    public static void outputPlotDataVersionTwo(Random r, String playerOneStrate) throws
            FileNotFoundException{
        String fileName = "";
        if (playerOneStrate.equals("conservative")) {
            fileName = "conserve vs p2.txt";
        } else if (playerOneStrate.equals("firstThreeMax")) {
            fileName = "firstThree vs p2.txt";
        }
        for (int k = 1; k <= TIMES; k++) {
            PrintStream output = new PrintStream(new File("Times " + k + " " + fileName));
            int[] result = new int[3];
            for (int i = 1; i <= ROUND * 10; i++) {
                ArrayList<Integer> playerOne = new ArrayList<>();
                if (playerOneStrate.equals("conservative")) {
                    playerOne = conservative(r);
                } else if (playerOneStrate.equals("firstThreeMax")) {
                    playerOne = maxFirstThree(r);
                }
                int score = sumResult(playerOne);
                playerTwoStrategy(score, result, r);
                double playOneWinProb = result[0] * 100.0 / i;
                output.print(playOneWinProb + " ");
            }
        }
    }


    //params{ArrayList<Integer>}player, represent the final dices that player have.
    //params{Random}r, random object to simulate rolling
    //simulate the conservative strategy. Take 4 and 1 first, otherwise take the maximum
    //return the final dices of this player.
    public static ArrayList<Integer> conservative(Random r) {
        ArrayList<Integer>player = new ArrayList<>();
        int dicesKept = 6;//record how many dices this player still need to keep.
        int[]dices = rolling(dicesKept, r);//roll dices first time
        boolean foundOne = false;//check if 1 was found
        boolean foundFour = false;//check if 4 was found
        while (dicesKept > 0) {
            if (!foundFour && contains(dices, 4) && !foundOne && contains(dices, 1)) {//if 1 and 4 both show up, take them both
                foundFour = true;
                foundOne = true;
                player.add(4);
                player.add(1);
                dicesKept --;
            } else if (!foundFour && contains(dices, 4)) {//if we don't have 4 and there is a 4 in our rolling, kept that one
                                                //we check 4 first since 4>1 and has higher priority thant 1
                foundFour = true;
                player.add(4);
            } else if (!foundOne && contains(dices, 1)) {//if we don't have 1 and there is a 1 in our rolling, kept that one
                foundOne = true;
                player.add(1);
            } else {//otherwise we kept the max one;
                player.add(max(dices));
            }
            dicesKept--;//we've kept a die
            dices = rolling(dicesKept, r);//roll dices again
        }
        //System.out.println("P1: " + toString(player) + " Sum is: " + sumResult(player));
        return player;
    }

    //params{ArrayList<Integer>}player, represent the final dices that player have.
    //params{Random}r, random object to simulate rolling
    //simulate the strategy that for first three times, take max, and then find 1 and 4 at next three times.
    public static ArrayList<Integer>maxFirstThree(Random r) {
        ArrayList<Integer>player = new ArrayList<>();
        boolean foundFour = false;
        boolean foundOne = false;
        for (int i = 0; i < 6; i++) {
            int[]dices = rolling(6 - i, r);//rolling remaining dices. 6 when it is first time
            if ( i < 3) {//first three times
                player.add(max(dices));
                if (max(dices) == 4) {//if we take 1 and 4 in first three rounds, we do not need to keep them in the last three rounds
                    foundFour = true;
                } else if (max(dices) == 1) {
                    foundOne = true;
                }
            } else {//last three times
                if (!foundFour && contains(dices, 4) && !foundOne && contains(dices, 1)) {
                    player.add(4);
                    player.add(1);
                    foundFour = true;
                    foundOne = true;
                    i++;
                } else if (!foundFour && contains(dices, 4)) {//kept 4 if there is
                    player.add(4);
                    foundFour = true;
                } else if (!foundOne && contains(dices, 1)) {//kept 1 if there is
                    player.add(1);
                    foundOne = true;
                } else {//if there is no 4 or 1, then kept the max
                    player.add(max(dices));
                }
            }
        }
        return player;
    }

    //@params{int}length, the length of returned array
    //@params{Random}r, object that used to created random number
    //return an array with random element from 1-6
    public static int[]rolling(int length, Random r) {
        int[]rolling = new int[length];//rolling dice
        for (int i = 0; i < rolling.length; i++) {
            rolling[i] = r.nextInt(6) + 1;
        }
        return rolling;
    }


    //@params{array}content that we want to inspect
    //@params{int}element that we want to check
    //return if this content array contains the element
    //used for rolling dice only
    public static boolean contains(int[]content, int element) {
        for (int i = 0; i < content.length; i++) {
            if (content[i] == element) {
                return true;
            }
        }
        return false;
    }

    //@params{array}content that we want to inspect
    //return the max element in content array
    public static int max(int[]content) {
        int max = content[0];
        for (int i = 1; i < content.length; i++) {
            if (content[i] > max) {
                max = content[i];
            }
        }
        return max;
    }

    //params{ArrayList<Integer>}content that we want to inspect
    //return if this content array list contains 1 and 4
    public static boolean listContainsOneAndFour(ArrayList<Integer>content) {
        Iterator iterator = content.iterator();
        boolean foundFour = false;
        boolean foundOne = false;
        while (iterator.hasNext()) {
            Object i = iterator.next();
            if (i.equals(4)) {
                foundFour =  true;
            } else if (i.equals(1)) {
                foundOne = true;
            }
        }
        return foundFour && foundOne;
    }

    public static void playGameVersionOne(Random r) {
        int[]result = new int[3];//array that record the number of wins for each player;
        for (int i = 1; i <= ROUND; i++) {
            ArrayList<Integer>playerOne = conservative(r);
            ArrayList<Integer>playerTwo = maxFirstThree(r);
            if (sumResult(playerOne) > sumResult(playerTwo)) {
                result[0]++;
            } else if (sumResult(playerOne) < sumResult(playerTwo)) {
                result[1]++;
            } else {
                result[2]++;
            }
        }
        double playerOneProb = Math.round(result[0] * 100.0 / ROUND);
        double playerTwoProb = Math.round(result[1] * 100.0 / ROUND);
        double tieProb = Math.round(result[2] * 100.0 / ROUND);
        System.out.println("Total rounds of game is " + ROUND + ". Player one won " + result[0] + " times, probability was " +
                "about " +playerOneProb + "% and player two won " + result[1] + " times, probability was about " + playerTwoProb + "%. And" +
                "They tied " + result[2] + " times. the probability is " + tieProb + "%.");
    }

    //params{ArrayList<Integer>}player, the dices that player have
    //return the score of this player.
    public static int sumResult(ArrayList<Integer>player) {
        if (!listContainsOneAndFour(player)) {//if the dice this player kept has no 1 and 4, then return 0 score
            return 0;
        }
        Iterator iterator = player.iterator();
        int result = 0;
        while (iterator.hasNext()) {
            int current = (int)iterator.next();
            result += current;
        }
        return result - 5;//remove one 4 and one 1 that does not count to the result.
    }

    //params{Random} r, random object that simulate dice rolling
    //params{String} strategy, strategy that we want p1 use
    //simulate p1 with different strategy against p2.
    public static void playGameVersionTwo(Random r, String strategy) {
        int[]result = new int[3];//record the number of win for players
        for (int i = 1; i <= ROUND; i++) {
            ArrayList<Integer>playerOne = new ArrayList<>();
            if (strategy.equals("conservative")) {
                playerOne = conservative(r);
            } else if (strategy.equals("firstThreeMax")) {
                playerOne = maxFirstThree(r);
            }
            int score = sumResult(playerOne);
            playerTwoStrategy(score, result, r);
        }
        double playerOneProb = Math.round(result[0] * 100.0 / ROUND);
        double playerTwoProb = Math.round(result[1] * 100.0 / ROUND);
        double tieProb = Math.round(result[2] * 100.0 / ROUND);
        System.out.println("Total rounds of game is " + ROUND + ". Player one won " + result[0] + " times, probability was " +
                "about " +playerOneProb + "% and player two won " + result[1] + " times, probability was about " + playerTwoProb + "%. And" +
                "They tied " + result[2] + " times. the probability is " + tieProb + "%.");
    }

    //params{int[]} result, array that record the number of win for both players
    //params{int} score, score for p1
    //params{Random} r, simulate rolling dice
    //make dices for player 2, and directly record win or lose into result array
    public static void playerTwoStrategy(int score, int[]result, Random r) {
        ArrayList<Integer> playerTwo = new ArrayList<>();
        boolean foundFour = false;
        boolean foundOne = false;
        boolean p2Win = false;
        for (int i = 0; i < 6; i++) {//rolling six times at most
            int[]dices = rolling(6 - i, r);
            if ((!foundFour && contains(dices, 4)) && (!foundOne && contains(dices, 1))) {//if in one rolling, both 1 and 4 appear, take both of them
                foundFour = true;
                foundOne = true;
                playerTwo.add(4);
                playerTwo.add(1);
                i++;
            } else if (!foundFour && contains(dices, 4) ) {
                foundFour = true;
                playerTwo.add(4);
            } else if (!foundOne && contains(dices, 1)) {
                foundOne = true;
                playerTwo.add(1);
            } else {
                playerTwo.add(max(dices));
            }
            if (sumResult(playerTwo) > score) {//if current dice sum more than player one score, then the game stop and player two wins.
                                                //checked after each rolling
                result[1]++;
                i = 6;
                p2Win = true;
            }
        }
        if (!p2Win) {//if after 6 times of rolling, p2 does not win, then p1 win or tie.
            if (sumResult(playerTwo) == score ) {//check if p1 and p2 tie
                result[2]++;
            } else if (sumResult(playerTwo) > score) {
                result[1]++;
            } else {
                result[0]++;
            }
        }
    }

    //params{Random} r, random object that simulate dice rolling
    //params{String} strategy, strategy that we want p1 use
    //simulate p1 with different strategy against andy choice.
    public static void playGameVersionThree(Random r, String strategy) {
        ArrayList<Integer>playerOne = new ArrayList<>();
        int[]result = new int[3];
        for (int i = 1; i <= ROUND; i++) {
            if (strategy.equals("conservative")) {
                playerOne = conservative(r);
            } else if (strategy.equals("firstThreeMax")) {
                playerOne = maxFirstThree(r);
            }
            ArrayList<Integer>playerTwo = playerThreeStrategy(r);
            if (sumResult(playerOne) > sumResult(playerTwo)) {
                result[0]++;
            } else if (sumResult(playerOne) < sumResult(playerTwo)) {
                result[1]++;
            } else {
                result[2]++;
            }
        }
        double playerOneProb = Math.round(result[0] * 100.0 / ROUND);
        double playerTwoProb = Math.round(result[1] * 100.0 / ROUND);
        double tieProb = Math.round(result[2] * 100.0 / ROUND);
        System.out.println("Total rounds of game is " + ROUND + ". Player one won " + result[0] + " times, probability was " +
                "about " +playerOneProb + "% and player two won " + result[1] + " times, probability was about " + playerTwoProb + "%. And" +
                "They tied " + result[2] + " times. the probability is " + tieProb + "%.");
    }

    //params{Random}r that used to generate random rolling
    //a keep strategy that andy prefer. Call it "andyChoice" when used
    public static ArrayList<Integer> playerThreeStrategy(Random r) {
        ArrayList<Integer>player = new ArrayList<>();
        boolean foundFour = false;
        boolean foundOne = false;
        for (int i = 0; i < 6; i++) {
            int[]dices = rolling(6-i, r);
            if (!foundFour && contains(dices, 4) && !foundOne && contains(dices, 1)) {
                player.add(1);
                player.add(4);
                foundFour = true;
                foundOne = true;
                i++;
            } else if (!foundFour && contains(dices, 4)) {
                player.add(4);
                foundFour = true;
            } else if (!foundOne && contains(dices, 1)) {
                player.add(1);
                foundOne = true;
            } else if ((6 - i) <= 2 && contains(dices, 5)) {
                player.add(5);
            } else if ((6 - i) == 1 && contains(dices, 4)) {
                player.add(4);
            } else {
                player.add(max(dices));
            }
        }
        return player;
    }
}