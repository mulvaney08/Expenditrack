package com.expenditrack.expenditrack;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.widget.SeekBar;
import android.widget.TextView;

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
import java.text.ParsePosition;
import java.util.*;

public class view_pie_charts extends AppCompatActivity {

    private XYPlot plot;
    public static final int SELECTED_SEGMENT_OFFSET = 50;

    //private TextView donutSizeTextView;
    //private SeekBar donutSizeSeekBar;

    public PieChart pie;

    private Segment s1;
    private Segment s2;
    private Segment s3;
    private Segment s4;

    private double totalShop1;
    private double totalShop2;
    private double totalShop3;
    private double totalShop4;

    private static double shop1SliceSize;
    private static double shop2SliceSize;
    private static double shop3SliceSize;
    private static double shop4SliceSize;


    TextView shop1, shop2, shop3, shop4;

    public void calculateSliceOfPie(double totalShop1, double totalShop2, double totalShop3, double totalShop4){

        double total = totalShop1 + totalShop2 + totalShop3 + totalShop4;

        shop1SliceSize = round(totalShop1 / total * 10, 1);
        shop2SliceSize = round(totalShop2 / total * 10, 1);
        shop3SliceSize = round(totalShop3 / total * 10, 1);
        shop4SliceSize = round(totalShop4 / total * 10, 1);

        Log.d("Total Spent: " + total, " total " );
        Log.d("Powercity: " + shop1SliceSize, "is the size of the slice" );
        Log.d("Boots: " + shop2SliceSize, "is the size of the slice" );
        Log.d("Tesco: " + shop3SliceSize, "is the size of the slice" );
        Log.d("Easons: " + shop4SliceSize, "is the size of the slice" );


    }

    private static double round(double value, int precision){
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public void viewCategoryGraph(View view){
        Intent intent = new Intent(this,view_category_pie.class);
        startActivity(intent);
    }


    public void getContents(){
        //Get contents from Firebase into String From : https://www.youtube.com/watch?v=WDGmpvKpHyw
        Utils.receiptRef.addListenerForSingleValueEvent(new ValueEventListener() { //SingleValueEvent Listener to prevent the append method causing duplicate entries

            @Override
            public void onDataChange (DataSnapshot dataSnapshot){

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.child("supplierName").getValue().toString().equalsIgnoreCase("Powercity")){
                        totalShop1 += Double.parseDouble(ds.child("totalSpent").getValue().toString());
                        Log.d("Powercity: " + totalShop1, "is the total spent" );
                    }
                    else if(ds.child("supplierName").getValue().toString().equalsIgnoreCase("Boots")){
                        totalShop2 += Double.parseDouble(ds.child("totalSpent").getValue().toString());
                        Log.d("Boots: " + totalShop2, "is the total spent" );

                    }
                    else if(ds.child("supplierName").getValue().toString().equalsIgnoreCase("Tesco") ){
                        totalShop3 += Double.parseDouble(ds.child("totalSpent").getValue().toString());
                        Log.d("Tesco: " + totalShop3, "is the total spent" );

                    }
                    else if(ds.child("supplierName").getValue().toString().equalsIgnoreCase("Easons")){
                        totalShop4 += Double.parseDouble(ds.child("totalSpent").getValue().toString());
                        Log.d("Easons: " + totalShop4, "is the total spent" );

                    }
                }
                calculateSliceOfPie(totalShop1,totalShop2,totalShop3,totalShop4);
                shop1 = (TextView) findViewById(R.id.shop1_info);
                shop2 = (TextView) findViewById(R.id.shop2_info);
                shop3 = (TextView) findViewById(R.id.shop3_info);
                shop4 = (TextView) findViewById(R.id.shop4_info);

                shop1.setText(s1.getTitle() + ": €"  +totalShop1);
                shop2.setText(s2.getTitle() + ": €"  +totalShop2);
                shop3.setText(s3.getTitle() + ": €"  +totalShop3);
                shop4.setText(s4.getTitle() + ": €"  +totalShop4);
            }


            @Override
            public void onCancelled (DatabaseError databaseError){

            }
        });
        //Snackbar receiptsLoaded = Snackbar.make(findViewById(R.id.activity_view_receipts2), "Receipts Loaded", Snackbar.LENGTH_LONG);
        //receiptsLoaded.show();
        //Toast.makeText(this,"Receipts Loaded",Toast.LENGTH_SHORT).show();

    }



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pie_charts);

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
                if(pie.getPie().containsPoint(click)) {
                    Segment segment = pie.getRenderer(PieRenderer.class).getContainingSegment(click);

                    if(segment != null) {
                        final boolean isSelected = getFormatter(segment).getOffset() != 0;
                        deselectAll();
                        setSelected(segment, !isSelected);
                        pie.redraw();
                        if(segment.getTitle().equalsIgnoreCase("Boots")){
                            viewCategoryGraph(view);
                        }
                    }
                }
                return false;
            }

            private SegmentFormatter getFormatter(Segment segment) {
                return pie.getFormatter(segment, PieRenderer.class);
            }

            private void deselectAll() {
                List<Segment> segments = pie.getRegistry().getSeriesList();
                for(Segment segment : segments) {
                    setSelected(segment, false);
                }
            }

            private void setSelected(Segment segment, boolean isSelected) {
                SegmentFormatter f = getFormatter(segment);
                if(isSelected) {
                    f.setOffset(SELECTED_SEGMENT_OFFSET);
                } else {
                    f.setOffset(0);
                }
            }
        });



//        donutSizeSeekBar = (SeekBar) findViewById(R.id.donutSizeSeekBar);
//        donutSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {}
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                pie.getRenderer(PieRenderer.class).setDonutSize(seekBar.getProgress()/100f,
//                        PieRenderer.DonutMode.PERCENT);
//                pie.redraw();
//                updateDonutText();
//            }
//        });

        //donutSizeTextView = (TextView) findViewById(R.id.donutSizeTextView);
        //updateDonutText();

        getContents();

        s1 = new Segment("Powercity", shop1SliceSize);
        s2 = new Segment("Boots", shop2SliceSize);
        s3 = new Segment("Tesco", shop3SliceSize);
        s4 = new Segment("Easons", shop4SliceSize);



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

//    protected void updateDonutText() {
//        donutSizeTextView.setText(donutSizeSeekBar.getProgress() + "%");
//    }

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

        // the animation will run for 1.5 seconds:
        animator.setDuration(1000);
        animator.start();

//
    }
}
