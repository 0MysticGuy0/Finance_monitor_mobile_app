package com.mygy.tanyafinances;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    public static User user;
    private  static String curUserId;
    private static String email;
    private static FirebaseFirestore usersBase;
    private static FirebaseUser curUsr;

    static{
        FirebaseAuth auth =FirebaseAuth.getInstance();
        curUsr = auth.getCurrentUser();
        curUserId = auth.getCurrentUser().getUid();
        email = auth.getCurrentUser().getEmail();
        usersBase = FirebaseFirestore.getInstance();
    }

    public static void saveUser(){
        if(user != null) {
            usersBase.collection(User.Constants.USER_TABLE_NAME)
                    .document(user.getDocId())
                    .update(user.getUserDocument())
                    .addOnSuccessListener( docRef -> {
                        System.out.println("----------Сохранил пользователя"+user.getEmail()+"-----------");

                    })
                    .addOnFailureListener(ex -> {
                        System.out.println("----------Ошибка сохранения"+user.getEmail()+"-----------");
                    });
            //user.saveToFile();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user.setActivity(this);

        replaceFragment(new BlankFragment(user));

        BottomNavigationView bnv=findViewById(R.id.main_bottomNav);
        bnv.setOnItemSelectedListener(item ->{
            if(item.getItemId()==R.id.mainNavBtn) {
                BlankFragment fragment = new BlankFragment(user);
                replaceFragment(fragment);
            }
            else if(item.getItemId()==R.id.historyNavBtn)
                replaceFragment(new HistoryFragment());
            else
                replaceFragment(new ChartFragment());
            return true;
        });
    }

    private void replaceFragment(Fragment newFragment){
        FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragmentContainerView,newFragment);
        ft.commit();
    }
}