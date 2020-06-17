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

public class HomeForward extends AppCompatActivity {
    private EditText remarks;
    private TextView bgNum,bgDiv,amount,nameOfWork;
    private Button submit, upload_photo,reupload,check;
    private String forwardname,name,img,d,bgnum,bgdiv,amt,namofwork;
    private Spinner toname;
    ArrayList<String> names;
    public static final int PICK_FILE_REQUEST = 1;
    ImageView imageView;
    Uri imageUri;
    DatabaseReference data;
    StorageReference storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_forward);

        Intent i = getIntent();
        name = i.getStringExtra("Name");
        bgnum = i.getStringExtra("bgnum");
        bgdiv = i.getStringExtra("bgdiv");
        amt = i.getStringExtra("amt");
        namofwork = i.getStringExtra("nameofwork");

        data = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();


        bgNum = (TextView) findViewById(R.id.textView42);
        bgNum.setText(bgnum);
        bgDiv = (TextView) findViewById(R.id.textView46);
        bgDiv.setText(bgdiv);
        amount = (TextView) findViewById(R.id.textView44);
        amount.setText(amt);
        nameOfWork = (TextView) findViewById(R.id.textView45);
        nameOfWork.setText(namofwork);

        remarks = (EditText) findViewById(R.id.editText11);
        upload_photo = (Button) findViewById(R.id.button19);
        submit = (Button) findViewById(R.id.button24);
        submit.setVisibility(View.GONE);
        toname = (Spinner) findViewById(R.id.spinner10);
        reupload = (Button) findViewById(R.id.button15);
        reupload.setVisibility(View.GONE);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setVisibility(View.GONE);

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
                ArrayAdapter aa = new ArrayAdapter(HomeForward.this, android.R.layout.simple_spinner_item, names);
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
        upload_photo.setOnClickListener(new View.OnClickListener() {
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
        startActivityForResult(Intent.createChooser(i, "IMAGE"), PICK_FILE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
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
                                upload_photo.setVisibility(View.GONE);
                                imageView.setVisibility(View.VISIBLE);
                                reupload.setVisibility(View.VISIBLE);
                                submit.setVisibility(View.VISIBLE);
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
        reupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit.setVisibility(View.GONE);
                submitForm();
            }
        });
    }

    private void submitForm(){
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
