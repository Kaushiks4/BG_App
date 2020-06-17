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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Bg_Activity extends AppCompatActivity {
    private String bgNigam,bgDiv,bgType,nameOfWork,amount,forwardname,d,name,imageref;
    private TextView bgnigam,bgdiv,amt,nameofwork,issuedate,Date;
    private EditText Amount,remarks,bgNum;
    private Button image,file,forward;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_FILE_REQUEST = 2;
    private Uri imageUri, FileUri;
    private int id;
    private StorageReference storage;
    private DatabaseReference database;
    private Spinner type,toname;
    ArrayList<String> names;
    private HashMap<String, String> init;
    private String[] types = {"FSD", "EMD", "APSD", "Mobilization Advance"};
    private DatePickerDialog.OnDateSetListener date, expiredate;
    final Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbg);

        Intent i = getIntent();
        name = i.getStringExtra("Name");
        bgNigam = i.getStringExtra("nigam");
        bgDiv = i.getStringExtra("Div");
        amount = i.getStringExtra("amt");
        nameOfWork = i.getStringExtra("nameofwork");
        String id1 = (i.getStringExtra("id"));
        id = Integer.parseInt(id1);
        id = -1 * id;
        System.out.println(id);
        bgnigam = (TextView) findViewById(R.id.bgnigam);
        bgnigam.setText(bgNigam);
        bgdiv = (TextView) findViewById(R.id.bgdiv);
        bgdiv.setText(bgDiv);
        amt = (TextView) findViewById(R.id.amount);
        amt.setText(amount);
        nameofwork = (TextView) findViewById(R.id.nameofwork);
        nameofwork.setText(nameOfWork);

        type = (Spinner) findViewById(R.id.spinner6);
        issuedate = (TextView) findViewById(R.id.textView40);
        Date = (TextView) findViewById(R.id.textView41);
        Amount = (EditText) findViewById(R.id.editText15);
        remarks = (EditText) findViewById(R.id.editText16);
        toname = (Spinner) findViewById(R.id.spinner11);
        bgNum = (EditText) findViewById(R.id.editText17);

        image = (Button) findViewById(R.id.button17);
        file = (Button) findViewById(R.id.button18);
        forward = (Button) findViewById(R.id.button20);

        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance().getReference();


        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH:mm");
        java.util.Date dat = new Date();
        d = formatter.format(dat);

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
                ArrayAdapter aa = new ArrayAdapter(Bg_Activity.this, android.R.layout.simple_spinner_item, names);
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

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, types);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(aa);

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bgType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel1();
            }
        };


        issuedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Bg_Activity.this, date, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
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
        Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Bg_Activity.this, expiredate, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
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

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    private void updateLabel1() {
        String Format = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.US);
        issuedate.setText(sdf.format(cal.getTime()));
    }

    private void updateLabel2() {
        String Format = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.US);
        Date.setText(sdf.format(cal.getTime()));
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

    private void submitForm(){
        if(bgNum.getText().toString().isEmpty()){
            bgNum.setError("Required");
            bgNum.requestFocus();
        }
        if(remarks.getText().toString().isEmpty()){
            remarks.setError("Required");
            remarks.requestFocus();
        }
        if (Amount.getText().toString().isEmpty()) {
            Amount.setError("Required");
            Amount.requestFocus();
        }
        if (imageUri == null) {
            Toast.makeText(getApplicationContext(), "Image not Selected", Toast.LENGTH_LONG).show();
            return;
        }
        if (FileUri == null) {
            Toast.makeText(getApplicationContext(), "PDF not Selected", Toast.LENGTH_LONG).show();
            return;
        }
        if(issuedate.getText().toString().equals("Select the Issue date")){
            Toast.makeText(getApplicationContext(),"Issue date not found!",Toast.LENGTH_LONG).show();
            return;
        }
        if(Date.getText().toString().equals("Select the Issue date")){
            Toast.makeText(getApplicationContext(),"Due date not found!",Toast.LENGTH_LONG).show();
            return;
        }
        if (!bgNum.getText().toString().isEmpty() && !Amount.getText().toString().isEmpty()  && !Date.getText().toString().equals("Select the Issue date") && !issuedate.getText().toString().equals("Select the Issue date")) {
            final String num = bgNum.getText().toString().trim();
            long amt = Integer.decode(Amount.getText().toString()).longValue();
            BG bg = new BG(num, bgNigam, bgDiv, bgType, amt, issuedate.getText().toString(), Date.getText().toString(),nameOfWork);
            database.child("Bank_Guarantees").child(num).setValue(bg).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    uploadFiles();
                    database.child("Initiations").orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot keys : dataSnapshot.getChildren()) {
                                    String init_name = keys.child("Initiated_by").getValue().toString();
                                    String init_date = keys.child("date_init").getValue().toString();
                                    database.child("Bank_Guarantees").child(bgNum.getText().toString()).child("Initiated_by").setValue(init_name);
                                    database.child("Bank_Guarantees").child(bgNum.getText().toString()).child("date_init").setValue(init_date);
                                    database.child("Bank_Guarantees").child(bgNum.getText().toString()).child("remarks").setValue(remarks.getText().toString());
                                    break;
                                }
                            }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
            database.child("Forward").child("Initiates").orderByChild("ID").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String key = "";
                    for (DataSnapshot keys : dataSnapshot.getChildren()) {
                        key = keys.getKey();
                        break;
                    }
                    database.child("Forward").child("Initiates").child(key).child("Notify").setValue("1");
                    init = new HashMap<String, String>();
                    init.putAll((Map<? extends String, ? extends String>) dataSnapshot.child(key).getValue());
                    init.put("Notify","1");
                    database.child("Initiations").orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot k: dataSnapshot.getChildren()){
                                String from = k.child("Initiated_by").getValue().toString();
                                String to = "Sharan";
                                String date = k.child("date_init").getValue().toString();
                                String remark = k.child("remarks").getValue().toString();
                                Forward forward = new Forward(from,null,remark,to,date,"1");
                                database.child("Forward").child("BGs").child(bgNum.getText().toString()).push().setValue(forward);
                                database.child("Forward").child("BGs").child(bgNum.getText().toString()).push().setValue(init);
                                Forward f = new Forward(name,imageref,remarks.getText().toString(),forwardname,d,"0");
                                database.child("Forward").child("BGs").child(bgNum.getText().toString()).push().setValue(f);
                                Toast.makeText(getApplicationContext(), "Bank Guarantee Added Successfully and forwarded to " + forwardname + "!", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(Bg_Activity.this,ChooseActivity.class);
                                i.putExtra("Name",name);
                                startActivity(i);
                                return;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    return;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

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
