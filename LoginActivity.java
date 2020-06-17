package my.app.bankguaranteemonitor;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    public EditText username,pwd;
    public Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.editText1);
        pwd = (EditText) findViewById(R.id.editText2);
        login = (Button) findViewById(R.id.button3);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveNetwork()) {
                    submitForm();
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
    private void submitForm() {
        final String uname = username.getText().toString().trim();
        final String password = pwd.getText().toString().trim();

        if(uname.isEmpty()) {
            username.setError("Username Required");
            username.requestFocus();
        }
        if(password.isEmpty()) {
            pwd.setError("Password Required");
            pwd.requestFocus();
        }
        if(!uname.isEmpty() && !password.isEmpty()) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference data = database.getReference();
            data.child("Users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int count = 0;
                    for(DataSnapshot user: dataSnapshot.getChildren()){
                        System.out.println(user.child("Name").getValue().toString());
                        System.out.println(uname);
                        if(user.child("Name").getValue().toString().equals(uname)){
                            count = 1;
                            if(user.child("Password").getValue().toString().equals(password)){
                                Toast.makeText(getApplicationContext(),"Login Successfull!",Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(LoginActivity.this,ChooseActivity.class);
                                i.putExtra("Name",user.getKey());
                                startActivity(i);
                                break;
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Invalid Password",Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }
                    if(count == 0){
                        Toast.makeText(getApplicationContext(),"Invalid Username",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),"Check your internet connection and try again!",Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    private boolean haveNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
