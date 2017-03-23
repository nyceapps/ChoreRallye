
package com.nyceapps.chorerallye.race;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.db.chart.Tools;
import com.db.chart.animation.Animation;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.renderer.XRenderer;
import com.db.chart.view.HorizontalBarChartView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nyceapps.chorerallye.R;
import com.nyceapps.chorerallye.chore.ChoreItem;
import com.nyceapps.chorerallye.main.RallyeApplication;
import com.nyceapps.chorerallye.main.RallyeData;
import com.nyceapps.chorerallye.member.MemberItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaceStatisticsActivity extends AppCompatActivity {
    private RallyeData data;

    private List<Integer> colors;

    private PieChart choresPieChart;
    //private HorizontalBarChart membersChoresBarChart;
    private HorizontalBarChartView membersChoresBarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_statistics);

        data = ((RallyeApplication) this.getApplication()).getRallyeData();

        initColors();

        //initChoresPieChart();

        initMembersChoresBarChart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.statistics_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.switch_statistics_chart:
                if (choresPieChart.getVisibility() == View.VISIBLE) {
                    choresPieChart.setVisibility(View.GONE);
                    membersChoresBarChart.setVisibility(View.VISIBLE);
                } else {
                    choresPieChart.setVisibility(View.VISIBLE);
                    membersChoresBarChart.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }

        return true;
    }

    private void initColors() {
        colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.JOYFUL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.COLORFUL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.LIBERTY_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.PASTEL_COLORS) {
            colors.add(c);
        }
        colors.add(ColorTemplate.getHoloBlue());
    }

    /*
    private void initChoresPieChart() {
        choresPieChart = (PieChart) findViewById(R.id.chores_pie_chart);

        choresPieChart.setTouchEnabled(false);
        choresPieChart.setUsePercentValues(true);
        choresPieChart.getDescription().setEnabled(false);
        choresPieChart.setExtraOffsets(5, 10, 5, 5);
        choresPieChart.setDrawEntryLabels(false);
        choresPieChart.setDrawHoleEnabled(false);
        choresPieChart.setDrawCenterText(true);
        choresPieChart.setRotationAngle(0);
        choresPieChart.setRotationEnabled(false);
        choresPieChart.setHighlightPerTapEnabled(false);

        PieData choresPieData = prepareChoresPieChartData();
        choresPieChart.setData(choresPieData);
        choresPieChart.highlightValues(null);

        Legend choresPieChartlegend = choresPieChart.getLegend();
        choresPieChartlegend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        choresPieChartlegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        choresPieChartlegend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        choresPieChartlegend.setWordWrapEnabled(true);
        choresPieChartlegend.setDrawInside(true);
        choresPieChartlegend.setEnabled(true);
    }

    private PieData prepareChoresPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        Map<String, Integer> choresCountMap = new HashMap<>();
        List<RaceItem> raceItems = data.getRace().getRaceItems();
        for (RaceItem raceItem : raceItems) {
            String choreName = raceItem.getChoreName();
            int choreValue = raceItem.getChoreValue();

            Integer count = choresCountMap.get(choreName);
            if (count == null) {
                count = new Integer(0);
            }
            count += choreValue;
            choresCountMap.put(choreName, count);
        }
        for (Map.Entry<String, Integer> choreCount : choresCountMap.entrySet()) {
            PieEntry pieEntry = new PieEntry((float) choreCount.getValue(), choreCount.getKey());
            entries.add(pieEntry);
        }

        PieDataSet dataSet = new PieDataSet(entries, null); //getString(R.string.statistics_label_chores_pie_chart));

        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.DKGRAY);

        return data;
    }
    */

    private void initMembersChoresBarChart() {
        membersChoresBarChart = (HorizontalBarChartView) findViewById(R.id.members_chores_bar_chart);

        BarSet membersChoresBarSet = prepareMembersChoresBarChartData();
        membersChoresBarChart.addData(membersChoresBarSet);
        membersChoresBarChart.setBarSpacing(Tools.fromDpToPx(4));

        membersChoresBarChart.setBorderSpacing(0)
                .setXAxis(true)
                .setYAxis(false)
                .setLabelsColor(Color.DKGRAY)
                .setXLabels(XRenderer.LabelPosition.OUTSIDE);

        membersChoresBarChart.show();
    }

    private BarSet prepareMembersChoresBarChartData() {
        Map<String, Map<String, Integer>> membersChoresCountMap = new HashMap<>();

        List<RaceItem> raceItems = data.getRace().getRaceItems();
        for (RaceItem raceItem : raceItems) {
            String memberName = raceItem.getMemberName();
            Map<String, Integer> choresCountMap = membersChoresCountMap.get(memberName);
            if (choresCountMap == null) {
                choresCountMap = new HashMap<>();
            }

            String choreName = raceItem.getChoreName();
            int choreValue = raceItem.getChoreValue();
            Integer count = choresCountMap.get(choreName);
            if (count == null) {
                count = new Integer(0);
            }
            count++;
            choresCountMap.put(choreName, count);

            membersChoresCountMap.put(memberName, choresCountMap);
        }

        List<MemberItem> members = data.getMembers();
        List<ChoreItem> chores = data.getChores();

        BarSet barSet = new BarSet();
        for (ChoreItem chore : chores) {
            for (int m = 0; m < members.size(); m++) {
                MemberItem member = members.get(m);

                int barValue = 0;
                Map<String, Integer> choresCountMap = membersChoresCountMap.get(member.getName());
                if (choresCountMap != null) {
                    Integer count = choresCountMap.get(chore.getName());
                    if (count != null) {
                        barValue = count;
                    }
                }

                String barLabel = (m == members.size() - 1 ? chore.getName() : "");
                Bar bar = new Bar(barLabel, barValue);
                int colorIdx = m;
                if (colorIdx > colors.size()) {
                    colorIdx -= colors.size();
                }
                bar.setColor(colors.get(colorIdx));
                barSet.addBar(bar);
            }
        }

        return barSet;
    }
    /*
    private void initMembersChoresBarChart() {
        membersChoresBarChart = (HorizontalBarChart) findViewById(R.id.members_chores_bar_chart);

        membersChoresBarChart.setTouchEnabled(false);
        membersChoresBarChart.setDrawBarShadow(false);
        membersChoresBarChart.setDrawValueAboveBar(true);
        membersChoresBarChart.getDescription().setEnabled(false);
        membersChoresBarChart.setPinchZoom(false);
        membersChoresBarChart.setDrawGridBackground(false);

        initMembersChoresBarChartData();

        XAxis xAxis = membersChoresBarChart.getXAxis();
        //xAxis.setCenterAxisLabels(true);
        //xAxis.setAxisMinimum(0f);
        xAxis.setLabelCount(100, true);
        BarData barData = membersChoresBarChart.getData();
        if (barData != null) {
            IBarDataSet firstBarDataSet = barData.getDataSetByIndex(0);
            if (firstBarDataSet != null) {
                xAxis.setValueFormatter(new LabelValueFormatter(firstBarDataSet));
            }
        }
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

        YAxis axisLeft = membersChoresBarChart.getAxisLeft();
        axisLeft.setDrawAxisLine(true);
        axisLeft.setDrawGridLines(true);
        axisLeft.setAxisMinimum(0f);

        YAxis axisRight = membersChoresBarChart.getAxisRight();
        axisRight.setDrawAxisLine(true);
        axisRight.setDrawGridLines(false);
        axisRight.setAxisMinimum(0f);

        membersChoresBarChart.setFitBars(true);

        Legend membersChoresBarChartLegend = membersChoresBarChart.getLegend();
        membersChoresBarChartLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        membersChoresBarChartLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        membersChoresBarChartLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        membersChoresBarChartLegend.setDrawInside(false);
        membersChoresBarChartLegend.setFormSize(8f);
        membersChoresBarChartLegend.setXEntrySpace(4f);
    }

    private void initMembersChoresBarChartData() {
        Map<String, Map<String, Integer>> membersChoresCountMap = new HashMap<>();

        List<RaceItem> raceItems = data.getRace().getRaceItems();
        for (RaceItem raceItem : raceItems) {
            String memberName = raceItem.getMemberName();
            Map<String, Integer> choresCountMap = membersChoresCountMap.get(memberName);
            if (choresCountMap == null) {
                choresCountMap = new HashMap<>();
            }

            String choreName = raceItem.getChoreName();
            int choreValue = raceItem.getChoreValue();
            Integer count = choresCountMap.get(choreName);
            if (count == null) {
                count = new Integer(0);
            }
            count++;
            choresCountMap.put(choreName, count);

            membersChoresCountMap.put(memberName, choresCountMap);
        }

        List<MemberItem> members = data.getMembers();
        List<ChoreItem> chores = data.getChores();

        float barWidth = 9f;
        float spaceForBar = 10f;
        List<List<BarEntry>> yVals = new ArrayList<>();

        for (int m = 0; m < members.size(); m++) {
            yVals.add(new ArrayList<BarEntry>());
        }

        float startX = 0;
        for (ChoreItem chore : chores) {
            for (int m = 0; m < members.size(); m++) {
                MemberItem member = members.get(m);

                int barValue = 0;
                Map<String, Integer> choresCountMap = membersChoresCountMap.get(member.getName());
                if (choresCountMap != null) {
                    Integer count = choresCountMap.get(chore.getName());
                    if (count != null) {
                        barValue = count;
                    }
                }

                BarEntry barEntry = new BarEntry(startX, barValue, chore.getName());
                List<BarEntry> barEntries = yVals.get(m);
                barEntries.add(barEntry);
                startX += spaceForBar;
            }
            //startX += spaceForBar * members.size();
        }


        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        int colorIdx = 0;
        for (int i = 0; i < yVals.size(); i++) {
            List<BarEntry> entries = yVals.get(i);
            BarDataSet dataSet = new BarDataSet(entries, members.get(i).getName());
            dataSet.setDrawValues(false);
            dataSet.setColor(colors.get(colorIdx));
            colorIdx++;
            if (colorIdx == colors.size()) {
                colorIdx = 0;
            }

            dataSets.add(dataSet);
        }

        BarData data = new BarData(dataSets);
        //data.setValueTextSize(10f);
        data.setBarWidth(barWidth);
        membersChoresBarChart.setData(data);

        membersChoresBarChart.highlightValue(null);
    }

    public class LabelValueFormatter implements IAxisValueFormatter {
        private final IBarDataSet data;
        private String lastLabel;

        public LabelValueFormatter(IBarDataSet pData) {
            data = pData;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // return the entry's data which represents the label
            BarEntry entryForXValue = data.getEntryForXValue(value, Float.NaN, DataSet.Rounding.CLOSEST);
            String formattedValue = (String) entryForXValue.getData();
            if (TextUtils.equals(lastLabel, formattedValue)) {
                formattedValue = "";
            }
            lastLabel = formattedValue;
            return formattedValue;
        }
    }
    */
}
