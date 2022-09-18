package com.portfolio.tictactoe;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.GnssAntennaInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

    /*Tic-Tac-Toe android game with a complete UI and computer AI using minimax recursive Search Algorithm,
    with a Heuristic Board Evaluation Function.

    METHODS:
    * startGame(View view) -  assign a pawn(X/O) to each player and starts the game
        PARAM: View - View object from the UI clicked "start" button.

        RETURN - Void

    * playerTap(View view) - draw the correct pawn on board and check for end game or move to next player.
        PARAM: View - View object from the clicked empty square.

        RETURN: Void

    * computerPlay() - starts the computer turn and AI, evaluate the board and make a move,
                       check for end game or move to next player
        PARAM: Void
        RETURN: void
    Utility methods:
    * int generateRandomNumber(int max, int min, boolean isEven) - generate a random number from parameters
        PARAM: int max - maximum range, exclusive.
               int min - min range, inclusive
               boolean isEven - boolean to return only even numbers

        RETURN: random in within range

    * boolean checkWin(int player) - check if current player has 3 in a row
        PARAM: int of current player

        RETURN: boolean answer if player won

    * void resetBoard() - reset board and stats to start a new game
        PARAM: Void

        RETURN: Void

    * void resetScores(View view) - reset the scores
        PARAM: View view - View object from clicked "reset scores" button in UI.

        RETURN: void

    AI methods: AI methods run internally by the computer
    * generateMoves()
    * evaluate()
    * evaluateLine(int index1, int index2, int index3)
    * miniMax(int level, int player)

    */

public class MainActivity extends AppCompatActivity {

    public static final String COMPUTER_TURN = "Computer's Turn";
    public static final String PLAYER_TURN = "Player 1 Turn";
    public static final String PLAYER2_TURN = "Player 2 Turn";
    public static final String PLAYER_WIN = "Player Win!";
    public static final String PLAYER2_WIN = "Player 2 Win!";
    public static final String COMPUTER_WINS = "Computer Wins!";
    public static final String NO_WINS = "Tie! No Wins!";
    public static final String CHOOSE_OPTION_AND_START = "press START to begin a game";
    public static int AI_LEVEL = 2;

    Toolbar myToolbar;
    TextView statusScreen, humanScore, computerScore, player2_title;
    ViewGroup gameBoard;
    SharedPreferences sharedPreferences;
    int roundCounter = 0;

    // Player representation
    // 0 - X
    // 1 - O
    int activePlayer = 3;
    int humanPlayer = 2;
    int computerPlayer = 2;

    int computerPoints = 0;
    int humanPoint = 0;

    // State meanings:
    //    0 - X
    //    1 - O
    //    2 - Null
    public static int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};

    // put all win positions in a 2D array
    int[][] winPositions = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}};


    //METHODS
    public void startGame(View view){
        Log.i("startGame", "start game method started");
        Log.i("startGame", "reset board start");
        resetBoard();
        Log.i("startGame", "reset board succeeded");

        Log.i("startGame", "picking beginner");
        //pick player one - random, X or O
        if (sharedPreferences.getBoolean("settings_player1_random", true)){ // if player 1 is random
            int randomChoice = generateRandomNumber(2, 0, false);
            if (randomChoice == 0){
                humanPlayer = 0;
                computerPlayer = 1;
                activePlayer = humanPlayer;
            }else{
                humanPlayer = 1;
                computerPlayer = 0;
                activePlayer = computerPlayer;
            }
        } else { //if player 1 is predefined
            if(sharedPreferences.getBoolean("Player1_is_X", true)){
                humanPlayer = 0;
                computerPlayer = 1;
                activePlayer = humanPlayer;
            } else if (sharedPreferences.getBoolean("Player1_is_O", false)){
                humanPlayer = 1;
                computerPlayer = 0;
                activePlayer = computerPlayer;
            } else {
                Log.e("startGame", "error with preferences");
            }

        }

        //choose second player
        if(sharedPreferences.getBoolean("settings_player2_choice", true)){ // if player 2 is ai or human
            player2_title.setText(R.string.computer);
        } else{
            player2_title.setText(R.string.Player_2);
        }

        //choose difficulty
        if(sharedPreferences.getBoolean("check_box_easy", false)){
            AI_LEVEL = 1;
        } else if (sharedPreferences.getBoolean("check_box_hard", true)){
            AI_LEVEL = 2;
        }

        Log.i("startGame", "player is " + humanPlayer);
        Log.i("startGame", "player 2 is " + computerPlayer);
        Log.i("startGame", "active player is " + activePlayer);

        //start first round
        if((activePlayer == computerPlayer) && sharedPreferences.getBoolean("settings_player2_choice", true)){ // if player 2 is ai
            Log.i("startGame", "computer turn");
            statusScreen.setText(COMPUTER_TURN);
            computerPlay();
        } else {
            gameBoard.setClickable(true);
            Log.i("startGame", "human turn");
            if (activePlayer == humanPlayer){
                statusScreen.setText(PLAYER_TURN);
            } else {
                statusScreen.setText(PLAYER2_TURN);
            }


        }
    }

    public void playerTap(View view){
        Log.i("playerTap", "player tapped screen");
        ImageView img = (ImageView) view;
        int tappedImage = Integer.parseInt(img.getTag().toString());

        //when a player taps an empty square in his turn.
        if((gameState[tappedImage] == 2) && (gameBoard.isClickable())){
            Log.i("playerTap", "tapped block is empty");
            gameState[tappedImage] = activePlayer; //fill 2d array


            //change to image XO and change players.
            if(activePlayer == 0){
                img.setImageResource(R.drawable.x);
            } else if(activePlayer == 1) {
                img.setImageResource(R.drawable.o);
            }

            gameBoard.setClickable(false);
            if(checkWin(activePlayer)){
                if(activePlayer == humanPlayer){
                    statusScreen.setText(PLAYER_WIN);
                    humanPoint++;
                    humanScore.setText(String.valueOf(humanPoint));
                } else {
                    statusScreen.setText(PLAYER2_WIN);
                    computerPoints++;
                    computerScore.setText(String.valueOf(computerPoints));
                }


            } else if(roundCounter == 8){
                statusScreen.setText(NO_WINS);
            } else {
                roundCounter++;
                if(sharedPreferences.getBoolean("settings_player2_choice", false)){
                    activePlayer = computerPlayer;
                    statusScreen.setText(COMPUTER_TURN);
                    computerPlay();
                } else {
                    if (activePlayer == computerPlayer){
                        activePlayer = humanPlayer;
                        statusScreen.setText(PLAYER_TURN);
                    } else {
                        activePlayer = computerPlayer;
                        statusScreen.setText(PLAYER2_TURN);
                    }
                    gameBoard.setClickable(true);
                }
            }
        }
    }
    //AI IMPLEMENTATION
    public void computerPlay(){
        //run miniMax algorithm to find best next move.
        int[] move = miniMax(AI_LEVEL, computerPlayer);
        Log.i("ComputerPlay", "computer index is " + move[0] + " score of " + move[1]);

        gameState[move[0]] = computerPlayer; //update 2d board
        ImageView selectedView = getWindow().getDecorView().findViewWithTag(String.valueOf(move[0])); //update UI board

        if(computerPlayer == 0){
            selectedView.setImageResource(R.drawable.x);
        } else if (computerPlayer == 1) {
            selectedView.setImageResource(R.drawable.o);
        }


        if(checkWin(activePlayer)){
            statusScreen.setText(COMPUTER_WINS);
            computerPoints++;
            computerScore.setText(String.valueOf(computerPoints));
        } else if (roundCounter == 8){
            statusScreen.setText(NO_WINS);
        } else {
            roundCounter++;
            activePlayer = humanPlayer;
            gameBoard.setClickable(true);
            statusScreen.setText(PLAYER_TURN);
        }
    }

    private List<int[]> generateMoves(){
        //generate list of possible moves/ empty spots on board or empty list if game over
        List<int[]> nextMove = new ArrayList<>();

        //if game over
        if(checkWin(computerPlayer) || checkWin(humanPlayer)){
            return nextMove;
        }
        // Search for empty cells and add to the List
        for(int i = 0; i < gameState.length; i++) {
            int[] indexRating = new int[2];
            if (gameState[i] == 2) {
                indexRating[0] = i;
                nextMove.add(indexRating);
            }
        }
        return nextMove;
    }

    private int evaluate() {
        int score = 0;
        // Evaluate score for each of the 8 win positions
        for (int [] wins : winPositions){
            score += evaluateLine(wins[0], wins[1], wins[2]);
        }
        //Log.e("evaluate", "score of evaluation is " + score);
        return score;
    }

    /* The heuristic evaluation function for the current board
     Return +100, +10, +1 for EACH 3-, 2-, 1-in-a-line for computer.
     -100, -10, -1 for EACH 3-, 2-, 1-in-a-line for opponent.
     0 otherwise   */
    private int evaluateLine(int index1, int index2, int index3) {
        int score = 0;

        //first cell
        if (gameState[index1] == computerPlayer) {
            score = 1;
        } else if (gameState[index1] == humanPlayer) {
            score = - 1;
        }
        //second cell
        if (gameState[index2] == computerPlayer){
            if (score == 1){ // cell1 is computerPlayer
                score = 10;
            } else if (score == -1){ // cell1 is humanPlayer
                return 0;
            } else { // cell1 is empty
                score = 1;
            }
        } else if(gameState[index2] == humanPlayer){
            if (score == -1){ // cell1 is humanPlayer
                score = -10;
            } else if (score == 1){ // cell1 is computerPlayer
                return 0;
            } else { // cell1 is empty
                score = -1;
            }
        }
        //third cell
        if (gameState[index3] == computerPlayer){
            if (score > 0) { // cell1 and/or cell2 is computerPlayer
                score *= 10;
            } else if( score < 0){ // cell1 and/or cell2 is humanPlayer
                return 0;
            } else { // cell1 and cell2 are empty
                score = 1;
            }
        } else if (gameState[index3] == humanPlayer){
            if (score < 0){ // cell1 and/or cell2 is humanPlayer
                score *= 10;
            } else if (score > 1){ // cell1 and/or cell2 is computerPlayer
                return 0;
            } else { // cell1 and cell2 are empty
                score = -1;
            }
        }
        return score;
    }

    /* Recursive minimax at level of depth for either maximizing or minimizing player.
     Return int[2] of {index, score}  */
    public int[] miniMax(int level, int player) {
        // Generate possible next moves in a List
        List<int[]> nextMove = generateMoves();

        // mySeed is maximizing; while oppSeed is minimizing
        int bestScore = (player == computerPlayer) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int currentScore;
        int bestIndex = -1;

        if (nextMove.isEmpty() || level == 0) {
            // Gameover or depth reached, evaluate score
            bestScore = evaluate();
        } else {
            for (int[] move : nextMove) {
                Log.i("miniMax", "checking index " + move[0]);
                // Try this move for the current "player"
                gameState[move[0]] = player;
                if (player == computerPlayer) { // computer is maximizing player
                    currentScore = miniMax(level - 1, humanPlayer)[1];
                    if (currentScore > bestScore) {
                        bestScore = currentScore;
                        bestIndex = move[0];
                    }
                } else { // human is minimizing player
                    currentScore = miniMax(level - 1, computerPlayer)[1];
                    if (currentScore < bestScore) {
                        bestScore = currentScore;
                        bestIndex = move[0];
                    }
                }
                Log.i("miniMax", "best score is " + bestScore);
                // Undo move
                gameState[move[0]] = 2;
            }
        }
        return new int[] {bestIndex, bestScore};
    }
    //END OF AI IMPLEMENTATION

    public int generateRandomNumber(int max, int min, boolean isEven){
        Random rand = new Random();
        int randomNum;

        if(isEven){
            randomNum = min + rand.nextInt((max-min)/2) *2;
        }else{
            randomNum = (int) ((Math.random() * (max - min)) + min);
        }
        return randomNum;
    }

    public boolean checkWin(int player){

        for(int[] wins : winPositions){
            if((gameState[wins[0]] == player)
                    && (gameState[wins[1]] == player)
                    &&(gameState[wins[2]] == player)){
                //win
                return true;
            }
        }
        return false;
    }

    public void resetBoard(){
        //reset 2D board
        for(int i = 0; i < gameState.length; i++){
            gameState[i] = 2;
        }

        //reset UI board.
        ((ImageView) findViewById(R.id.square1)).setImageResource(0);
        ((ImageView) findViewById(R.id.square2)).setImageResource(0);
        ((ImageView) findViewById(R.id.square3)).setImageResource(0);
        ((ImageView) findViewById(R.id.square4)).setImageResource(0);
        ((ImageView) findViewById(R.id.square5)).setImageResource(0);
        ((ImageView) findViewById(R.id.square6)).setImageResource(0);
        ((ImageView) findViewById(R.id.square7)).setImageResource(0);
        ((ImageView) findViewById(R.id.square8)).setImageResource(0);
        ((ImageView) findViewById(R.id.square9)).setImageResource(0);

        //reset status board
        statusScreen.setText(CHOOSE_OPTION_AND_START);

        //reset players
        activePlayer = 3;
        humanPlayer = 2;
        computerPlayer = 2;
        roundCounter = 0;
    }

    public void resetScores(View view){
        computerPoints = 0;
        humanPoint = 0;
        humanScore.setText(String.valueOf(humanPoint));
        computerScore.setText(String.valueOf(computerPoints));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setup toolbar
        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        statusScreen = findViewById(R.id.tv_main_game_status);
        statusScreen.setText(CHOOSE_OPTION_AND_START);

        gameBoard = findViewById(R.id.grid_squares);
        gameBoard.setClickable(false);

        humanScore = findViewById(R.id.tv_main_score_human);
        humanScore.setText(String.valueOf(humanPoint));

        player2_title = findViewById(R.id.tv_main_score_computer_title);
        computerScore = findViewById(R.id.tv_main_score_computer);
        computerScore.setText(String.valueOf(computerPoints));


        Log.i("onCreate", "activity created");
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.getAll();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    //tool bar menu handler
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_about:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}