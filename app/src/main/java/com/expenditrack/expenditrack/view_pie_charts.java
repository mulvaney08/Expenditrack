package com.expenditrack.expenditrack;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.graphics.*;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.pie.PieChart;
import com.androidplot.pie.PieRenderer;
import com.androidplot.pie.Segment;
import com.androidplot.pie.SegmentFormatter;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.*;

public class view_pie_charts extends AppCompatActivity {

    private XYPlot plot;
    public static final int SELECTED_SEGMENT_OFFSET = 50;

    public PieChart pie;

    private Segment s1;
    private Segment s2;
    private Segment s3;
    private Segment s4;

    private double totalShop1 = 0;
    private double totalShop2 = 0;
    private double totalShop3 = 0;
    private double totalShop4 = 0;

    private String nameShop1 = "";
    private String nameShop2 = "";
    private String nameShop3 = "";
    private String nameShop4 = "";

    private static double shop1SliceSize;
    private static double shop2SliceSize;
    private static double shop3SliceSize;
    private static double shop4SliceSize;

    Intent mainIntent;

    static ArrayList<String> shopNames = new ArrayList<>();
    static ArrayList<Double> totals = new ArrayList<>();

    TextView shop1, shop2, shop3, shop4;

    ProgressBar loading;
    Handler handler = new Handler();
    final int extendRun = 500;

    int control;
    int controlCompare;


    public void calculateSliceOfPie(double totalShop1, double totalShop2, double totalShop3, double totalShop4) {

        double total = totalShop1 + totalShop2 + totalShop3 + totalShop4;

        shop1SliceSize = round(totalShop1 / total * 10, 1);
        shop2SliceSize = round(totalShop2 / total * 10, 1);
        shop3SliceSize = round(totalShop3 / total * 10, 1);
        shop4SliceSize = round(totalShop4 / total * 10, 1);

    }

    private static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public void getContents() {
        //Get contents from Firebase into String From : https://www.youtube.com/watch?v=WDGmpvKpHyw

        try {
            Utils.receiptRef.addListenerForSingleValueEvent(new ValueEventListener() { //SingleValueEvent Listener to prevent the append method causing duplicate entries

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        totals.add(Double.parseDouble(ds.child("totalSpent").getValue().toString()));
                        shopNames.add(ds.child("supplierName").getValue().toString());
                    }
                    control = totals.size();
                    storeInfo();
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(view_pie_charts.this, "Unable to load graph", Toast.LENGTH_SHORT).show();
        }

    }

    public void storeInfo() {
        String holdName1, holdName2, holdName3;
        double holdTotal1, holdTotal2, holdTotal3;
        try {
            nameShop1 = shopNames.get(0);
            totalShop1 = totals.get(0);
            nameShop2 = shopNames.get(1);
            totalShop2 = totals.get(1);
            nameShop3 = shopNames.get(2);
            totalShop3 = totals.get(2);
            nameShop4 = shopNames.get(3);
            totalShop4 = totals.get(3);

            //four values 100, 30, 60, 70
            for (int i = 0; i < shopNames.size(); i++) {
                if (totals.get(i) > totalShop1) { // 30 > 100
                    Log.d(" if checking", "" + totals.get(i) + " against " + totalShop1);
                    holdName1 = nameShop1; // hold name for 50
                    holdTotal1 = totalShop1;  // hold 50
                    totalShop1 = totals.get(i); // set 100
                    nameShop1 = shopNames.get(i); // set name for 100
//                    totalShop2 = holdTotal1;
//                    nameShop2 = holdName1;
                    if (holdTotal1 > totalShop2) {  // 40 > 30
                        Log.d("checking", "" + totals.get(i) + " against " + totalShop2);
                        holdName2 = nameShop2; // hold name for 30
                        holdTotal2 = totalShop2; // hold 30
                        totalShop2 = holdTotal1; // set 40
                        nameShop2 = holdName1; // set name for 40
//                        totalShop3 = holdTotal2;
//                        nameShop3 = holdName2;
                        if (holdTotal2 > totalShop3) {
                            Log.d("checking", "" + totals.get(i) + " against " + totalShop3);
                            holdName3 = nameShop3; // hold name for 20
                            holdTotal3 = totalShop3; // hold 20
                            totalShop3 = holdTotal2; // set 30
                            nameShop3 = holdName2; // set name for 30
//                            totalShop4 = holdTotal3;
//                            nameShop4 = holdName3;
                            if (holdTotal3 > totalShop4) {
                                Log.d("checking", "" + totals.get(i) + " against " + totalShop4);
                                totalShop4 = holdTotal3; // set 20
                                nameShop4 = holdName3; // set name for 20
                            }
                        }
                    }
                } else if (totals.get(i) > totalShop2) { // 30 > 30
                    Log.d(" else if 1 checking", "" + totals.get(i) + " against " + totalShop2);
                    holdName2 = nameShop2; // hold name for 30
                    holdTotal2 = totalShop2; // hold 30
                    totalShop2 = totals.get(i); // set 40
                    nameShop2 = shopNames.get(i); // set name for 40
                    if (holdTotal2 > totalShop3) {
                        Log.d(" else if 1 checking", "" + totals.get(i) + " against " + totalShop3);
                        holdName3 = nameShop3; // hold name for 20
                        holdTotal3 = totalShop3; // hold 20
                        totalShop3 = holdTotal2; // set 30
                        nameShop3 = holdName2; // set name for 30
                        if (holdTotal3 > totalShop4) {
                            Log.d(" else if 1 checking", "" + totals.get(i) + " against " + totalShop4);
                            totalShop4 = holdTotal3; // set 20
                            nameShop4 = holdName3; // set name for 20
                        }
                    }
                } else if (totals.get(i) > totalShop3) {
                    Log.d(" else if 2 checking", "" + totals.get(i) + " against " + totalShop3);
                    holdName3 = nameShop3; // hold name for 20
                    holdTotal3 = totalShop3; // hold 20
                    totalShop3 = totals.get(i); // set 30
                    nameShop3 = shopNames.get(i); // set name for 30
                    if (holdTotal3 > totalShop4) {
                        Log.d(" else if 2 checking", "" + totals.get(i) + " against " + totalShop4);
                        totalShop4 = holdTotal3; // set 20
                        nameShop4 = holdName3; // set name for 20
                    }

                } else if (totals.get(i) > totalShop4) {
                    Log.d(" else if 3 checking", "" + totals.get(i) + " against " + totalShop4);
                    totalShop4 = totals.get(i); // set 20
                    nameShop4 = shopNames.get(i); // set name for 20
                }

                calculateSliceOfPie(totalShop1, totalShop2, totalShop3, totalShop4);
                shop1 = findViewById(R.id.shop1_info);
                shop2 = findViewById(R.id.shop2_info);
                shop3 = findViewById(R.id.shop3_info);
                shop4 = findViewById(R.id.shop4_info);

                shop1.setText(nameShop1 + ": €" + String.format(" %.2f", totalShop1));
                shop2.setText(nameShop2 + ": €" + String.format(" %.2f", totalShop2));
                shop3.setText(nameShop3 + ": €" + String.format(" %.2f", totalShop3));
                shop4.setText(nameShop4 + ": €" + String.format(" %.2f", totalShop4));
                controlCompare++;
            }
//            runInBG(extendRun);
        } catch (Exception e) {
            Toast.makeText(this, "Please add more receipts", Toast.LENGTH_SHORT).show();
            startActivity(mainIntent);
        }
        totals.clear();
        shopNames.clear();

    }

    @Override
    public void onResume() {
        super.onResume();
        getContents();
    }

    public void runInBG(final int extendRun) {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (controlCompare == control) {
                    loading.setVisibility(View.GONE);
                } else {
                    runInBG(extendRun);
                }

            }
        }, 500);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pie_charts);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mainIntent = new Intent(this, Main.class);
//        loading = findViewById(R.id.loading_login);
//        loading.setVisibility(View.GONE);

        Toolbar toolbar = findViewById(R.id.viewReceiptsToolbar);
        setSupportActionBar(toolbar);

        try {
            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        } catch (Exception e) {

        }
        // initialize our XYPlot reference:
        pie = findViewById(R.id.mySimplePieChart);

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

        s1 = new Segment(nameShop1, shop1SliceSize);
        s2 = new Segment(nameShop2, shop2SliceSize);
        s3 = new Segment(nameShop3, shop3SliceSize);
        s4 = new Segment(nameShop4, shop4SliceSize);


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

        setupIntroAnimation();
    }

    @Override
    public void onStart() {
        super.onStart();
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

//
    }
}
