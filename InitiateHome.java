package my.app.bankguaranteemonitor;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InitiateHome extends AppCompatActivity {
    EditText amount,remarks,nameOfWork;
    Spinner nigam,type,div;
    Button submit,addDiv;
    String bgNigam,bgType,division,name;
    String[] nigams = {"CNNL","KNNL","KBJNL","VJNL"};
    String[] types = {"FSD","EMD","APSD","Mobilization Advance"};
    String[] divisions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiate_home);

        Intent i = getIntent();
        name = i.getStringExtra("Name");

        amount = (EditText) findViewById(R.id.editText6);
        remarks = (EditText) findViewById(R.id.editText7);
        submit = (Button) findViewById(R.id.button4);
        addDiv = (Button) findViewById(R.id.button13);
        nameOfWork = (EditText) findViewById(R.id.editText4);

        div = (Spinner) findViewById(R.id.spinner7);
        nigam = (Spinner) findViewById(R.id.spinner);
        type = (Spinner) findViewById(R.id.spinner3);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference divs = database.getReference();

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,nigams);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nigam.setAdapter(aa);

        ArrayAdapter a = new ArrayAdapter(this,android.R.layout.simple_spinner_item,types);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(a);


        nigam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                bgNigam = parent.getItemAtPosition(position).toString();
                divs.child("Divisions").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long n = dataSnapshot.child(bgNigam).getChildrenCount();
                        divisions = new String[(int) n];
                        int i=0;
                        for(DataSnapshot keys:dataSnapshot.child(bgNigam).getChildren()){
                            divisions[i] = keys.getValue().toString();
                            i++;
                        }
                        ArrayAdapter d = new ArrayAdapter(InitiateHome.this,android.R.layout.simple_spinner_item,divisions);
                        d.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        div.setAdapter(d);

                        div.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                        {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                            {
                                division = parent.getItemAtPosition(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent)
                            {
                                // can leave this empty
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // can leave this empty
            }
        });

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bgType = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        addDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InitiateHome.this,AddDivision.class);
                startActivity(i);
                return;
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(haveNetwork()) {
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
        long amt = 0;
        amt = Integer.decode(amount.getText().toString()).longValue();
        final String rem = remarks.getText().toString().trim();
        if(amt == 0) {
            amount.setError("Amount Required!!");
            amount.requestFocus();
        }
        if(rem.isEmpty()) {
            remarks.setError("Remarks Required!!");
            remarks.requestFocus();
        }
        if(nameOfWork.getText().toString().isEmpty()){
            nameOfWork.setError("Required");
            nameOfWork.requestFocus();
        }
        if(amt!=0 && !rem.isEmpty() && !nameOfWork.getText().toString().isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
            Date date = new Date();
            final String init = dateFormat.format(date);
            final DatabaseReference result = FirebaseDatabase.getInstance().getReference();
            final long finalAmt = amt;
            result.child("Initiations").orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long id = 0;
                    for(DataSnapshot k: dataSnapshot.getChildren()){
                        id = Integer.decode(k.child("id").getValue().toString()).longValue();
                        break;
                    }
                    id -= 1;
                    addBG(finalAmt,init,rem,id,result);
                    return;
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    private void addBG(long finalAmt, String init, String rem, long id, DatabaseReference result){
        BG bg = new BG(finalAmt,bgNigam,division,init,bgType,rem,nameOfWork.getText().toString());
        result.child("Initiations").child(init).setValue(bg);
        result.child("Initiations").child(init).child("Initiated_by").setValue(name);
        result.child("Initiations").child(init).child("Notify").setValue(0);
        result.child("Initiations").child(init).child("id").setValue(id);
        Toast.makeText(getApplicationContext(),"Request Submitted Successfully!", Toast.LENGTH_LONG).show();
        Intent i = new Intent(InitiateHome.this,ChooseActivity.class);
        i.putExtra("Name",name);
        startActivity(i);
    }
    private boolean haveNetwork(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
