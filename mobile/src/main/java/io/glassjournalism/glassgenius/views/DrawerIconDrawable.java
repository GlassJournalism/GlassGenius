/*
 * Copyright 2014 Johannes Homeier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.glassjournalism.glassgenius.views;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class DrawerIconDrawable extends Drawable {
    public static final int STATE_DRAWER = 0;
    public static final int STATE_ARROW = 1;

    private static final float LEVEL_BREAKPOINT = 0.5f;

    //level of the animation
    private float level;

    //Dimensions
    private float scale = 3;
    private int width;
    private int height;
    private int offsetX;

    //Drawing-Objects
    private Paint mPaint;
    private RectF topRect;
    private RectF middleRect;
    private RectF bottomRect;

    private boolean breakpointReached = false;
    /**
     * Create a new DrawerIconDrawable with size @param size in pixel
     * @param size
     */
    public DrawerIconDrawable(int size){
        this(size, size);
    }

    /**
     * Create a new DrawerIconDrawable with width @param width and height @param height in pixel
     */
    public DrawerIconDrawable(int width, int height){
        this(width, height, 0);
    }

    /**
     * Create a new DrawerIconDrawable with width @param width and height @param height, and X-offset @param offsetX in pixel
     */
    public DrawerIconDrawable(int width, int height, int offsetX){
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;

        scale = Math.min(width, height)/48;

        setBounds(new Rect(0, 0, width, height));

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(0xffffffff);

        topRect = new RectF();
        middleRect = new RectF();
        bottomRect = new RectF();

        setLevel(0);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.translate(offsetX, 0);
        drawTopRect(canvas);
        drawMiddleRect(canvas);
        drawBottomRect(canvas);
    }

    private void drawTopRect(Canvas canvas){
        float scaleFactor = level < LEVEL_BREAKPOINT ? level*2 : (level-0.5f)*2;
        canvas.save();
        offsetTopRect(3*scale*scaleFactor, 0, -3*scale*scaleFactor, 0);
        canvas.rotate(level < LEVEL_BREAKPOINT ? level*450 : 225 + (1-level)*270, 24*scale, 24*scale);
        canvas.drawRect(topRect, mPaint);
        canvas.restore();
    }

    private void drawMiddleRect(Canvas canvas){
        float scaleFactor = level < LEVEL_BREAKPOINT ? level*2 : (level-0.5f)*2;
        canvas.save();
        offsetMiddleRect(0, 0, -2*scale*scaleFactor, 0);
        canvas.rotate(level < LEVEL_BREAKPOINT ? level*360 : 180 + (1-level)*360, 24*scale, 24*scale);
        canvas.drawRect(middleRect, mPaint);
        canvas.restore();
    }


    private void drawBottomRect(Canvas canvas){
        float scaleFactor = level < LEVEL_BREAKPOINT ? level*2 : (level-0.5f)*2;
        canvas.save();
        offsetBottomRect(3*scale*scaleFactor, 0, -3*scale*scaleFactor, 0);
        canvas.rotate(level < LEVEL_BREAKPOINT ? level*270 : 135 + (1-level)*450, 24*scale, 24*scale);
        canvas.drawRect(bottomRect, mPaint);
        canvas.restore();
    }

    private void offsetTopRect(float offsetLeft, float offsetTop, float offsetRight, float offsetBottom){
        topRect.set(
                15*scale + offsetLeft
                ,18*scale + offsetTop
                , 33*scale + offsetRight
                , 20*scale + offsetBottom
        );
    }

    private void offsetMiddleRect(float offsetLeft, float offsetTop, float offsetRight, float offsetBottom){
        middleRect.set(
                15*scale + offsetLeft
                ,23*scale + offsetTop
                , 33*scale + offsetRight
                , 25*scale + offsetBottom
        );
    }

    private void offsetBottomRect(float offsetLeft, float offsetTop, float offsetRight, float offsetBottom){
        bottomRect.set(
                15*scale + offsetLeft
                ,28*scale + offsetTop
                , 33*scale + offsetRight
                , 30*scale + offsetBottom
        );
    }


    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return 0;
    }

    /**
     * set the color of the Drawable;
     * @param color
     */
    public void setColor(int color){
        mPaint.setColor(color);
        invalidateSelf();
    }

    /**
     * set the state of the Drawable;
     * @param level
     */
    public void setState(int state){
        switch(state){
            case STATE_DRAWER:
                setLevel((float) STATE_DRAWER);
                break;
            case STATE_ARROW:
                setLevel((float) STATE_ARROW);
                break;
        }
    }

    /**
     * set the level of the animation; drawer indicator is fully displayed at 0; arrow is fully displayed at 1
     * @param level
     */
    public void setLevel(float level){
        if(level == 1) breakpointReached = true;
        if(level == 0) breakpointReached = false;

        this.level = (breakpointReached ? LEVEL_BREAKPOINT : 0) + level/2;
        invalidateSelf();
    }

    @Override
    public int getIntrinsicWidth() {
        return width;
    }

    @Override
    public int getIntrinsicHeight() {
        return height;
    }
}