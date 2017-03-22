
package com.nyceapps.chorerallye.race;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nyceapps.chorerallye.R;
import com.nyceapps.chorerallye.chore.ChoreItem;
import com.nyceapps.chorerallye.main.RallyeApplication;
import com.nyceapps.chorerallye.main.RallyeData;
import com.nyceapps.chorerallye.main.Utils;
import com.nyceapps.chorerallye.member.MemberItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nyceapps.chorerallye.main.Constants.DISPLAY_MODE_LOG;
import static com.nyceapps.chorerallye.main.Constants.DISPLAY_MODE_RALLYE;

public class RaceStatisticsActivity extends AppCompatActivity {
    private RallyeData data;

    private PieChart choresPieChart;
    private BarChart membersChoresBarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_statistics);

        data = ((RallyeApplication) this.getApplication()).getRallyeData();

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

    private void initChoresPieChart() {
        choresPieChart = (PieChart) findViewById(R.id.chores_pie_chart);

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

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

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

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.DKGRAY);
        choresPieChart.setData(data);

        choresPieChart.highlightValues(null);

        choresPieChart.invalidate();
    }

    private void initMembersChoresBarChart() {
        membersChoresBarChart = (BarChart) findViewById(R.id.members_chores_bar_chart);

        membersChoresBarChart.getDescription().setEnabled(false);
        membersChoresBarChart.setPinchZoom(false);
        membersChoresBarChart.setDrawBarShadow(false);
        membersChoresBarChart.setDrawGridBackground(false);
        membersChoresBarChart.setDrawValueAboveBar(false);
        membersChoresBarChart.setFitBars(true);

        initMembersChoresBarChartData();

        Legend membersChoresBarChartLegend = membersChoresBarChart.getLegend();
        membersChoresBarChartLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        membersChoresBarChartLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        membersChoresBarChartLegend.setOrientation(Legend.LegendOrientation.VERTICAL);
        membersChoresBarChartLegend.setDrawInside(true);
        membersChoresBarChartLegend.setYOffset(0f);
        membersChoresBarChartLegend.setXOffset(10f);
        membersChoresBarChartLegend.setYEntrySpace(0f);
        membersChoresBarChartLegend.setTextSize(8f);
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

        List<IBarDataSet> dataSets = new ArrayList<>();

        int barIdx = 0;
        List<MemberItem> members = data.getMembers();
        List<ChoreItem> chores = data.getChores();
        for (ChoreItem chore : chores) {
            List<BarEntry> yVals = new ArrayList<>();
            for (MemberItem member : members) {
                int barValue = 0;
                Map<String, Integer> choresCountMap = membersChoresCountMap.get(member.getName());
                if (choresCountMap != null) {
                    Integer count = choresCountMap.get(chore.getName());
                    if (count != null) {
                        barValue = count;
                    }
                }
                yVals.add(new BarEntry(barIdx, barValue));
                barIdx++;
            }

            BarDataSet dataSet = new BarDataSet(yVals, chore.getName());
            dataSets.add(dataSet);
        }

        BarData data = new BarData(dataSets);
        membersChoresBarChart.setData(data);

        /*
        // specify the width each bar should have
        membersChoresBarChart.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        membersChoresBarChart.getXAxis().setAxisMinimum(startYear);

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        membersChoresBarChart.getXAxis().setAxisMaximum(startYear + mChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        membersChoresBarChart.groupBars(startYear, groupSpace, barSpace);
        membersChoresBarChart.invalidate();
        */
    }
}
