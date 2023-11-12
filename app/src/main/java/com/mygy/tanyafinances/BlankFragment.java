package com.mygy.tanyafinances;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class BlankFragment extends Fragment {

    private User user;
    private View view;
    private CategoryRecyclerAdapter categoriesAdaper;


    public BlankFragment(User user) {
        this.user = user;
    }
    public BlankFragment() {
        this.user = MainActivity.user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_blank, container, false);

        if(user!=null) {
            user.setBalanceText(view.findViewById(R.id.balance));
            TextView lastAdded = view.findViewById(R.id.balance_lastAddDate);
            user.setLastAddedText(lastAdded);
            if (user.getLastDataAdded() != null)
                lastAdded.setText(User.dateFormat.format(user.getLastDataAdded()));

            categoriesAdaper = new CategoryRecyclerAdapter(getActivity(), user.getCategories());
            RecyclerView categoriesRecycler = view.findViewById(R.id.categoriesRecycler);
            categoriesRecycler.setAdapter(categoriesAdaper);
        }
        ImageButton addOperation = view.findViewById(R.id.addInfoBtn);
        addOperation.setOnClickListener(v -> {
            createPopupAddOperation();
        });

        ImageButton addConstraint = view.findViewById(R.id.addConstraintBtn);
        addConstraint.setOnClickListener(v -> {
            createPopupAddCConstraint();
        });


        ImageButton addCategory = view.findViewById(R.id.addCategoryBtn);
        addCategory.setOnClickListener(v -> {
            createPopupAddCategory();
            categoriesAdaper.notifyDataSetChanged();
        });

        return view;
    }

    public void createPopupAddCConstraint(){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(getActivity());
        final View addConstraintView = getLayoutInflater().inflate(R.layout.add_constraint,null);

        EditText value = addConstraintView.findViewById(R.id.addConstraint_value);

        ImageButton cancel = addConstraintView.findViewById(R.id.addConstraint_cancelBtn);
        ImageButton add = addConstraintView.findViewById(R.id.addConstraint_addBtn);

        a_builder.setView(addConstraintView);
        AlertDialog dialog = a_builder.create();
        dialog.show();

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        add.setOnClickListener(v -> {
            try{
                String val = value.getText().toString();
                if(value == null || value.length() == 0) throw new RuntimeException("Введите значение!!!");
                Double dv = Double.parseDouble(val);
                MainActivity.user.addConstraint(dv);
                Toast.makeText(getActivity(),"Создано уведомление при балансе <= "+dv,Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }catch (NumberFormatException ex){
                Toast.makeText(getActivity(),"Введите число!!!",Toast.LENGTH_SHORT).show();
            }
            catch(RuntimeException ex){
                Toast.makeText(getActivity(),ex.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void createPopupAddCategory(){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(getActivity());
        final View addCategoryView = getLayoutInflater().inflate(R.layout.add_category,null);

        EditText nameET = addCategoryView.findViewById(R.id.addCategory_name);
        EditText descriptionET = addCategoryView.findViewById(R.id.addCategory_description);

        ImageButton cancel = addCategoryView.findViewById(R.id.addCategory_cancelBtn);
        ImageButton add = addCategoryView.findViewById(R.id.addCategory_addBtn);

        a_builder.setView(addCategoryView);
        AlertDialog dialog = a_builder.create();
        dialog.show();

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        add.setOnClickListener(v -> {
            try{
                String name = nameET.getText().toString();
                if(name == null || name.length() == 0) throw new RuntimeException("Введите имя категроии!!!");
                new Category(name,descriptionET.getText().toString(),R.drawable.baseline_edit_24);

                dialog.cancel();
            }catch(RuntimeException ex){
                Toast.makeText(getActivity(),ex.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void createPopupAddOperation(){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(getActivity());
        final View addOperationView = getLayoutInflater().inflate(R.layout.add_operation,null);

        EditText sumET = addOperationView.findViewById(R.id.addOperation_sum);
        EditText description = addOperationView.findViewById(R.id.addOperation_description);

        final Category[] selectedCategory = new Category[1];
        Spinner category = addOperationView.findViewById(R.id.addOperation_categorySpinner);
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,Category.getAllCategoriesNames());
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoriesAdapter);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory[0] = Category.getCategoryByName((String) parent.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        final Date[] selectedDate = new Date[1];
        Button dateBtn = addOperationView.findViewById(R.id.addOperation_dateBtn);
        dateBtn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    selectedDate[0] = new Date(year-1900,month,dayOfMonth);
                    dateBtn.setText(dayOfMonth+"/"+month+"/"+year);
                }
            },year,month,day);
            dpd.show();
        });

        ImageButton cancel = addOperationView.findViewById(R.id.addOperation_cancelBtn);
        ImageButton add = addOperationView.findViewById(R.id.addOperation_addBtn);

        a_builder.setView(addOperationView);
        AlertDialog dialog = a_builder.create();
        dialog.show();

        cancel.setOnClickListener(v -> {
        dialog.dismiss();
        });

        add.setOnClickListener(v -> {
            try {
                double sum = Double.parseDouble(sumET.getText().toString());
                if(selectedCategory[0] == null || selectedDate[0] == null) throw new RuntimeException("Выберите дату и категорию!!!");
                String descrp = description.getText().toString();

                MainActivity.user.addOperation(new Operation(sum,selectedCategory[0],selectedDate[0],descrp));
                dialog.cancel();
            }catch (NumberFormatException ex){
                Toast.makeText(getActivity(),"Некорректный ввод!!!",Toast.LENGTH_SHORT).show();
            }catch(RuntimeException ex){
                Toast.makeText(getActivity(),ex.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}