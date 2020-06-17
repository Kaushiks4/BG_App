package my.app.bankguaranteemonitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class InitForward extends AppCompatActivity {
    TextView bgdiv,amt,name_of_work;
    EditText remarks;
    int id;
    Spinner toname;
    ArrayList<String> names;
    String name,forwardname,division,amount,nameOfWork,d;
    Button forward;
    DatabaseReference data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_forward);
        Intent i = getIntent();
        name = i.getStringExtra("Name");

        id = Integer.parseInt(i.getStringExtra("id"));
        division = i.getStringExtra("Div");
        amount = i.getStringExtra("amt");
        nameOfWork = i.getStringExtra("nameofwork");

        bgdiv = (TextView) findViewById(R.id.textView14);
        bgdiv.setText(division);
        amt = (TextView) findViewById(R.id.textView23);
        amt.setText(amount);
        name_of_work = (TextView) findViewById(R.id.textView26);
        name_of_work.setText(nameOfWork);
        remarks = (EditText) findViewById(R.id.editText13);
        toname = (Spinner) findViewById(R.id.spinner4);

        forward = (Button) findViewById(R.id.button12);
        data = FirebaseDatabase.getInstance().getReference();

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH:mm");
        Date date = new Date();
        d = formatter.format(date);

        data.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                names = new ArrayList<>();
                int i = 0;
                for(DataSnapshot keys: dataSnapshot.getChildren()) {
                    names.add(keys.child("Name").getValue().toString());
                    if(keys.getKey().equals(name)){
                        i = names.indexOf(keys.getKey());
                    }
                }
                names.remove(i);
                ArrayAdapter aa = new ArrayAdapter(InitForward.this, android.R.layout.simple_spinner_item, names);
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                toname.setAdapter(aa);

                toname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        forwardname = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }
    public void submitForm(){
        if(remarks.getText().toString().isEmpty()){
            remarks.setError("Required");
            remarks.requestFocus();
        }
        if(!remarks.getText().toString().isEmpty()){
            Forward forward = new Forward(id,name,forwardname,remarks.getText().toString(),d);
            data.child("Forward").child("Initiates").push().setValue(forward);
            data.child("Initiations").orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String key="";
                    for(DataSnapshot k: dataSnapshot.getChildren()){
                        key = k.getKey();
                        break;
                    }
                    data.child("Initiations").child(key).child("Notify").setValue("1");
                    Toast.makeText(getApplicationContext(),"Forwarded Successfully",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(InitForward.this,Init.class);
                    i.putExtra("Name",name);
                    startActivity(i);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
