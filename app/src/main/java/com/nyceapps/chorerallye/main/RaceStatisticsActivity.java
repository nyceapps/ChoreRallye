
package com.nyceapps.chorerallye.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nyceapps.chorerallye.R;
import com.nyceapps.chorerallye.member.MemberItem;
import com.nyceapps.chorerallye.race.RaceItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        choresPieChartlegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        choresPieChartlegend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        choresPieChartlegend.setWordWrapEnabled(true);
        choresPieChartlegend.setDrawInside(true);
        choresPieChartlegend.setEnabled(true);
    }

    private void initChoresPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        List<RaceItem> raceItems = data.getRace().getRaceItems();
        Map<String, Integer> choresCountMap = new HashMap<>();
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

        Legend membersChoresBarChartLegend = membersChoresBarChart.getLegend();
        membersChoresBarChartLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        membersChoresBarChartLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        membersChoresBarChartLegend.setOrientation(Legend.LegendOrientation.VERTICAL);
        membersChoresBarChartLegend.setDrawInside(true);
        membersChoresBarChartLegend.setYOffset(0f);
        membersChoresBarChartLegend.setXOffset(10f);
        membersChoresBarChartLegend.setYEntrySpace(0f);
        membersChoresBarChartLegend.setTextSize(8f);

        List<MemberItem> members = data.getMembers();
        List<RaceItem> raceItems = data.getRace().getRaceItems();
    }
}
