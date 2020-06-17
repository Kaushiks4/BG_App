package my.app.bankguaranteemonitor;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Notify extends AppCompatActivity implements  ClickInterface{
    ArrayList<String> bgnums,bgdivs,amounts,remarks,fnames,name_of_work,dates;
    ArrayList<String> images;
    String name;
    RecyclerView recyclerView;
    DatabaseReference data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        Intent i = getIntent();
        name = i.getStringExtra("Name");
        data = FirebaseDatabase.getInstance().getReference();
        data.child("Forward").child("BGs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bgnums = new ArrayList<>();
                bgdivs = new ArrayList<>();
                amounts = new ArrayList<>();
                dates = new ArrayList<>();
                fnames = new ArrayList<>();
                remarks = new ArrayList<>();
                images = new ArrayList<>();
                name_of_work = new ArrayList<>();
                for (DataSnapshot bgs : dataSnapshot.getChildren()) {
                    String bg = bgs.getKey();
                    for (DataSnapshot d : bgs.getChildren()) {
                        if (d.child("To").getValue().toString().equals(name)) {
                            if (d.child("Notify").getValue().toString().equals("0")) {
                                bgnums.add(bg);
                                remarks.add(d.child("Remarks").getValue().toString());
                                images.add(d.child("Image").getValue().toString());
                                fnames.add(d.child("From").getValue().toString());
                                dates.add(d.child("Date").getValue().toString());
                                System.out.println(d.getKey());
                                data.child("Bank_Guarantees").child(bg).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        bgdivs.add(dataSnapshot.child("bgDivision").getValue().toString());
                                        amounts.add(dataSnapshot.child("amount").getValue().toString());
                                        name_of_work.add(dataSnapshot.child("name_of_work").getValue().toString());
                                        MyAdapter myAdapter = new MyAdapter(Notify.this, bgnums, bgdivs, amounts, remarks, fnames, dates, images, name_of_work, null,Notify.this);
                                        recyclerView.setAdapter(myAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(Notify.this));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemClick(int position) {
        return;
    }

    @Override
    public void onDelete(int position) {
        return;
    }

    @Override
    public void onForward(int position) {
        String bgnum = bgnums.get(position);
        String bgdiv = bgdivs.get(position);
        String nameofwork = name_of_work.get(position);
        String amt = amounts.get(position);

        Intent i = new Intent(Notify.this,HomeForward.class);
        i.putExtra("Name",name);
        i.putExtra("bgnum",bgnum);
        i.putExtra("amt",amt);
        i.putExtra("bgdiv",bgdiv);
        i.putExtra("nameofwork",nameofwork);
        startActivity(i);
    }
}
