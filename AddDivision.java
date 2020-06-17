package my.app.bankguaranteemonitor;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddDivision extends AppCompatActivity {
    public EditText bgDiv;
    public Button submit;
    public Spinner nig;
    public String nigam;
    public String[] nigams = {"KNNL","CNNL","KBJNL","VJNL"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_division);

        bgDiv = (EditText)findViewById(R.id.editText5);
        nig = (Spinner) findViewById(R.id.spinner8);
        submit = (Button) findViewById(R.id.button14);

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,nigams);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nig.setAdapter(aa);

        nig.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nigam = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(),"Select Nigam",Toast.LENGTH_SHORT).show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }
    private void submitForm(){
        if(bgDiv.getText().toString().isEmpty()){
            bgDiv.setError("Required");
            bgDiv.requestFocus();
        }
        else{
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference divis = database.getReference();
            divis.child("Divisions").child(nigam).child(bgDiv.getText().toString()).setValue(bgDiv.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(),"Division Added Successfullty",Toast.LENGTH_LONG).show();
                    return;
                }
            });
        }
    }
}
