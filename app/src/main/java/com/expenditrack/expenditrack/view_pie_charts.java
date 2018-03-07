package com.expenditrack.expenditrack;

import android.animation.ValueAnimator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.graphics.*;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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

    DecimalFormat df = new DecimalFormat("#.#");

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

    }

    @Override
    public void onStart() {
        super.onStart();
        setupIntroAnimation();
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
        animator.setDuration(1500);
        animator.start();

//        Segment segment = new Segment("Powercity", 10);
//        SegmentFormatter formatter = new SegmentFormatter(Color.RED);
//
//        pie.addSegment(segment, formatter);
//
//        PieRenderer pieRenderer = new PieRenderer(pie);
//        pieRenderer.setDonutSize(Float.parseFloat("0.5"), PieRenderer.DonutMode.PERCENT);
//
//        pie.getRenderer(PieRenderer.class).setStartDegs(90);

//        // initialize our XYPlot reference:
//        plot = (XYPlot) findViewById(R.id.plot);
//
//        // create a couple arrays of y-values to plot:
//        final Number[] domainLabels = {1, 2, 3, 6, 7, 8, 9, 10, 13, 14};
//        Number[] series1Numbers = {1, 4, 2, 8, 4, 16, 8, 32, 16, 64};
//        Number[] series2Numbers = {5, 2, 10, 5, 20, 10, 40, 20, 80, 40};
//
//        // turn the above arrays into XYSeries':
//        // (Y_VALS_ONLY means use the element index as the x value)
//        XYSeries series1 = new SimpleXYSeries(
//                Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");
//        XYSeries series2 = new SimpleXYSeries(
//                Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");
//
//        // create formatters to use for drawing a series using LineAndPointRenderer
//        // and configure them from xml:
//        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.GREEN, Color.BLUE, null);
//
//        LineAndPointFormatter series2Format = new LineAndPointFormatter(Color.RED, Color.GREEN, Color.BLUE, null);
//
//
//        // add an "dash" effect to the series2 line:
//        series2Format.getLinePaint().setPathEffect(new DashPathEffect(new float[] {
//
//                // always use DP when specifying pixel sizes, to keep things consistent across devices:
//                PixelUtils.dpToPix(20),
//                PixelUtils.dpToPix(15)}, 0));
//
//        // just for fun, add some smoothing to the lines:
//        // see: http://androidplot.com/smooth-curves-and-androidplot/
//        series1Format.setInterpolationParams(
//                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
//
//        series2Format.setInterpolationParams(
//                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
//
//        // add a new series' to the xyplot:
//        plot.addSeries(series1, series1Format);
//        plot.addSeries(series2, series2Format);
//
//        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
//            @Override
//            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
//                int i = Math.round(((Number) obj).floatValue());
//                return toAppendTo.append(domainLabels[i]);
//            }
//            @Override
//            public Object parseObject(String source, ParsePosition pos) {
//                return null;
//            }
//        });
    }
}
