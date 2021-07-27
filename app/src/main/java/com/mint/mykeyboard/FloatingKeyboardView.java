package com.mint.mykeyboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.lang.reflect.Method;

import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

public class FloatingKeyboardView extends KeyboardView {
    private static final int MOVE_THRESHOLD = 0;
    private static final int TOP_PADDING_DP = 28;
    private static final int HANDLE_COLOR = Color.parseColor("#AAD1D6D9");
    private static final int HANDLE_PRESSED_COLOR = Color.parseColor("#D1D6D9");
    private static final float HANDLE_ROUND_RADIUS = 20.0f;
    private static final CornerPathEffect HANDLE_CORNER_EFFECT = new CornerPathEffect(HANDLE_ROUND_RADIUS);
    private static int topPaddingPx;
    private static int width;
    private static Path mHandlePath;
    private static Paint mHandlePaint;
    private static boolean allignBottomCenter = false;
    private static final String TAG = "FloatingKeyboardView";
    private WindowManager windowManager;


//    //Add the view to the window.
//    private final WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams(
//            WindowManager.LayoutParams.WRAP_CONTENT,
//            WindowManager.LayoutParams.WRAP_CONTENT,
//            WindowManager.LayoutParams.TYPE_PHONE,
//            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//            PixelFormat.TRANSLUCENT);

    @SuppressLint("ClickableViewAccessibility")
    public FloatingKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // ((Activity) getContext()).getWindow().setFlags(FLAG_LAYOUT_IN_SCREEN, FLAG_LAYOUT_INSET_DECOR);
        topPaddingPx = (int) convertDpToPixel((float) TOP_PADDING_DP, context);

//        this.setOnKeyboardActionListener(mOnKeyboardActionListener);
        this.setOnTouchListener(mKeyboardOntTouchListener);
        this.setPadding(0, (int) convertDpToPixel(TOP_PADDING_DP, context), 0, 0);


//        //Add the view to the window -- CAN'T KEEP THIS SNIPPET HERE, UNABLE TO INFLATE FLOATING KEYBOARD VIEW ERROR
//        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//        windowManager.addView(mFloatingView, params);

//        // TODO: determine where to keep this snippet
//        //Specify the view position
//        wmParams.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
//        wmParams.x = 0;
//        wmParams.y = 100;
//
//        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//        windowManager.addView(view, wmParams);

        mHandlePaint=new Paint();
        mHandlePaint.setColor(HANDLE_COLOR);
        mHandlePaint.setStyle(Paint.Style.FILL);
        mHandlePaint.setPathEffect(HANDLE_CORNER_EFFECT);
        mHandlePath=new Path();
    }

//    public static boolean isAllignBottomCenter() {
//        return allignBottomCenter;
//    }
//
//    public static void setAllignBottomCenter(boolean allignBottomCenter) {
//        FloatingKeyboardView.allignBottomCenter = allignBottomCenter;
//    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        if (isAllignBottomCenter()) {
//            RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
//            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//            relativeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//            setLayoutParams(relativeLayoutParams);
//        }

    }

    @Override
    public void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        width = xNew;
        drawHandle();
    }

    private void drawHandle() {
        mHandlePath.rewind();
        mHandlePath.moveTo(0, topPaddingPx);
        mHandlePath.lineTo(0, topPaddingPx - 25);
        mHandlePath.lineTo(width / 3, topPaddingPx - 25);
        mHandlePath.lineTo(width / 3, 0);
        mHandlePath.lineTo(2 * width / 3, 0);
        mHandlePath.lineTo(2 * width / 3, topPaddingPx - 25);
        mHandlePath.lineTo(width, topPaddingPx - 25);
        mHandlePath.lineTo(width, topPaddingPx);
        // Draw this line twice to fix strange artifact in API21
        mHandlePath.lineTo(width, topPaddingPx);
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = mHandlePaint;
        Path path = mHandlePath;
        canvas.drawPath(path, paint);
    }

//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//    }

    /**
     * Returns whether the FloatingKeyboardView is visible.
     */
    public boolean isVisible() {
        return this.getVisibility() == View.VISIBLE;
    }

    /**
     * Make the FloatingKeyboardView visible, and hide the system keyboard for view v.
     */
    public void show(View v) {
        this.setVisibility(View.VISIBLE);
        this.setEnabled(true);
//        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        // TODO: Correct Position Keyboard
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getLayoutParams();
        params.topMargin = v.getTop() + v.getHeight();
        params.leftMargin = v.getLeft();
        setLayoutParams(params);
//        windowManager.addView(this, params);
    }

    /**
     * Make the FloatingKeyboardView invisible.
     */
    public void hide() {
        this.setVisibility(View.GONE);
        this.setEnabled(false);
    }

    /**
     * TouchListener to handle the drag of keyboard
     */
    private OnTouchListener mKeyboardOntTouchListener = new View.OnTouchListener() {
        float dx;
        float dy;
        int moveToY;
        int moveToX;
        int distY;
        int distX;
        Rect inScreenCoordinates;
        boolean handleTouched = false;  // Tried using "true"... in vain

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // Use ViewGroup.MarginLayoutParams so as to work inside any layout
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            boolean performClick = false;

            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if (handleTouched) {
                        moveToY = (int) (event.getRawY() - dy);
                        moveToX = (int) (event.getRawX() - dx);

                        // printing values of moveToY and moveToX
                        Log.d(TAG, "Value of moveToY = " + moveToY);
                        Log.d(TAG, "Value of moveToX = " + moveToX);

                        distY = moveToY - params.topMargin;
                        distX = moveToX - params.leftMargin;

                        // checking values of distY and distX
                        Log.d(TAG, "Value of Math.abs(distY) = " + Math.abs(distY));
                        Log.d(TAG, "Value of Math.abs(distX) = " + Math.abs(distX));

                        if (Math.abs(distY) > MOVE_THRESHOLD ||
                                Math.abs(distX) > MOVE_THRESHOLD) {
                            // Ignore any distance before threshold reached
                            moveToY = moveToY - Integer.signum(distY) * Math.min(MOVE_THRESHOLD, Math.abs(distY));
                            moveToX = moveToX - Integer.signum(distX) * Math.min(MOVE_THRESHOLD, Math.abs(distX));

                            inScreenCoordinates = keepInScreen(moveToY, moveToX);

                            // checking top margin and left margin values
                            Log.d(TAG,"Top Margin = " + inScreenCoordinates.top);
                            Log.d(TAG,"Left Margin = " + inScreenCoordinates.left);
                            // TODO : check in keepInScreen as to why inScreenCoordinates.top and .left are zero

                            view.setY(inScreenCoordinates.top);
                            view.setX(inScreenCoordinates.left);


                            Log.d(TAG, "Parent view is " + view.getParent().toString());
                        }
                        performClick = false;
                    } else {
                        performClick = true;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if (handleTouched) {
                        // reset handle color
                        mHandlePaint.setColor(HANDLE_COLOR);
                        mHandlePaint.setStyle(Paint.Style.FILL);
                        invalidate();

                        performClick = false;
                    } else {
                        performClick = true;
                    }

                    break;

                case MotionEvent.ACTION_DOWN:
                    handleTouched = event.getY() <= getPaddingTop(); // Allow move only wher touch on top padding
                    dy = event.getRawY() - view.getY();
                    dx = event.getRawX() - view.getX();

                    //change handle color on tap
                    if (handleTouched) {
                        mHandlePaint.setColor(HANDLE_PRESSED_COLOR);
                        mHandlePaint.setStyle(Paint.Style.FILL);
                        invalidate();
                        performClick = false;
                    } else {
                        performClick = true;
                    }
                    break;
            }
            return !performClick;
        }
    };

    private void moveTo(int y, int x) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getLayoutParams();;
//        Rect inScreenCoordinates = keepInScreen(y, x);
        params.topMargin = y;
        params.leftMargin = x;
        setLayoutParams(params);
    }

    /**
     * Position keyboard to specific point. Caution do not move it outside screen.
     * @param x
     * @param y
     */
    public void positionTo(int x, int y) {
        moveTo (y,x);
    }
    /**
     * @param topMargin of desired position
     * @param leftMargin of desired position
     * @return a Rect with corrected positions so the whole view to stay in screen
     */
    private Rect keepInScreen(int topMargin, int leftMargin) {
        int top = topMargin;
        int left = leftMargin;
//        measure(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        int height = getMeasuredHeight();
        Log.d(TAG,"KeepInScreen: Height = " + height);
        int width = getMeasuredWidth();
        Log.d(TAG, "KeepInScreen: Width = " + width);

        // TODO: Try to explain this !!!
        // we can use getRootView() instead of ((View) getParent())
        int rightCorrection = ((View) getParent()).getPaddingRight() ;
        int botomCorrection =((View) getParent()).getPaddingBottom() ;
        int leftCorrection = ((View) getParent()).getPaddingLeft();
        int topCorrection =((View) getParent()).getPaddingTop();

        Log.d(TAG, "keepInScreen: parent is " + getParent().toString());
//        getGlobalVisibleRect()
        // TODO: why are all correction parameters = 0 ???
        Log.d(TAG, "KeepInScreen: Corrections: Right=" + rightCorrection +
                ", bottom=" + botomCorrection + ", left=" + leftCorrection + ", top=" + topCorrection); // always 0

        Rect rootBounds = new Rect();
        getRootView().getGlobalVisibleRect(rootBounds);
        // keyboard (along with the handle bar) now moves along y-axis but it cannot move beyond (keyboard_height+handleBar_height) - thanks to getGlobalVisibleRect()
        // TODO: keyboard doesn't move along x-axis
//        ((View) getParent()).getHitRect(rootBounds);
        rootBounds.set(rootBounds.left+leftCorrection,rootBounds.top+topCorrection,rootBounds.right-rightCorrection,rootBounds.bottom-botomCorrection);
        Log.d(TAG, "KeepInScreen: Rootbounds are: Left=" + rootBounds.left + ", top=" + rootBounds.top + ", right=" + rootBounds.right + ", bottom=" + rootBounds.bottom);
//        Rootbounds are: Left=0, top=0, right=1080, bottom=902

        if (top <= rootBounds.top)
            top = rootBounds.top;
        else if (top + height > rootBounds.bottom)
            top = rootBounds.bottom - height;

        Log.d(TAG, "KeepInScreen: Top is " + top); // always 0

        if (left <= rootBounds.left)
            left = rootBounds.left;
        else if (left + width > rootBounds.right)
            left = rootBounds.right - width;

        Log.d(TAG, "KeepInScreen: Left is " + left); // always 0

//            Log.e("x0:"+rootBounds.left+" y0:"+rootBounds.top+" Sx:"+rootBounds.right+" Sy:"+rootBounds.bottom, "INPUT:left:"+leftMargin+" top:"+topMargin+
//                    " OUTPUT:left:"+left+" top:"+top+" right:"+(left + getWidth())+" bottom:"+(top + getHeight()));
        return new Rect(left, top, left + width, top + height);
//        return new Rect(leftMargin, topMargin, leftMargin + width, topMargin + height);
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    private static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
}