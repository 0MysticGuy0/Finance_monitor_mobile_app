package com.mygy.tanyafinances;

import static com.google.android.material.internal.ContextUtils.getActivity;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class User implements Serializable {
    private String email;
    private String passwd;
    private String docId;
    private final ArrayList<Category> categories;//список всех категорий
    private final ArrayList<Operation> allOperations;
    private final ArrayList<Double> constraints;
    private Date lastDataAdded;
    private double balance;
    private transient TextView balanceText;
    private transient TextView lastAddedText;
    public transient static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY");
    private transient AppCompatActivity activity;
    private static final String FILE_NAME = "user.dat";
    private transient HashMap<String,Object> userDocument = new HashMap<>();

    public static class Constants{
        public final static String EMAIL = "email";
        public final static String PASSWORD = "passwd";
        public final static String DOC_ID = "doc_id";
        public final static String BALANCE = "balance";
        public final static String LAST_ADDED = "last_Added";
        public final static String CATEGORIES = "categories";
        public final static String OPERATION_HISTORY = "operation_history";
        public final static String CONSTRAINTS = "constraints";
        public final static String USER_TABLE_NAME = "users";
    }

    public User() {
        this.activity = activity;
        categories = Category.getAllCategories();
        new Category("Перевод", "Перевод денежных средств.", R.drawable.baseline_compare_arrows_24);
        new Category("Продукты питания", "", R.drawable.baseline_fastfood_24);
        new Category("Транспорт", "", R.drawable.baseline_emoji_transportation_24);
        new Category("Зарплата", "", R.drawable.baseline_currency_ruble_24);
        new Category("Спортивные клубы", "", R.drawable.baseline_sports_24);
        allOperations = new ArrayList<>();
        constraints = new ArrayList<>();
        lastDataAdded = new Date();
        balance = 0.0;

        userDocument.put(Constants.EMAIL,email);
        userDocument.put(Constants.PASSWORD,passwd);
        userDocument.put(Constants.BALANCE,balance);
        userDocument.put(Constants.LAST_ADDED,lastDataAdded);
        userDocument.put(Constants.CATEGORIES,categories);
        userDocument.put(Constants.OPERATION_HISTORY,allOperations);
        userDocument.put(Constants.CONSTRAINTS,constraints);
        userDocument.put(Constants.DOC_ID,docId);

    }

    public User(String email, String passwd, ArrayList<Operation> allOperations, ArrayList<Double> constraints, Date lastDataAdded, double balance) {
        this.email = email;
        this.passwd = passwd;
        this.allOperations = allOperations;
        this.constraints = constraints;
        this.categories = Category.getAllCategories();
        this.lastDataAdded = lastDataAdded;
        this.balance = balance;

        userDocument.put(Constants.EMAIL,email);
        userDocument.put(Constants.PASSWORD,passwd);
        userDocument.put(Constants.BALANCE,balance);
        userDocument.put(Constants.LAST_ADDED,lastDataAdded);
        userDocument.put(Constants.CATEGORIES,categories);
        userDocument.put(Constants.OPERATION_HISTORY,allOperations);
        userDocument.put(Constants.CONSTRAINTS,constraints);
        userDocument.put(Constants.DOC_ID,docId);
    }

    public void setEmail(String email) {
        this.email = email;
        userDocument.put(Constants.EMAIL,email);
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
        userDocument.put(Constants.PASSWORD,passwd);
    }

    public void setBalance(double balance) {
        this.balance = balance;
        userDocument.put(Constants.BALANCE,balance);
    }

    public void setLastDataAdded(Date lastDataAdded) {
        this.lastDataAdded = lastDataAdded;
        userDocument.put(Constants.LAST_ADDED,lastDataAdded);
    }

    public void setBalanceText(TextView balanceText) {
        this.balanceText = balanceText;
        this.balanceText.setText(Double.toString(balance));
    }
    public void removeCategory(Category category){
        categories.remove(category);
        userDocument.put(Constants.CATEGORIES,categories);
        MainActivity.saveUser();
    }

    public String getEmail() {
        return email;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
        userDocument.put(Constants.DOC_ID,docId);
    }

    public void setLastAddedText(TextView lastAddedText) {
        this.lastAddedText = lastAddedText;
        userDocument.put(Constants.LAST_ADDED,lastDataAdded);
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public Date getLastDataAdded() {
        return lastDataAdded;
    }

    public double getBalance() {
        return balance;
    }

    public HashMap<String, Object> getUserDocument() {
        return userDocument;
    }

    private void addMoney(double money) {
        balance += money;
        for (Double c : constraints) {
            if(money <= c){
                Toast.makeText(activity,"ВАШ БАЛАНС МЕНЬШЕ, ЧЕМ "+c+"!!!",Toast.LENGTH_LONG).show();
                constraints.remove(c);
                userDocument.put(Constants.CONSTRAINTS,constraints);
                break;
            }
        }
        userDocument.put(Constants.BALANCE,balance);
    }

    public void addOperation(Operation operation) {
        allOperations.add(operation);
        addMoney(operation.getDeltaMoney());
        lastDataAdded = new Date();

        balanceText.setText(Double.toString(balance));
        lastAddedText.setText(dateFormat.format(getLastDataAdded()));
        Collections.sort(allOperations, (o1, o2) -> {
            return (((Operation) o1).getDate().before(((Operation) o2).getDate())) ? 1 : -1;
        });
        userDocument.put(Constants.OPERATION_HISTORY,allOperations);
        MainActivity.saveUser();
    }
    public void addConstraint(Double value){
        constraints.add(value);
        MainActivity.saveUser();
        userDocument.put(Constants.CONSTRAINTS,constraints);
    }

    public ArrayList<Operation> getAllOperations() {
        return allOperations;
    }
    public void saveToFile(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME));
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            ois.readObject();
            oos.writeObject(this);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    public static User readFromFile(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (User) ois.readObject();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
