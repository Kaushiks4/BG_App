package my.app.bankguaranteemonitor;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private EditText bgNum, amount,work,remarks;
    private TextView Date, dueDate;
    private Button image, file, submit,add;
    private Spinner nigam, type, bgDiv,toname;
    private String bgNigam, bgType, division,name,forwardname,imageref;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_FILE_REQUEST = 2;
    private String[] nigams = {"KNNL","CNNL","KBJNL","VJNL"};
    private String[] divisions;
    private Uri imageUri, FileUri;
    private StorageReference storage;
    private DatabaseReference database;
    private ArrayList<String> names;
    String[] types = {"FSD", "EMD", "APSD", "Mobilization Advance"};
    private DatePickerDialog.OnDateSetListener issuedate, expiredate;
    final Calendar cal = Calendar.getInstance();
    Map<String,String> init;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent i = getIntent();
        name = i.getStringExtra("Name");

        bgNum = (EditText) findViewById(R.id.editText8);
        type = (Spinner) findViewById(R.id.spinner9);
        Date = (TextView) findViewById(R.id.textView21);
        dueDate = (TextView) findViewById(R.id.textView22);
        amount = (EditText) findViewById(R.id.editText12);
        nigam = (Spinner) findViewById(R.id.spinner2);
        bgDiv = (Spinner) findViewById(R.id.spinner5);
        toname = (Spinner) findViewById(R.id.spinner12);
        remarks = (EditText) findViewById(R.id.editText14);

        work = (EditText) findViewById(R.id.editText10);
        image = (Button) findViewById(R.id.button8);
        add = (Button) findViewById(R.id.button26);

        file = (Button) findViewById(R.id.button5);
        submit = (Button) findViewById(R.id.button6);

        storage = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance().getReference();

        ArrayAdapter a = new ArrayAdapter(this, android.R.layout.simple_spinner_item, nigams);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nigam.setAdapter(a);

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, types);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(aa);

        nigam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bgNigam = parent.getItemAtPosition(position).toString();
                FirebaseDatabase data = FirebaseDatabase.getInstance();
                database = data.getReference();
                database.child("Divisions").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int i = 0;
                        long n = dataSnapshot.child(bgNigam).getChildrenCount();
                        divisions = new String[(int) n];
                        for (DataSnapshot values : dataSnapshot.child(bgNigam).getChildren()) {
                            divisions[i] = values.getValue().toString();
                            i++;
                        }

                        ArrayAdapter d = new ArrayAdapter(HomeActivity.this, android.R.layout.simple_spinner_item, divisions);
                        d.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        bgDiv.setAdapter(d);

                        bgDiv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                division = parent.getItemAtPosition(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // can leave this empty
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Connection Error! Try again!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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

        database.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
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
                ArrayAdapter aa = new ArrayAdapter(HomeActivity.this, android.R.layout.simple_spinner_item, names);
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

        issuedate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel1();
            }
        };
        Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(HomeActivity.this, issuedate, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        expiredate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel2();
            }
        };
        dueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(HomeActivity.this, expiredate, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submit();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this,AddDivision.class);
                startActivity(i);
            }
        });
    }

    private void updateLabel1() {
        String Format = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.US);
        Date.setText(sdf.format(cal.getTime()));
    }

    private void updateLabel2() {
        String Format = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.US);
        dueDate.setText(sdf.format(cal.getTime()));
    }

    private void openImageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Upload 1st Page of BG"), PICK_IMAGE_REQUEST);
    }

    private void openFileChooser() {
        Intent i = new Intent();
        i.setType("application/pdf");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Upload All the pages as PDF"), PICK_FILE_REQUEST);
    }
    public String getFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading");
                progressDialog.show();
                final StorageReference riversRef = storage.child(bgNum.getText().toString()+"."+getFileExtension(imageUri));
                riversRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageref = uri.toString();
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
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            FileUri = data.getData();
            if (FileUri != null) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading");
                progressDialog.show();
                StorageReference riversRef = storage.child(bgNum.getText().toString()+"."+getFileExtension(FileUri));
                riversRef.putFile(FileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), "No File Selected", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void Submit() {
        if (bgNum.getText().toString().isEmpty()) {
            bgNum.setError("Required");
            bgNum.requestFocus();
        }
        if (amount.getText().toString().isEmpty()) {
            amount.setError("Required");
            amount.requestFocus();
        }
        if (imageUri == null) {
            Toast.makeText(getApplicationContext(), "Image not Selected", Toast.LENGTH_LONG).show();
            return;
        }
        if (FileUri == null) {
            Toast.makeText(getApplicationContext(), "PDF not Selected", Toast.LENGTH_LONG).show();
            return;
        }
        if(work.getText().toString().isEmpty()){
            work.setError("Required");
            work.requestFocus();
        }
        if(Date.getText().toString().equals("Select the Issue date")){
            Toast.makeText(getApplicationContext(),"Issue date not found!",Toast.LENGTH_LONG).show();
            return;
        }
        if(dueDate.getText().toString().equals("Select the Issue date")){
            Toast.makeText(getApplicationContext(),"Due date not found!",Toast.LENGTH_LONG).show();
            return;
        }
        if(remarks.getText().toString().isEmpty()){
            remarks.setError("Required");
            remarks.requestFocus();
        }
        if (!bgNum.getText().toString().isEmpty() && !amount.getText().toString().isEmpty() && !work.getText().toString().isEmpty() && !Date.getText().toString().equals("Select the Issue date") && !dueDate.getText().toString().equals("Select the Issue date") && !remarks.getText().toString().isEmpty()) {
            long amt = Integer.decode(amount.getText().toString()).longValue();
            BG bg = new BG(bgNum.getText().toString(), bgNigam, division, bgType, amt, Date.getText().toString(), dueDate.getText().toString(),work.getText().toString().trim());
            database.child("Bank_Guarantees").child(bgNum.getText().toString()).setValue(bg).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    uploadFiles();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy-HH:mm");
                    java.util.Date date = new Date();
                    String d = dateFormat.format(date);
                    database.child("Bank_Guarantees").child(bgNum.getText().toString()).child("Initiated_by").setValue(name);
                    database.child("Bank_Guarantees").child(bgNum.getText().toString()).child("date_init").setValue(d);
                    database.child("Bank_Guarantees").child(bgNum.getText().toString()).child("remarks").setValue(remarks.getText().toString());
                    Forward f = new Forward(name,imageref,remarks.getText().toString(),forwardname,d,"0");
                    database.child("Forward").child("BGs").child(bgNum.getText().toString()).push().setValue(f);
                    Toast.makeText(getApplicationContext(), "Bank Guarantee Added and forwarded to " +forwardname+ " Successfully!", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(HomeActivity.this,ChooseActivity.class);
                    i.putExtra("Name",name);
                    startActivity(i);
                    return;

                    }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Connection Error! Try Again", Toast.LENGTH_SHORT).show();
                    return;
                }
            });
        }
    }

    private void uploadFiles() {
        final StorageReference file = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = file.child(bgNum.getText().toString()+"."+getFileExtension(imageUri));
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri downloadUrl) {
                database.child("Bank_Guarantees").child(bgNum.getText().toString()).child("Image").setValue(downloadUrl.toString());
            }
        });
                dateRef = file.child(bgNum.getText().toString() + "."+getFileExtension(FileUri));
            dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri downloadUrl) {
                    database.child("Bank_Guarantees").child(bgNum.getText().toString()).child("File").setValue(downloadUrl.toString());
                }
            });
    }
}
