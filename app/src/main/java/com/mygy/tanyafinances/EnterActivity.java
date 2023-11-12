package com.mygy.tanyafinances;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class EnterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore usersBase;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        auth = FirebaseAuth.getInstance();
        usersBase = FirebaseFirestore.getInstance();

        Button loginBtn = findViewById(R.id.enter_loginBtn);
        Button registerBtn = findViewById(R.id.enter_registerBtn);

        loginBtn.setOnClickListener(v -> {
            showLoginWindow();
        });

        registerBtn.setOnClickListener(v -> {
            showRegisterWindow();
        });
    }

    private void showLoginWindow(){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
        final View loginView = getLayoutInflater().inflate(R.layout.login_window,null);

        EditText emailET = loginView.findViewById(R.id.login_email);
        EditText passwdET = loginView.findViewById(R.id.login_password);

        ImageButton cancel = loginView.findViewById(R.id.login_cancelBtn);
        ImageButton add = loginView.findViewById(R.id.login_loginBtn);

        a_builder.setView(loginView);
        AlertDialog dialog = a_builder.create();
        dialog.show();

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        add.setOnClickListener(v -> {
            try{
                String email = emailET.getText().toString();
                String passwd = passwdET.getText().toString();

                if(email == null || email.length()==0 || passwd == null || passwd.length() == 0) throw new RuntimeException();

                auth.signInWithEmailAndPassword(email,passwd)
                                .addOnSuccessListener(res -> {

                                    usersBase.collection(User.Constants.USER_TABLE_NAME)
                                            .whereEqualTo(User.Constants.EMAIL,email)
                                            .get()
                                            .addOnCompleteListener( task -> {
                                                if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
                                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                                    try {
                                                        for(HashMap<String,Object> c:(ArrayList<HashMap<String,Object>>)documentSnapshot.get(User.Constants.CATEGORIES)){
                                                            new Category(c);
                                                        }
                                                        System.out.println((ArrayList<Double>) documentSnapshot.get(User.Constants.CONSTRAINTS));
                                                        double b;
                                                        try {
                                                            b = documentSnapshot.getDouble(User.Constants.BALANCE);
                                                        }catch (NullPointerException ex){
                                                            b = 0.0;
                                                        }
                                                        ArrayList<Operation> history = new ArrayList<>();

                                                        ArrayList<HashMap<String,Object>> oh = (ArrayList<HashMap<String,Object>>) documentSnapshot.get(User.Constants.OPERATION_HISTORY);
                                                        System.out.println("ssssssssssss"+documentSnapshot.get(User.Constants.OPERATION_HISTORY));
                                                        if(oh != null){
                                                            for(HashMap<String,Object> o:oh){
                                                                history.add(new Operation(o));
                                                            }
                                                        }

                                                        user = new User(documentSnapshot.getString(User.Constants.EMAIL),
                                                                documentSnapshot.getString(User.Constants.PASSWORD),
                                                                history,
                                                                (ArrayList<Double>) documentSnapshot.get(User.Constants.CONSTRAINTS),
                                                                documentSnapshot.getDate(User.Constants.LAST_ADDED),
                                                                b );

                                                        if(user.getDocId() == null){
                                                            user.setDocId(documentSnapshot.getId());
                                                        }
                                                        MainActivity.user = user;
                                                        startActivity(new Intent(EnterActivity.this,MainActivity.class));
                                                        finish();
                                                    }
                                                    catch (NullPointerException ex){
                                                        ex.printStackTrace();
                                                        System.out.println("errrrr"+ex.getMessage());
                                                        throw new RuntimeException();
                                                    }

                                                }
                                            }).addOnFailureListener( ex -> {
                                                throw new RuntimeException();
                                                //startActivity(new Intent(EnterActivity.this,MainActivity.class));
                                                //finish();
                                            });
                                })
                        .addOnFailureListener(ex -> {
                            Toast.makeText(getApplicationContext(),"Ошибка авторизаци! "+ex.getMessage(),Toast.LENGTH_SHORT).show();
                        });
            }
            catch(RuntimeException ex){
                Toast.makeText(getApplicationContext(),"Введите значение!!!",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showRegisterWindow(){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(this);
        final View regestrationView = getLayoutInflater().inflate(R.layout.register_window,null);

        EditText emailET = regestrationView.findViewById(R.id.registration_email);
        EditText passwdET = regestrationView.findViewById(R.id.registration_password);

        ImageButton cancel = regestrationView.findViewById(R.id.registration_cancelBtn);
        ImageButton add = regestrationView.findViewById(R.id.registration_regBtn);

        a_builder.setView(regestrationView);
        AlertDialog dialog = a_builder.create();
        dialog.show();

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        add.setOnClickListener(v -> {
            try{
                String email = emailET.getText().toString();
                String passwd = passwdET.getText().toString();

                if(email == null || email.length()==0 || passwd == null || passwd.length() == 0) throw new RuntimeException();
                if(passwd.length() < 6) throw new IllegalArgumentException();

                auth.createUserWithEmailAndPassword(email,passwd)
                                .addOnSuccessListener(res -> {
                                    User user = new User();
                                    user.setActivity(this);
                                    user.setEmail(email);
                                    user.setPasswd(passwd);

                                    MainActivity.user=user;

                                    usersBase.collection(User.Constants.USER_TABLE_NAME).add(user.getUserDocument())
                                            .addOnSuccessListener( docRef -> {
                                                user.setDocId(docRef.getId());
                                                Toast.makeText(this,"Успешно зарегистрирован",Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(EnterActivity.this,MainActivity.class));
                                                finish();
                                            })
                                            .addOnFailureListener(ex -> {
                                                Toast.makeText(this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                                            });
                                    dialog.cancel();
                                });

                //dialog.cancel();
            }
            catch (IllegalArgumentException ex){
                Toast.makeText(this,"Минимальная длина пароля - 6 символов!!!",Toast.LENGTH_SHORT).show();
            }
            catch(RuntimeException ex){
                Toast.makeText(this,"Введите значение!!!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}