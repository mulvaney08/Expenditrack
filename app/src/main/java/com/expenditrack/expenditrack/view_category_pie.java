package com.expenditrack.expenditrack;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.PieRenderer;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.XYPlot;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Aaron on 08/03/2018.
 */

public class view_category_pie extends AppCompatActivity {

    private XYPlot plot;
    public static final int SELECTED_SEGMENT_OFFSET = 50;

    public PieChart pie;

    private Segment s1;
    private Segment s2;
    private Segment s3;
    private Segment s4;

    private double totalCategory1;
    private double totalCategory2;
    private double totalCategory3;
    private double totalCategory4;

    private static double categpory1SliceSize;
    private static double categpory2SliceSize;
    private static double categpory3SliceSize;
    private static double categpory4SliceSize;

    TextView category1, category2, category3, category4;

    public void calculateSliceOfPie(double totalShop1, double totalShop2, double totalShop3, double totalShop4) {

        double total = totalShop1 + totalShop2 + totalShop3 + totalShop4;

        categpory1SliceSize = round(totalShop1 / total * 10, 1);
        categpory2SliceSize = round(totalShop2 / total * 10, 1);
        categpory3SliceSize = round(totalShop3 / total * 10, 1);
        categpory4SliceSize = round(totalShop4 / total * 10, 1);

        Log.d("Total Spent: " + total, " total ");
        Log.d("Powercity: " + categpory1SliceSize, "is the size of the slice");
        Log.d("Boots: " + categpory2SliceSize, "is the size of the slice");
        Log.d("Tesco: " + categpory3SliceSize, "is the size of the slice");
        Log.d("Easons: " + categpory4SliceSize, "is the size of the slice");


    }

    private static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public void getContents() {
        //Get contents from Firebase into String From : https://www.youtube.com/watch?v=WDGmpvKpHyw
        Utils.receiptRef.addListenerForSingleValueEvent(new ValueEventListener() { //SingleValueEvent Listener to prevent the append method causing duplicate entries

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("supplierName").getValue().toString().equalsIgnoreCase("Boots") && ds.child("category").getValue().toString().equalsIgnoreCase("Electronics")) {
                        totalCategory1 += Double.parseDouble(ds.child("totalSpent").getValue().toString());
                        Log.d("Boots category Elec: " + totalCategory1, "is the total spent");
                    } else if (ds.child("supplierName").getValue().toString().equalsIgnoreCase("Boots") && ds.child("category").getValue().toString().equalsIgnoreCase("Household Cleaning")) {
                        totalCategory2 += Double.parseDouble(ds.child("totalSpent").getValue().toString());
                        Log.d("Boots category Clean: " + totalCategory2, "is the total spent");

                    } else if (ds.child("supplierName").getValue().toString().equalsIgnoreCase("Boots") && ds.child("category").getValue().toString().equalsIgnoreCase("Health care")) {
                        totalCategory3 += Double.parseDouble(ds.child("totalSpent").getValue().toString());
                        Log.d("Boots category HHold: " + totalCategory3, "is the total spent");

                    } else if (ds.child("supplierName").getValue().toString().equalsIgnoreCase("Boots") && ds.child("category").getValue().toString().equalsIgnoreCase("Personal Care/Beauty")) {
                        totalCategory4 += Double.parseDouble(ds.child("totalSpent").getValue().toString());
                        Log.d("Boots category Beauty: " + totalCategory4, "is the total spent");

                    }
                }
                calculateSliceOfPie(totalCategory1, totalCategory2, totalCategory3, totalCategory4);
                category1 = (TextView) findViewById(R.id.category1_info);
                category2 = (TextView) findViewById(R.id.category2_info);
                category3 = (TextView) findViewById(R.id.category3_info);
                category4 = (TextView) findViewById(R.id.category4_info);

                category1.setText(s1.getTitle() + ": €" + totalCategory1);
                category2.setText(s2.getTitle() + ": €" + totalCategory2);
                category3.setText(s3.getTitle() + ": €" + totalCategory3);
                category4.setText(s4.getTitle() + ": €" + totalCategory4);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Snackbar receiptsLoaded = Snackbar.make(findViewById(R.id.activity_view_receipts2), "Receipts Loaded", Snackbar.LENGTH_LONG);
        //receiptsLoaded.show();
        //Toast.makeText(this,"Receipts Loaded",Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_pie);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.viewReceiptsToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);


        // initialize our XYPlot reference:
        pie = (PieChart) findViewById(R.id.mySimplePieChart);

        // enable the legend:
        //pie.getLegend().setVisible(true);

        final float padding = PixelUtils.dpToPix(30);
        pie.getPie().setPadding(padding, padding, padding, padding);

        // detect segment clicks:
        pie.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                PointF click = new PointF(motionEvent.getX(), motionEvent.getY());
                if (pie.getPie().containsPoint(click)) {
                    Segment segment = pie.getRenderer(PieRenderer.class).getContainingSegment(click);

                    if (segment != null) {
                        final boolean isSelected = getFormatter(segment).getOffset() != 0;
                        deselectAll();
                        setSelected(segment, !isSelected);
                        pie.redraw();
                    }
                }
                return false;
            }

            private SegmentFormatter getFormatter(Segment segment) {
                return pie.getFormatter(segment, PieRenderer.class);
            }

            private void deselectAll() {
                List<Segment> segments = pie.getRegistry().getSeriesList();
                for (Segment segment : segments) {
                    setSelected(segment, false);
                }
            }

            private void setSelected(Segment segment, boolean isSelected) {
                SegmentFormatter f = getFormatter(segment);
                if (isSelected) {
                    f.setOffset(SELECTED_SEGMENT_OFFSET);
                } else {
                    f.setOffset(0);
                }
            }
        });

        getContents();

        s1 = new Segment("Electronics", categpory1SliceSize);
        s2 = new Segment("Household Cleaning", categpory2SliceSize);
        s3 = new Segment("Health care", categpory3SliceSize);
        s4 = new Segment("Personal Care/Beauty", categpory4SliceSize);


        EmbossMaskFilter emf = new EmbossMaskFilter(
                new float[]{1, 1, 1}, 0.4f, 10, 8.2f);

        SegmentFormatter sf1 = new SegmentFormatter(this, R.xml.segment_formatter1);
        sf1.getLabelPaint().setShadowLayer(3, 0, 0, Color.BLACK);
        sf1.getFillPaint().setMaskFilter(emf);

        SegmentFormatter sf2 = new SegmentFormatter(this, R.xml.segment_formatter2);
        sf2.getLabelPaint().setShadowLayer(3, 0, 0, Color.BLACK);
        sf2.getFillPaint().setMaskFilter(emf);

        SegmentFormatter sf3 = new SegmentFormatter(this, R.xml.segment_formatter3);
        sf3.getLabelPaint().setShadowLayer(3, 0, 0, Color.BLACK);
        sf3.getFillPaint().setMaskFilter(emf);

        SegmentFormatter sf4 = new SegmentFormatter(this, R.xml.segment_formatter4);
        sf4.getLabelPaint().setShadowLayer(3, 0, 0, Color.BLACK);
        sf4.getFillPaint().setMaskFilter(emf);

        pie.addSegment(s1, sf1);
        pie.addSegment(s2, sf2);
        pie.addSegment(s3, sf3);
        pie.addSegment(s4, sf4);

        pie.getBorderPaint().setColor(Color.TRANSPARENT);
        pie.getBackgroundPaint().setColor(Color.TRANSPARENT);

        Intent loadme = new Intent(this, view_category_pie.class);

        setupIntroAnimation();
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        getContents();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    protected void setupIntroAnimation() {

        final PieRenderer renderer = pie.getRenderer(PieRenderer.class);
        // start with a zero degrees pie:

        renderer.setExtentDegs(0);
        // animate a scale value from a starting val of 0 to a final value of 1:
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);

        // use an animation pattern that begins and ends slowly:
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float scale = valueAnimator.getAnimatedFraction();
                renderer.setExtentDegs(360 * scale);
                pie.redraw();
            }
        });

        // the animation will run for 1 second:
        animator.setDuration(1000);
        animator.start();

    }

}
