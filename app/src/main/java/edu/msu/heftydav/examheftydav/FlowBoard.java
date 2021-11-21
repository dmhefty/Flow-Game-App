package edu.msu.heftydav.examheftydav;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.constraintlayout.helper.widget.Flow;

import java.util.ArrayList;
import java.util.Random;

public class FlowBoard {

    //
    // methods
    //

    public FlowBoard(Context context){
        //random.setSeed(12);
        dotBlock1 = random.nextInt(100);
        linePaint.setStrokeWidth(3f);

        // run through until they are separate locactions
        while(true){
            dotBlock2 = random.nextInt(100);
            if(dotBlock2 != dotBlock1) break;
        }

        for(int i = 0; i < 100; i++){
            if(i == dotBlock1 || i == dotBlock2){
                blocks.add(new FlowBlock(context, i, true));
            }
            else {
                blocks.add(new FlowBlock(context, i, false));
            }
        }
    }

    public void draw(Canvas canvas){
        int wid = canvas.getWidth();
        int hit = canvas.getHeight();

        // Determine the minimum of the two dimensions
        int minDim = Math.min(hit, wid);

        boardSize = (int)(minDim * SCALE_IN_VIEW);

        // Compute the margins so we center the puzzle
        marginX = (wid - boardSize) / 2;
        marginY = (hit - boardSize) / 2;

        scaleFactor = 0.9f;

        for(FlowBlock block : blocks){
            block.draw(canvas, boardSize, scaleFactor);
        }

        for(int i=1; i<10; i++){
            canvas.save();

            canvas.drawLine(marginX + i * boardSize/10f,
                    marginY,
                    marginX + i * boardSize/10f,
                    marginY + boardSize,
                    linePaint
                    );

            canvas.drawLine(marginX,
                    marginY + i * boardSize/10f,
                    marginX + boardSize,
                    marginY  + i * boardSize/10f,
                    linePaint
            );

            canvas.restore();
        }

    }

    public boolean onTouchEvent(View view, MotionEvent event){
        // Convert an x,y location to a relative location in the
        // board.
        //
        float relX = (event.getX()) / (boardSize + marginX*2);
        float relY = (event.getY()) / (boardSize + marginY*2);

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                if(isComplete){
                    // throw toast if no jump is possible
                    Toast toast=Toast.makeText(view.getContext(),
                            "Game has been completed, press reset if you wish to play again.",
                            Toast.LENGTH_SHORT);
                    toast.setMargin(50,50);
                    toast.setGravity(Gravity.BOTTOM| Gravity.CENTER, 0, 0);
                    toast.show();
                    return true;
                }
                return onTouched(view, relX, relY);

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                return onReleased(view, relX, relY);


            case MotionEvent.ACTION_MOVE:
                return onDragged(view, relX, relY);
        }

        return false;
    }

    private boolean onTouched(View view, float x, float y) {

        // Check each piece to see if it has been hit
        // We do this in reverse order so we find the pieces in front
        for(int p=blocks.size()-1; p>=0;  p--) {
            if(blocks.get(p).hit(x, y, boardSize, SCALE_IN_VIEW, marginX, marginY)
                    && blocks.get(p).isDot) {
                // We hit a piece!

                //dragging = pieces.get(pieces.size()-1);
                startBlock = blocks.get(p);
                startBlock.isGrabbed = true;
                startBlock.isSelected = true;
                lastRelX = x;
                lastRelY = y;
                //PuzzlePiece t = pieces.get(pieces.size()-1);
                //pieces.set(p, t);
                //pieces.set(pieces.size()-1, dragging);
                selectedBlocks.add(startBlock);
                view.invalidate();
                return true;
            }
        }
        // throw toast if no piece is valid
        Toast toast=Toast.makeText(view.getContext(),
                "Invalid block, Try again.",
                Toast.LENGTH_SHORT);
        toast.setMargin(50,50);
        toast.setGravity(Gravity.BOTTOM| Gravity.CENTER, 0, 0);
        toast.show();
        return false;
    }

    /**
     * Handle a release of a touch message.
     * @param x x location for the touch release, relative to the puzzle - 0 to 1 over the puzzle
     * @param y y location for the touch release, relative to the puzzle - 0 to 1 over the puzzle
     * @return true if the touch is handled
     */
    private boolean onReleased(View view, float x, float y) {
        // if the game has already finished, move on
        if(isComplete) return false;
        if(startBlock == null) return false;

        if(isDone()) {
            // throw toast if no jump is possible
            Toast toast=Toast.makeText(view.getContext(),
                    "Winner!",
                    Toast.LENGTH_SHORT);
            toast.setMargin(50,50);
            toast.setGravity(Gravity.BOTTOM| Gravity.CENTER, 0, 0);
            toast.show();

            isComplete = true;

            return true;
        }
        emptySelection(view);

        // throw toast if no jump is possible
        Toast toast=Toast.makeText(view.getContext(),
                "Line not completed. Try again.",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM| Gravity.CENTER, 0, 0);
        toast.setMargin(50,50);
        toast.show();
        return false;
    }

    /**
     * Handle a move of a touch message.
     * @param x x location for the touch release, relative to the puzzle - 0 to 1 over the puzzle
     * @param y y location for the touch release, relative to the puzzle - 0 to 1 over the puzzle
     * @return true if the touch is handled
     */
    private boolean onDragged(View view, float x, float y) {
        if(isComplete) {
            return false;
        }

        // check there is a selection and that the touch is within the board
        if(startBlock != null){
            // if the touch is outside of the board reset
            if ( x*(marginX*2 + boardSize) > marginX + boardSize
                    || x*(marginX*2 + boardSize) < marginX
                    || y*(marginY*2 + boardSize) > marginY + boardSize
                    || y*(marginY*2 + boardSize) < marginY){

                emptySelection(view);

                Toast toast=Toast.makeText(view.getContext(),
                        "Cannot go outside of the board. Try again.",
                        Toast.LENGTH_SHORT);
                toast.setMargin(50,50);
                toast.setGravity(Gravity.BOTTOM| Gravity.CENTER, 0, 0);
                toast.show();

                return false;
            }

            for(int p=blocks.size()-1; p>=0;  p--) {
                int xIndex = blocks.get(p).locationIndex%10;
                int yIndex = blocks.get(p).locationIndex/10;

                if(blocks.get(p).hit(x, y, boardSize, SCALE_IN_VIEW, marginX, marginY)) {
                    // We hit a piece!
                    if((p == dotBlock1 && startBlock.locationIndex == dotBlock2)
                        || (p == dotBlock2 && startBlock.locationIndex == dotBlock1)){

                        // throw toast if no jump is possible
                        Toast toast=Toast.makeText(view.getContext(),
                                "Winner!",
                                Toast.LENGTH_SHORT);
                        toast.setMargin(50,50);
                        toast.setGravity(Gravity.BOTTOM| Gravity.CENTER, 0, 0);
                        toast.show();
                        ((FlowView)view).Reset();
                    }

                    //dragging = pieces.get(pieces.size()-1);
                    FlowBlock hitBlock = blocks.get(p);
                    hitBlock.isSelected = true;
                    lastRelX = x;
                    lastRelY = y;
                    //PuzzlePiece t = pieces.get(pieces.size()-1);
                    //pieces.set(p, t);
                    //pieces.set(pieces.size()-1, dragging);
                    selectedBlocks.add(hitBlock);
                    view.invalidate();
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isDone() {
        if(blocks.get(dotBlock1).isSelected && blocks.get(dotBlock2).isSelected)
            return true;

        return false;
    }

    private void emptySelection(View view){
        startBlock = null;
        for(FlowBlock block : selectedBlocks){
            block.isSelected = false;
        }
        selectedBlocks.clear();
        view.invalidate();
    }

    //
    // member variables
    //

    /**
     * Blocks representing the start and finish
     */
    private int dotBlock1;
    private int dotBlock2;

    /**
     * The size of the checkerboard in pixels
     */
    private int boardSize;

    /**
     * How much we scale the checkerboard pieces
     */
    private float scaleFactor;

    /**
     * Left margin in pixels
     */
    private int marginX;

    /**
     * Top margin in pixels
     */
    private int marginY;

    /**
     * Most recent relative X touch when dragging
     */
    private float lastRelX;

    /**
     * Most recent relative Y touch when dragging
     */
    private float lastRelY;

    /**
     * Collection of checkerboard pieces
     */
    public ArrayList<FlowBlock> blocks = new ArrayList< >();

    /**
     * Collection of selected checkerboard pieces
     */
    public ArrayList<FlowBlock> selectedBlocks = new ArrayList< >();

    /**
     * This variable is set to a piece we are dragging. If
     * we are not dragging, the variable is null.
     */
    private FlowBlock startBlock = null;

    /**
     * Percentage of the display width or height that
     * is occupied by the checkerboard.
     */
    final static float SCALE_IN_VIEW = 0.9f;

    /**
     * Random number generator
     */
    private static Random random = new Random();

    /**
     * determines if a player has made their move
     */
    public boolean playerHasMoved = false;

    /**
     * The name of the bundle keys to save the checkerboard
     */
    private final static String LOCATIONS = "checkerboard.locations";
    private final static String IDS = "checkerboard.ids";

    /**
     * Paint color we will use to draw dark checker squares
     */
    private int darkSquare = 0xff779455;

    /**
     * Paint color we will use to draw light checker squares
     */
    private int lightSquare = 0xffebebd0;

    /**
     * Paint for drawing lines
     */
    private Paint linePaint = new Paint(Color.BLACK);

    /**
     * Determines if the game has been complete or not
     */
    private boolean isComplete = false;

    //
    // subclasses
    //

}
