package edu.msu.heftydav.examheftydav;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class FlowView extends View {

    public FlowView(Context context) {
        super(context);
        init(context, 0);
    }

    public FlowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, 0);
    }

    public FlowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, defStyleAttr);
    }

    public void init(Context context, int defstyle) {
        board = new FlowBoard(context);
        return;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return board.onTouchEvent(this, event);
        //return true
    }

    /**
     * Handle movement of the touches
     */
    private void move() {
        // If no touch1, we have nothing to do
        // This should not happen, but it never hurts
        // to check.
        if(touch1.id < 0) {
            return;
        }

        // At least one touch
        // We are moving
        touch1.computeDeltas();


        params.posX += touch1.dX;
        params.posY += touch1.dY;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        board.draw(canvas);
    }

    public boolean hitTest(float x, float y) {
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    /**
     * Get the positions for the two touches and put them
     * into the appropriate touch objects.
     * @param event the motion event
     */
    private void getPositions(MotionEvent event) {
        for(int i=0;  i<event.getPointerCount();  i++) {

            // Get the pointer id
            int id = event.getPointerId(i);

            // Convert to image coordinates
            float x = (event.getX(i) - marginLeft) / imageScale;
            float y = (event.getY(i) - marginTop) / imageScale;

            if(id == touch1.id) {
                touch1.copyToLast();
                touch1.x = x;
                touch1.y = y;
            }
        }

        invalidate();
    }

    public FlowBoard getBoard() {
        return board;
    }

    public void Reset(){
        board = new FlowBoard(getContext());
        invalidate();
    }

    //
    // attributes
    //

    public FlowBoard board;

    /**
     * Paint object we will use to draw a line
     */
    private Paint linePaint;

    /**
     * First touch status
     */
    private Touch touch1 = new Touch();

    private Parameters params = new Parameters();

    /**
     * The image bitmap. None initially.
     */
    private Bitmap imageBitmap = null;
    /**
     * Image drawing scale
     */
    private float imageScale = 1;

    /**
     * Image left margin in pixels
     */
    private float marginLeft = 0;

    /**
     * Image top margin in pixels
     */
    private float marginTop = 0;

    //
    //  Subclasses
    //

    private static class Parameters implements Serializable {
        /**
         * Path to the image file if one exists
         */
        public String imagePath = null;
        /**
         * The current checker type
         */
        public int checker;
        /**
         * X location of hat relative to the image
         */
        public float posX = 0;
        /**
         * Y location of hat relative to the image
         */
        public float posY = 0;

    }

    /**
     * Local class to handle the touch status for one touch.
     * We will have one object of this type for each of the
     * two possible touches.
     */
    private static class Touch {
        /**
         * Touch id
         */
        public int id = -1;

        /**
         * Current x location
         */
        public float x = 0;

        /**
         * Current y location
         */
        public float y = 0;

        /**
         * Previous x location
         */
        public float lastX = 0;

        /**
         * Previous y location
         */
        public float lastY = 0;
        /**
         * Change in x value from previous
         */
        public float dX = 0;

        /**
         * Change in y value from previous
         */
        public float dY = 0;
        /**
         * Copy the current values to the previous values
         */
        public void copyToLast() {
            lastX = x;
            lastY = y;
        }
        /**
         * Compute the values of dX and dY
         */
        public void computeDeltas() {
            dX = x - lastX;
            dY = y - lastY;
        }
    }
}
