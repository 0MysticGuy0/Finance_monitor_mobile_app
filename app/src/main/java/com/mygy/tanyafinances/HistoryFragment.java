package com.mygy.tanyafinances;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryFragment extends Fragment {
    private List<Operation> history;
    private HistoryRecyclerAdapter adapter;
    private final Date[] selectedDates = new Date[2];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        history = MainActivity.user.getAllOperations();

        RecyclerView recycler = view.findViewById(R.id.history_recycler);
        adapter = new HistoryRecyclerAdapter(getActivity(),history);
        recycler.setAdapter(adapter);

        Button startDateBtn = view.findViewById(R.id.hostory_startDateBtn);
        Button endDateBtn = view.findViewById(R.id.hostory_endDateBtn);

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

                    adapter.setOperations( Operation.getOperationsInDates(selectedDates[0],selectedDates[1]) );
                    System.out.println("Ffffffff"+history.size());
                    adapter.notifyDataSetChanged();
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

                    adapter.setOperations( Operation.getOperationsInDates(selectedDates[0],selectedDates[1]) );
                    System.out.println("Ffffffff"+history.size());
                    adapter.notifyDataSetChanged();
                }
            },year,month,day);
            dpd.show();
        });

        return view;
    }

}