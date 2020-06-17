package my.app.bankguaranteemonitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ForwardActivity extends AppCompatActivity {
    private String name,forwardname,img,d;
    private TextView forwardto,bgnumid,bgnum;
    private EditText bgNum,remarks;
    private Spinner toname;
    private ArrayList<String> names;
    private DatabaseReference data;
    private StorageReference storage;
    private Button upload,change,forward,check;
    private ImageView imageView;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward);

        Intent i = getIntent();
        name = i.getStringExtra("Name");

        bgNum = (EditText) findViewById(R.id.editText9);
        remarks = (EditText) findViewById(R.id.editText18);
        remarks.setVisibility(View.GONE);
        toname = (Spinner) findViewById(R.id.spinner13);
        toname.setVisibility(View.GONE);
        forwardto = (TextView) findViewById(R.id.textView48);
        forwardto.setVisibility(View.GONE);
        upload = (Button) findViewById(R.id.button27);
        upload.setVisibility(View.GONE);
        change = (Button) findViewById(R.id.button28);
        change.setVisibility(View.GONE);
        forward = (Button) findViewById(R.id.button29);
        forward.setVisibility(View.GONE);
        imageView = (ImageView) findViewById(R.id.imageView3);
        imageView.setVisibility(View.GONE);
        check = (Button) findViewById(R.id.button25);
        bgnumid = (TextView) findViewById(R.id.textView49);
        bgnumid.setVisibility(View.GONE);
        bgnum = (TextView) findViewById(R.id.textView50);
        bgnum.setVisibility(View.GONE);

        data = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bgNum.getText().toString().isEmpty()) {
                    bgNum.setError("Required");
                    bgNum.requestFocus();
                } else {
                    data.child("Bank_Guarantees").child(bgNum.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                bgnumid.setVisibility(View.VISIBLE);
                                bgnum.setText(bgNum.getText().toString());
                                bgnum.setVisibility(View.VISIBLE);
                                check.setVisibility(View.GONE);
                                bgNum.setVisibility(View.GONE);
                                remarks.setVisibility(View.VISIBLE);
                                forwardto.setVisibility(View.VISIBLE);
                                toname.setVisibility(View.VISIBLE);
                                upload.setVisibility(View.VISIBLE);
                                forwardForm();
                                return;
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"BG Not found! Add BG.",Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
    private void forwardForm(){
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
                ArrayAdapter aa = new ArrayAdapter(ForwardActivity.this, android.R.layout.simple_spinner_item, names);
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

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH:mm");
        Date date = new Date();
        d = formatter.format(date);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void openFileChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "IMAGE"), 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading");
                progressDialog.show();
                final StorageReference image = storage.child(bgNum.getText().toString()+name+d+".jpg");
                image.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                upload.setVisibility(View.GONE);
                                imageView.setVisibility(View.VISIBLE);
                                change.setVisibility(View.VISIBLE);
                                forward.setVisibility(View.VISIBLE);
                                try{
                                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                    imageView.setImageBitmap(selectedImage);
                                } catch(FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        img = uri.toString();
                                    }
                                });
                                Toast.makeText(getApplicationContext(), "Image Uploaded ", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Check your Internet and Try Again", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                            }
                        });
            } else {
                Toast.makeText(getApplicationContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forward.setVisibility(View.GONE);
                submitForm();
            }
        });
    }

    private void submitForm() {
        if(bgNum.getText().toString().isEmpty()){
            bgNum.setError("Required");
            bgNum.requestFocus();
        }
        if(remarks.getText().toString().isEmpty()){
            remarks.setError("Required");
            remarks.requestFocus();
        }
        if(imageUri == null){
            Toast.makeText(getApplicationContext(),"Image not uploaded",Toast.LENGTH_LONG).show();
            return;
        }
        if(!bgNum.getText().toString().isEmpty() && !remarks.getText().toString().isEmpty() && imageUri != null) {
            data.child("Forward").child("BGs").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String last = "";
                    int flag = 0;
                    if(dataSnapshot.getValue() == null){
                        data.child("Bank_Guarantees").child(bgNum.getText().toString()).child("remarks").setValue(remarks.getText().toString());
                        Forward forward = new Forward(name,img,remarks.getText().toString(),forwardname,d,"0");
                        data.child("Forward").child("BGs").child(bgNum.getText().toString()).push().setValue(forward);
                        Toast.makeText(getApplicationContext(),"Forwarded Successfully",Toast.LENGTH_LONG).show();
                        recreate();
                    }
                    for(DataSnapshot key: dataSnapshot.getChildren()) {
                        if (bgNum.getText().toString().equals(key.getKey())) {
                            flag = 1;
                            for (DataSnapshot keys : key.getChildren()) {
                                last = keys.getKey();
                            }
                            data.child("Forward").child("BGs").child(bgNum.getText().toString()).child(last).child("Notify").setValue("1");
                            data.child("Bank_Guarantees").child(bgNum.getText().toString()).child("remarks").setValue(remarks.getText().toString());
                            Forward forward = new Forward(name, img, remarks.getText().toString(), forwardname, d,"0");
                            data.child("Forward").child("BGs").child(bgNum.getText().toString()).push().setValue(forward);
                            Toast.makeText(getApplicationContext(), "Forwarded Successfully", Toast.LENGTH_LONG).show();
                            recreate();
                            break;
                        }
                    }
                    if(flag == 0){
                        data.child("Bank_Guarantees").child(bgNum.getText().toString()).child("remarks").setValue(remarks.getText().toString());
                        Forward forward = new Forward(name,img,remarks.getText().toString(),forwardname,d,"0");
                        data.child("Forward").child("BGs").child(bgNum.getText().toString()).push().setValue(forward);
                        Toast.makeText(getApplicationContext(),"Forwarded Successfully",Toast.LENGTH_LONG).show();
                        recreate();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
