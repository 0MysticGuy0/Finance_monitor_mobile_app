package com.mygy.tanyafinances;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChartFragment extends Fragment {
    private final Date[] selectedDates = new Date[2];
    private AnyChartView chart;
    private Pie pie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        chart = view.findViewById(R.id.chart_chart);
        pie = AnyChart.pie();
        pie.title("Популярность категорий");
        setUpChartView();
        chart.setChart(pie);

        Button startDateBtn = view.findViewById(R.id.chart_startDateBtn);
        Button endDateBtn = view.findViewById(R.id.chart_endDateBtn);

        startDateBtn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    selectedDates[0] = new Date(year-1900,month,dayOfMonth);
                    startDateBtn.setText(dayOfMonth+"/"+month+"/"+year);

                    setUpChartView();
                }
            },year,month,day);
            dpd.show();
        });
        endDateBtn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    selectedDates[1] = new Date(year-1900,month,dayOfMonth);
                    endDateBtn.setText(dayOfMonth+"/"+month+"/"+year);

                    setUpChartView();
                }
            },year,month,day);
            dpd.show();
        });

        return view;
    }

    private void setUpChartView(){
        List<DataEntry> dataEntries = new ArrayList<>();
        List<Operation> operations = Operation.getOperationsInDates(selectedDates[0],selectedDates[1]);
        HashMap<Category,Integer> operationsCount= new HashMap<>();
        for(Operation operation : operations){
            if(operationsCount.containsKey(operation.getCategory())){
                operationsCount.put(operation.getCategory(),operationsCount.get(operation.getCategory())+1);
            }
            else{
                operationsCount.put(operation.getCategory(),1);
            }
        }

        for(Map.Entry<Category,Integer> val: operationsCount.entrySet()){
            dataEntries.add(new ValueDataEntry(val.getKey().getName(), val.getValue()));
        }
        if(dataEntries.size()==0)
            dataEntries.add(new ValueDataEntry("нет операций",1));
        pie.data(dataEntries);

    }
}