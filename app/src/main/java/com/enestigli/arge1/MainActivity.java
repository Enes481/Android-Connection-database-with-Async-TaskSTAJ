package com.enestigli.arge1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.enestigli.arge1.databinding.ActivityMainBinding;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    String host_name;
    String db;
    String db_user_name;
    String db_passw;
    Connection con ;


    public void init() {
        host_name = "jdbc:sqlserver://localhost\\DESKTOP-4GBOEMR\\MSSQLSERVER02;user=Enes;password=";

        db = "Personel";
        db_user_name = "Deneme";
        db_passw = "";

        btn_register_click();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.progressBar.setVisibility(View.GONE);
        init();

    }


    public void btn_register_click() {
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == binding.btnRegister.getId()) {
                    AccessControl accessControl = new AccessControl();
                    accessControl.execute("");

                }
            }
        });
    }


    class AccessControl extends AsyncTask<String, String, String> {

        String message = "";
        boolean successfull_flag = false;
        String user_name;
        String password;

        @Override
        protected void onPreExecute() {                         //that will appear before background processes

            binding.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {            //that will appear after background processes
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();

            if (successfull_flag) {
                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_LONG).show();

            }
        }


        @Override
        protected String doInBackground(String... strings) {        //that will during background processes
            user_name = binding.txtUserName.getText().toString();
            password = binding.txtPassword.getText().toString();

            if (user_name.trim().equals("") || password.trim().equals("")) {
                message = " Enter user name and password";
            }


            else {
                con = connection(host_name, db_user_name, db, db_passw); //host_name -->"DESKTOP-4GBOEMR" , db_user_name --> Deneme , db-->Personel, db_passw --> 


                try {
                   /*if (con == null) {
                        message = "check your connection.";
                    }*/ //else {
                        String query = "select * from calisanlar where Ad='" + user_name + "' and PersonelNo='" + password + "'";
                        Statement statement = con.createStatement();
                        ResultSet rs = statement.executeQuery(query);

                        if (rs.next()) {
                            message = " Login successful.";
                            successfull_flag = true;
                            con.close();

                            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                            startActivity(intent);

                        } else {
                            message = "User not found";
                            successfull_flag = false;
                        }
                    //}
                } catch (SQLException throwables) {
                    successfull_flag = false;
                    message = throwables.getMessage();
                }

            }
            return message;
           }

        }


        @SuppressLint("NewApi")
        public Connection connection(String host_name, String db_user_name, String db, String db_passw) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Connection conn = null;
            String ConnectionURL=null;

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                ConnectionURL = "jdbc:jtds:sqlserver://" + host_name + ";" + "database" + db + ";user=" + db_user_name + ";password=" + db_passw + ";";
                conn = DriverManager.getConnection(ConnectionURL);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            return conn;
        }
    }

