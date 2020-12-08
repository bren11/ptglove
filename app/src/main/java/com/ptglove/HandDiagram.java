package com.ptglove;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;

class HandDiagram extends View {
    private Paint colorPaint, imgPaint;
    private Rect r;
    private int width, height, picWidth, picHeight;
    private Bitmap img;
    private Path[] paths;
    private int[] colors;
    private Matrix matrix;

    public HandDiagram(Context context, AttributeSet attrs) {
        super(context, attrs);

        colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        colorPaint.setColor(0xa03BB143);

        //redPaint.setColor(0xa0D30000);

        imgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        imgPaint.setColor(0xff000000);

        r = new Rect(20, 20, 40, 40);
        img = BitmapFactory.decodeResource(getResources(), R.drawable.hand4);
        matrix = new Matrix();

        initPaths();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Joints joint : Joints.values()) {
            if (paths[joint.ordinal()] != null) {
                colorPaint.setColor(colors[joint.ordinal()]);
                SystemClock.sleep(1);
                canvas.drawPath(paths[joint.ordinal()], colorPaint);
            }
        }

        r.set(picWidth, picHeight, width - picWidth, height - picHeight);
        canvas.drawBitmap(img, null, r, imgPaint);
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh) {
        height = h;
        width = w;

        picWidth = (width - (height * 794 / 893)) / 2;
        if (picWidth < 0) {
            picWidth = 0;
            picHeight = (height - (width * 893 / 794)) / 2;
            //Log.d("helloo " , picWidth + "  " + picHeight + "  " + width + "  " + height);
            matrix.setScale(width / 794.0f, width / 750.0f);
        } else {
            picHeight = 0;
            //Log.d("helloo " , picWidth + "  " + picHeight + "  " + width + "  " + height);
            matrix.setScale(height / 860.0f, height / 893.0f);
        }

        for (Joints joint : Joints.values()) {
            if (paths[joint.ordinal()] != null) {
                paths[joint.ordinal()].transform(matrix);
                paths[joint.ordinal()].offset(picWidth, picHeight);
            }
        }
    }

    public void changeColor(Joints joint, int color) {
        colors[joint.ordinal()] = color;
        this.invalidate();
    }

    public void update() {
        this.invalidate();
    }

    private void initPaths() {
        colors = new int[Joints.length()];
        for (int i = 0; i < Joints.length(); i++) {
            colors[i] = 0xa03BB143;
        }

        paths = new Path[Joints.length()];
        paths[Joints.THUMB_UPPER.ordinal()] = new Path();
        paths[Joints.THUMB_UPPER.ordinal()].moveTo(607, 549);
        paths[Joints.THUMB_UPPER.ordinal()].lineTo(690, 595);
        paths[Joints.THUMB_UPPER.ordinal()].lineTo(735, 532);
        paths[Joints.THUMB_UPPER.ordinal()].lineTo(759, 487);
        paths[Joints.THUMB_UPPER.ordinal()].lineTo(681, 445);
        paths[Joints.THUMB_UPPER.ordinal()].lineTo(657, 458);
        paths[Joints.THUMB_UPPER.ordinal()].lineTo(607, 549);

        paths[Joints.THUMB_KNUCKLE.ordinal()] = new Path();
        paths[Joints.THUMB_KNUCKLE.ordinal()].moveTo(607, 549);
        paths[Joints.THUMB_KNUCKLE.ordinal()].lineTo(690, 595);
        paths[Joints.THUMB_KNUCKLE.ordinal()].lineTo(673, 652);
        paths[Joints.THUMB_KNUCKLE.ordinal()].lineTo(550, 795);
        paths[Joints.THUMB_KNUCKLE.ordinal()].lineTo(581, 561);
        paths[Joints.THUMB_KNUCKLE.ordinal()].lineTo(607, 549);

        paths[Joints.INDEX_UPPER.ordinal()] = new Path();
        paths[Joints.INDEX_UPPER.ordinal()].moveTo(506, 200);
        paths[Joints.INDEX_UPPER.ordinal()].lineTo(588, 215);
        paths[Joints.INDEX_UPPER.ordinal()].lineTo(604, 110);
        paths[Joints.INDEX_UPPER.ordinal()].lineTo(525, 97);
        paths[Joints.INDEX_UPPER.ordinal()].lineTo(506, 200);

        paths[Joints.INDEX_MIDDLE.ordinal()] = new Path();
        paths[Joints.INDEX_MIDDLE.ordinal()].moveTo(506, 200);
        paths[Joints.INDEX_MIDDLE.ordinal()].lineTo(588, 215);
        paths[Joints.INDEX_MIDDLE.ordinal()].lineTo(566, 344);
        paths[Joints.INDEX_MIDDLE.ordinal()].lineTo(469, 322);
        paths[Joints.INDEX_MIDDLE.ordinal()].lineTo(506, 200);

        paths[Joints.INDEX_KNUCKLE.ordinal()] = new Path();
        paths[Joints.INDEX_KNUCKLE.ordinal()].moveTo(566, 344);
        paths[Joints.INDEX_KNUCKLE.ordinal()].lineTo(469, 322);
        paths[Joints.INDEX_KNUCKLE.ordinal()].lineTo(456, 362);
        paths[Joints.INDEX_KNUCKLE.ordinal()].lineTo(444, 368);
        paths[Joints.INDEX_KNUCKLE.ordinal()].lineTo(440, 485);
        paths[Joints.INDEX_KNUCKLE.ordinal()].lineTo(558, 505);
        paths[Joints.INDEX_KNUCKLE.ordinal()].lineTo(558, 392);
        paths[Joints.INDEX_KNUCKLE.ordinal()].lineTo(566, 344);

        paths[Joints.MIDDLE_UPPER.ordinal()] = new Path();
        paths[Joints.MIDDLE_UPPER.ordinal()].moveTo(342, 190);
        paths[Joints.MIDDLE_UPPER.ordinal()].lineTo(435, 190);
        paths[Joints.MIDDLE_UPPER.ordinal()].lineTo(430, 70);
        paths[Joints.MIDDLE_UPPER.ordinal()].lineTo(343, 70);
        paths[Joints.MIDDLE_UPPER.ordinal()].lineTo(342, 190);

        paths[Joints.MIDDLE_MIDDLE.ordinal()] = new Path();
        paths[Joints.MIDDLE_MIDDLE.ordinal()].moveTo(342, 190);
        paths[Joints.MIDDLE_MIDDLE.ordinal()].lineTo(435, 190);
        paths[Joints.MIDDLE_MIDDLE.ordinal()].lineTo(432, 310);
        paths[Joints.MIDDLE_MIDDLE.ordinal()].lineTo(342, 310);
        paths[Joints.MIDDLE_MIDDLE.ordinal()].lineTo(342, 190);

        paths[Joints.MIDDLE_KNUCKLE.ordinal()] = new Path();
        paths[Joints.MIDDLE_KNUCKLE.ordinal()].moveTo(432, 310);
        paths[Joints.MIDDLE_KNUCKLE.ordinal()].lineTo(342, 310);
        paths[Joints.MIDDLE_KNUCKLE.ordinal()].lineTo(344, 374);
        paths[Joints.MIDDLE_KNUCKLE.ordinal()].lineTo(331, 382);
        paths[Joints.MIDDLE_KNUCKLE.ordinal()].lineTo(336, 498);
        paths[Joints.MIDDLE_KNUCKLE.ordinal()].lineTo(440, 485);
        paths[Joints.MIDDLE_KNUCKLE.ordinal()].lineTo(444, 368);
        paths[Joints.MIDDLE_KNUCKLE.ordinal()].lineTo(434, 362);
        paths[Joints.MIDDLE_KNUCKLE.ordinal()].lineTo(432, 310);

        paths[Joints.RING_UPPER.ordinal()] = new Path();
        paths[Joints.RING_UPPER.ordinal()].moveTo(188, 252);
        paths[Joints.RING_UPPER.ordinal()].lineTo(270, 223);
        paths[Joints.RING_UPPER.ordinal()].lineTo(240, 121);
        paths[Joints.RING_UPPER.ordinal()].lineTo(161, 149);
        paths[Joints.RING_UPPER.ordinal()].lineTo(188, 252);

        paths[Joints.RING_MIDDLE.ordinal()] = new Path();
        paths[Joints.RING_MIDDLE.ordinal()].moveTo(188, 252);
        paths[Joints.RING_MIDDLE.ordinal()].lineTo(270, 223);
        paths[Joints.RING_MIDDLE.ordinal()].lineTo(303, 337);
        paths[Joints.RING_MIDDLE.ordinal()].lineTo(218, 364);
        paths[Joints.RING_MIDDLE.ordinal()].lineTo(188, 252);

        paths[Joints.RING_KNUCKLE.ordinal()] = new Path();
        paths[Joints.RING_KNUCKLE.ordinal()].moveTo(303, 337);
        paths[Joints.RING_KNUCKLE.ordinal()].lineTo(218, 364);
        paths[Joints.RING_KNUCKLE.ordinal()].lineTo(224, 417);
        paths[Joints.RING_KNUCKLE.ordinal()].lineTo(213, 434);
        paths[Joints.RING_KNUCKLE.ordinal()].lineTo(245, 536);
        paths[Joints.RING_KNUCKLE.ordinal()].lineTo(336, 498);
        paths[Joints.RING_KNUCKLE.ordinal()].lineTo(331, 382);
        paths[Joints.RING_KNUCKLE.ordinal()].lineTo(317, 378);
        paths[Joints.RING_KNUCKLE.ordinal()].lineTo(303, 337);

        paths[Joints.PINKIE_UPPER.ordinal()] = new Path();
        paths[Joints.PINKIE_UPPER.ordinal()].moveTo(75, 274);
        paths[Joints.PINKIE_UPPER.ordinal()].lineTo(14, 309);
        paths[Joints.PINKIE_UPPER.ordinal()].lineTo(59, 396);
        paths[Joints.PINKIE_UPPER.ordinal()].lineTo(127, 357);
        paths[Joints.PINKIE_UPPER.ordinal()].lineTo(75, 274);

        paths[Joints.PINKIE_MIDDLE.ordinal()] = new Path();
        paths[Joints.PINKIE_MIDDLE.ordinal()].moveTo(59, 396);
        paths[Joints.PINKIE_MIDDLE.ordinal()].lineTo(127, 357);
        paths[Joints.PINKIE_MIDDLE.ordinal()].lineTo(177, 427);
        paths[Joints.PINKIE_MIDDLE.ordinal()].lineTo(104, 474);
        paths[Joints.PINKIE_MIDDLE.ordinal()].lineTo(59, 396);

        paths[Joints.PINKIE_KNUCKLE.ordinal()] = new Path();
        paths[Joints.PINKIE_KNUCKLE.ordinal()].moveTo(104, 474);
        paths[Joints.PINKIE_KNUCKLE.ordinal()].lineTo(177, 427);
        paths[Joints.PINKIE_KNUCKLE.ordinal()].lineTo(200, 438);
        paths[Joints.PINKIE_KNUCKLE.ordinal()].lineTo(213, 434);
        paths[Joints.PINKIE_KNUCKLE.ordinal()].lineTo(245, 536);
        paths[Joints.PINKIE_KNUCKLE.ordinal()].lineTo(153, 590);
        paths[Joints.PINKIE_KNUCKLE.ordinal()].lineTo(127, 520);
        paths[Joints.PINKIE_KNUCKLE.ordinal()].lineTo(104, 474);

        paths[Joints.WRIST_F_B.ordinal()] = new Path();
        paths[Joints.WRIST_F_B.ordinal()].moveTo(179, 725);
        paths[Joints.WRIST_F_B.ordinal()].lineTo(238, 811);
        paths[Joints.WRIST_F_B.ordinal()].lineTo(281, 830);
        paths[Joints.WRIST_F_B.ordinal()].lineTo(483, 828);
        paths[Joints.WRIST_F_B.ordinal()].lineTo(550, 795);
        paths[Joints.WRIST_F_B.ordinal()].lineTo(558, 735);
        paths[Joints.WRIST_F_B.ordinal()].lineTo(179, 735);
    }
}