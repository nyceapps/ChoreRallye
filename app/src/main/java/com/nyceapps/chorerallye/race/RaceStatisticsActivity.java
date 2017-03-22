
package com.nyceapps.chorerallye.race;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
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
    private HorizontalBarChart membersChoresBarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_statistics);

        data = ((RallyeApplication) this.getApplication()).getRallyeData();

        initColors();

        initChoresPieChart();

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

        initChoresPieChartData();

        Legend choresPieChartlegend = choresPieChart.getLegend();
        choresPieChartlegend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        choresPieChartlegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        choresPieChartlegend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        choresPieChartlegend.setWordWrapEnabled(true);
        choresPieChartlegend.setDrawInside(true);
        choresPieChartlegend.setEnabled(true);
    }

    private void initChoresPieChartData() {
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
        choresPieChart.setData(data);

        choresPieChart.highlightValues(null);
    }

    private void initMembersChoresBarChart() {
        membersChoresBarChart = (HorizontalBarChart) findViewById(R.id.members_chores_bar_chart);

        membersChoresBarChart.setTouchEnabled(false);
        membersChoresBarChart.setDrawBarShadow(false);
        membersChoresBarChart.setDrawValueAboveBar(true);
        membersChoresBarChart.getDescription().setEnabled(false);
        membersChoresBarChart.setPinchZoom(false);
        membersChoresBarChart.setDrawGridBackground(false);

        List<String> axisLabelsList = new ArrayList<>();
        List<ChoreItem> chores = data.getChores();
        for (ChoreItem chore : chores) {
            axisLabelsList.add(chore.getName());
        }
        String[] axisLabels = axisLabelsList.toArray(new String[axisLabelsList.size()]);

        XAxis xAxis = membersChoresBarChart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setLabelCount(100, true);
        xAxis.setValueFormatter(new ChoresXAxisValueFormatter());
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

        initMembersChoresBarChartData();

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

                BarEntry barEntry = new BarEntry(startX, barValue);
                List<BarEntry> barEntries = yVals.get(m);
                barEntries.add(barEntry);
                startX += barWidth;
            }
            startX += (spaceForBar - barWidth) * members.size();
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
        data.setValueTextSize(10f);
        data.setBarWidth(barWidth);
        membersChoresBarChart.setData(data);

        membersChoresBarChart.highlightValue(null);
    }

    public class ChoresXAxisValueFormatter extends IndexAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return String.valueOf(value);
            /*
            int intValue = (int) (value / 10);
            List<MemberItem> members = data.getMembers();
            List<ChoreItem> chores = data.getChores();
            if (intValue < chores.size() && intValue % members.size() == 0) {
                ChoreItem chore = chores.get(intValue);
                return chore.getName();
            }

            return "";
            */
        }
    }
}
