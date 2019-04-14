package com.lsm.histogramview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import java.util.Collections;
import java.util.List;

public class HistogramView extends View {
    //图表标题
    private String graphTitle = "";
    //标题字体的大小
    private int graphTitleSize = 18;
    //标题的字体颜色
    private int graphTitleColor = Color.RED;
    //x轴名称
    private String xAxisName = "";
    //y轴名称
    private String yAxisName = "";
    //坐标轴字体颜色
    private int axisTextSize = 12;
    //坐标轴字体颜色
    private int axisTextColor = Color.BLACK;
    //x y坐标线条的颜色
    private int axisLineColor = Color.BLACK;
    //x，y坐标线的宽度
    private int axisLineWidth = 2;
    private Paint mPaint;
    private int screenWith, screenHeight;
    //视图的宽度
    private int width;
    //视图的高度
    private int height;
    //起点x坐标值
    private int originalX;
    //起点y坐标值
    private int originalY;
    //y轴等份划分
    private int axisDivideSizeY;

    //标题距离x轴的距离
    private int titleMarginXaxis = 60;
    //x y轴刻度的高度
    private int xAxisScaleHeight = 5;
    //刻度的最大值
    private Integer maxValue;
    //y轴空留部分高度
    private int yMarign = 30;

    //柱状图数据
    private List<Integer> columnList;
    //柱状图颜色
    private List<Integer> columnColors;

    public HistogramView(Context context) {
        this(context, null);
    }

    public HistogramView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistogramView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取屏幕的宽高
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        screenWith = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        initAttrs(context, attrs);
        initPaint();
    }

    /**
     * //获取自定义属性
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HistogramView);
        graphTitle = array.getString(R.styleable.HistogramView_graphTitle);
        xAxisName = array.getString(R.styleable.HistogramView_xAxisName);
        yAxisName = array.getString(R.styleable.HistogramView_yAxisName);
        axisTextSize = array.getDimensionPixelSize(R.styleable.HistogramView_axisTextSize, sp2px(axisTextSize));
        axisTextColor = array.getColor(R.styleable.HistogramView_axisTextColor, axisTextColor);
        axisLineColor = array.getColor(R.styleable.HistogramView_axisLineColor, axisLineColor);
        graphTitleSize = array.getDimensionPixelSize(R.styleable.HistogramView_graphTitleSize, sp2px(graphTitleSize));
        graphTitleColor = array.getColor(R.styleable.HistogramView_graphTitleColor, graphTitleColor);
        axisLineWidth = (int) array.getDimension(R.styleable.HistogramView_axisLineWidth, dip2px(axisLineWidth));
        array.recycle();
    }

    /**
     * 初始化paint
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int w = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST) {
            w = screenWith;
        }
        int h = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            h = screenHeight;
        }
        setMeasuredDimension(w, h);
        if (width == 0 || height == 0) {
            //x轴的起点位置
            originalX = dip2px(30);
            //视图的宽度  空间的宽度减去左边和右边的位置
            width = getMeasuredWidth() - originalX * 2;
            //y轴的起点位置 空间高度的2/3
            originalY = getMeasuredHeight() * 2 / 3;
            //图表显示的高度为空间高度的一半
            height = getMeasuredHeight() / 2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制标题
        drawTitle(canvas);
        //绘制x轴
        drawXAxis(canvas);
        //绘制y轴
        drawYAxis(canvas);
        //绘制x轴刻度
//        drawXAxisScale(canvas);
        //绘制x轴刻度值
        drawXAxisScaleValue(canvas);
        //绘制y轴刻度
        drawYAxisScale(canvas);
        drawYAxisScaleValue(canvas);
        //绘制x轴箭头
        drawXAxisArrow(canvas);
        //绘制y轴箭头
        drawYAxisArrow(canvas);
        //绘制柱状图
        drawColumn(canvas);
    }

    /**
     * 绘制柱状图
     *
     * @param canvas
     */
    protected void drawColumn(Canvas canvas) {
        if (columnList != null && columnColors != null) {
            float cellWidth = width / (columnList.size() + 2);
            //根据最大值和高度计算比例
            float scale = (height - dip2px(yMarign)) / maxValue;
            for (int i = 0; i < columnList.size(); i++) {
                mPaint.setColor(columnColors.get(i));
                float leftTopY = originalY - columnList.get(i) * scale;
                canvas.drawRect(originalX + cellWidth * (i + 1),
                        leftTopY,
                        originalX + cellWidth * (i + 2),
                        originalY - axisLineWidth / 2,
                        mPaint);
            }
        }
    }

    /**
     * 绘制y轴箭头
     *
     * @param canvas
     */
    private void drawYAxisArrow(Canvas canvas) {
        mPaint.setColor(axisTextColor);
        Path yPath = new Path();
        yPath.moveTo(originalX, originalY - height - 30);
        yPath.lineTo(originalX - 10, originalY - height);
        yPath.lineTo(originalX + 10, originalY - height);
        yPath.close();
        canvas.drawPath(yPath, mPaint);
        //绘制y轴名称
        if (!TextUtils.isEmpty(yAxisName)) {
            canvas.drawText(yAxisName, originalX - 50, originalY - height - 35, mPaint);
        }
    }

    /**
     * 绘制x轴箭头
     *
     * @param canvas
     */
    private void drawXAxisArrow(Canvas canvas) {
        mPaint.setColor(axisTextColor);
        Path xPath = new Path();
        xPath.moveTo(originalX + width + 30, originalY);
        xPath.lineTo(originalX + width, originalY + 10);
        xPath.lineTo(originalX + width, originalY - 10);
        xPath.close();
        canvas.drawPath(xPath, mPaint);
        //绘制x轴名称
        if (!TextUtils.isEmpty(xAxisName)) {
            canvas.drawText(xAxisName, originalX + width, originalY + 50, mPaint);
        }
    }

    /**
     * 绘制y轴刻度值
     *
     * @param canvas
     */
    protected void drawYAxisScaleValue(Canvas canvas) {
        try {
            mPaint.setColor(axisTextColor);
            mPaint.setTextSize(axisTextSize);
            int cellHeight = (height - dip2px(yMarign)) / axisDivideSizeY;
            float cellValue = maxValue / (axisDivideSizeY + 0f);
            //这里只处理的大于1时的绘制  小于等于1的绘制没有处理
            int ceil = (int) Math.ceil(cellValue);
//            DecimalFormat df2 = new DecimalFormat("###.00");
//            String format = df2.format(ceil);
//            float result = Float.parseFloat(format);
            for (int i = 0; i < axisDivideSizeY + 1; i++) {
                if (i == 0) {
                    continue;
                }
                String s = ceil * i + "";
                float v = mPaint.measureText(s);
                canvas.drawText(s,
                        originalX - v - 10,
                        originalY - cellHeight * i + 10,
                        mPaint);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 绘制y轴刻度
     *
     * @param canvas
     */
    protected void drawYAxisScale(Canvas canvas) {
        mPaint.setColor(axisLineColor);
        float cellHeight = (height - dip2px(yMarign)) / axisDivideSizeY;
        for (int i = 0; i < axisDivideSizeY; i++) {
            canvas.drawLine(originalX,
                    originalY - cellHeight * (i + 1),
                    originalX + 10,
                    originalY - cellHeight * (i + 1),
                    mPaint);
        }
    }

    /**
     * 绘制x轴刻度值
     *
     * @param canvas
     */
    protected void drawXAxisScaleValue(Canvas canvas) {
        int xTxtMargin = dip2px(15);
        mPaint.setColor(axisTextColor);
        mPaint.setTextSize(axisTextSize);
        mPaint.setFakeBoldText(true);
        float cellWidth = width / (columnList.size() + 2);
        for (int i = 0; i < columnList.size() + 1; i++) {
            if (i == 0) {
                continue;
            }
            String txt = i + "";
            //测量文字的宽度
            float txtWidth = mPaint.measureText(txt);
            canvas.drawText(txt, cellWidth * i + originalX + (cellWidth / 2 - txtWidth / 2),
                    originalY + xTxtMargin,
                    mPaint);
        }
    }

    /**
     * 绘制x轴刻度
     *
     * @param canvas
     */
    protected void drawXAxisScale(Canvas canvas) {
        Log.e("TAG", columnList.size() + "");
        mPaint.setColor(axisLineColor);
        int scaleNum = columnList.size() + 2;
        float cellWidth = width / scaleNum;
        for (int i = 0; i < scaleNum - 1; i++) {
            canvas.drawLine(cellWidth * (i + 1) + originalX,
                    originalY,
                    cellWidth * (i + 1) + originalX,
                    originalY - dip2px(xAxisScaleHeight), mPaint);
        }
    }

    /**
     * 绘制标题
     *
     * @param canvas
     */
    private void drawTitle(Canvas canvas) {
        if (!TextUtils.isEmpty(graphTitle)) {
            //绘制标题
            mPaint.setTextSize(graphTitleSize);
            mPaint.setColor(graphTitleColor);
            //设置文字粗体
            mPaint.setFakeBoldText(true);
            //获取文字的宽度
            float measureText = mPaint.measureText(graphTitle);
            canvas.drawText(
                    graphTitle,
                    getWidth() / 2 - measureText / 2,
                    originalY + dip2px(titleMarginXaxis),
                    mPaint
            );
        }
    }

    /**
     * 绘制y轴
     *
     * @param canvas
     */
    protected void drawYAxis(Canvas canvas) {
        mPaint.setColor(axisLineColor);
        mPaint.setStrokeWidth(axisLineWidth);
        canvas.drawLine(originalX, originalY, originalX, originalY - height, mPaint);
    }

    /**
     * 绘制x轴
     *
     * @param canvas
     */
    protected void drawXAxis(Canvas canvas) {
        mPaint.setColor(axisLineColor);
        mPaint.setStrokeWidth(axisLineWidth);
        canvas.drawLine(originalX, originalY, originalX + width, originalY, mPaint);
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    private int dip2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }

    /**
     * 调用该方法进行图表的设置
     * @param columnList 柱状图的数据
     * @param columnColors  颜色
     * @param axisDivideSizeY y轴显示的等份数
     */
    public void setColumnInfo(List<Integer> columnList, List<Integer> columnColors, int axisDivideSizeY) {
        this.columnList = columnList;
        this.columnColors = columnColors;
        this.axisDivideSizeY = axisDivideSizeY;
        //获取刻度的最大值
        maxValue = Collections.max(columnList);
        Log.e("TAG", "maxValue-->" + maxValue);
        invalidate();
    }
}
