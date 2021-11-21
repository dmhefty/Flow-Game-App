package edu.msu.heftydav.examheftydav;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class FlowBlock {

    //
    //  methods
    //
    public FlowBlock(Context context, int boardIndex, boolean dot) {
        this.locationIndex = boardIndex;
        this.isDot = dot;

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(lightSquare);

        outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outlinePaint.setColor(darkSquare);


    }

    public void draw(Canvas canvas, int boardSize, float scaleFactor) {

        int wid = canvas.getWidth();
        int hit = canvas.getHeight();

        // Determine the minimum of the two dimensions
        int minDim = Math.min(hit, wid);

        int puzzleSize = (int)(minDim * scaleFactor);

        int xIndex = locationIndex%10;

        int yIndex = locationIndex/10;

        // Compute the margins so we center the puzzle
        int marginX = (wid - puzzleSize) / 2;
        int marginY = (hit - puzzleSize) / 2;

        setLocationFromIndex(locationIndex, marginX, marginY, puzzleSize);

        int squareCenterXOffset = marginX + xIndex * puzzleSize/10;
        int squareCenterYOffset = marginY + yIndex * puzzleSize/10;

        if (isSelected) {
            fillPaint.setColor(lightSquare);
        }
        else{
            fillPaint.setColor(darkSquare);
        }

        canvas.drawRect(squareCenterXOffset, squareCenterYOffset,
                squareCenterXOffset + puzzleSize/10f, squareCenterYOffset + puzzleSize/10f, fillPaint);

        if (isDot) {
            fillPaint.setColor(dotColor);
            canvas.drawCircle(squareCenterXOffset + puzzleSize/20f,
                    squareCenterYOffset + puzzleSize/20f,
                    (puzzleSize/20f) * 0.9f,
                    fillPaint);
        }
    }

    /**
     * Test to see if we have touched a puzzle piece
     * @param testX X location as a normalized coordinate (0 to 1)
     * @param testY Y location as a normalized coordinate (0 to 1)
     * @param puzzleSize the size of the puzzle in pixels
     * @param scaleFactor the amount to scale a piece by
     * @return true if we hit the piece
     */
    public boolean hit(float testX, float testY,
                       int puzzleSize, float scaleFactor, int marginX, int marginY) {

        // Make relative to the location and size to the piece size
        int i = 0;
        if(isDot){
            i = 1;
        }
        i++;

        scaleFactor = (puzzleSize/10f)/(float)Math.min(puzzleSize/10f, puzzleSize/10f);

        int pX = (int)(( ((testX - x) * (puzzleSize + marginX*2)) +
                puzzleSize/10f * 3f/10f));
        int pY = (int)(( ((testY - y) * (puzzleSize + marginY*2)) +
                puzzleSize/10f * 3f/10f));

        if(pX < 0 || pX >= (puzzleSize/10f) ||
                pY < 0 || pY >= (puzzleSize/10f)) {
            return false;
        }

        // We are within the rectangle of the piece.
        // Are we touching actual picture?
        //return (piece.getPixel(pX, pY) & 0xff000000) != 0;

        return true;
    }

    public void setLocationFromIndex(int index, int marginX, int marginY, int puzzleSize){
        int xIndex; int yIndex;
        xIndex = index%10;
        yIndex = index/10;

        // Convert x,y to pixels and add the margin, then draw
        x = (float) (marginX + xIndex * puzzleSize/10f) / (float) (puzzleSize + 2*marginX);
        y = (float) (marginY + yIndex * puzzleSize/10f) / (float) (puzzleSize + 2*marginY);

    }

    //
    // member variables
    //

    /**
     * x location.
     * We use relative x locations in the range 0-1 for the center
     * of the puzzle piece.
     */
    private float x = 0;

    /**
     * y location
     */
    private float y = 0;

    /**
     * defines if it is a start/end locaction
     */
    public boolean isDot;

    /**
     * defines if it is part of a selected line
     */
    public boolean isSelected = false;

    /**
     * What square the piece is in, starts counting from the top, leftmost, square, counts right
     * until the end of the row then loops to the leftmost square on the next row and continues
     * counting
     */
    public int locationIndex;

    /**
     * True when the piece has been picked up to move
     */
    public boolean isGrabbed;

    /**
     * Paint for filling the area the checkerboard is in
     */
    private final Paint fillPaint;

    /**
     * Paint for outlining the area the checkerboard is in
     */
    private Paint outlinePaint;

    /**
     * Paint color we will use to draw dark checker squares
     */
    private int darkSquare = 0xff779455;

    /**
     * Paint color we will use to draw light checker squares
     */
    private int lightSquare = 0xffebebd0;

    /**
     * Paint color we will use to draw light checker squares
     */
    private int dotColor = 0xffff0000;

    //
    // getters/setters
    //

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }


    //
    // subclasses
    //

}
