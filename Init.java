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

public class Init extends AppCompatActivity implements ClickInterface{
    ArrayList<String> bgnigams,bgdivs,amounts,remarks,fnames,name_of_work,dates;
    ArrayList<Integer> ids;
    String name;
    RecyclerView recyclerView;
    DatabaseReference data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        Intent i = getIntent();
        name = i.getStringExtra("Name");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        data = FirebaseDatabase.getInstance().getReference();
        if(name.equals("Sharan")) {
            data.child("Initiations").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ids = new ArrayList<>();
                    bgnigams = new ArrayList<>();
                    bgdivs = new ArrayList<>();
                    amounts = new ArrayList<>();
                    remarks = new ArrayList<>();
                    fnames = new ArrayList<>();
                    name_of_work = new ArrayList<>();
                    dates = new ArrayList<>();
                    for (DataSnapshot inits : dataSnapshot.getChildren()) {
                        if (inits.child("Notify").getValue().toString().equals("0")) {
                            int id = Integer.parseInt(inits.child("id").getValue().toString());
                            id = -1 * id;
                            ids.add(id);
                            bgnigams.add(inits.child("bgNigam").getValue().toString());
                            bgdivs.add(inits.child("bgDivision").getValue().toString());
                            amounts.add(inits.child("amount").getValue().toString());
                            remarks.add(inits.child("remarks").getValue().toString());
                            fnames.add(inits.child("Initiated_by").getValue().toString());
                            name_of_work.add(inits.child("name_of_work").getValue().toString());
                            dates.add(inits.child("date_init").getValue().toString());
                        }
                    }
                    MyAdapter myAdapter = new MyAdapter(Init.this, null, bgdivs, amounts, remarks, fnames, dates, null, name_of_work, ids,Init.this);
                    recyclerView.setAdapter(myAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Init.this));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            data.child("Forward").child("Initiates").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ids = new ArrayList<>();
                    bgnigams = new ArrayList<>();
                    bgdivs = new ArrayList<>();
                    amounts = new ArrayList<>();
                    remarks = new ArrayList<>();
                    fnames = new ArrayList<>();
                    name_of_work = new ArrayList<>();
                    dates = new ArrayList<>();
                    for(DataSnapshot keys: dataSnapshot.getChildren()){
                        if(keys.child("To").getValue().toString().equals(name)){
                            if(keys.child("Notify").getValue().toString().equals("0")){
                                String id = keys.child("ID").getValue().toString();
                                ids.add(Integer.valueOf(id));
                                fnames.add(keys.child("From").getValue().toString());
                                dates.add(keys.child("Date").getValue().toString());
                                remarks.add(keys.child("Remarks").getValue().toString());
                                data.child("Initiations").orderByChild("id").equalTo(Integer.parseInt(id)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot k: dataSnapshot.getChildren()){
                                            bgnigams.add(k.child("bgNigam").getValue().toString());
                                            bgdivs.add(k.child("bgDivision").getValue().toString());
                                            amounts.add(k.child("amount").getValue().toString());
                                            name_of_work.add(k.child("name_of_work").getValue().toString());
                                            MyAdapter myAdapter = new MyAdapter(Init.this, null, bgdivs, amounts, remarks, fnames, dates, null, name_of_work, null,Init.this);
                                            recyclerView.setAdapter(myAdapter);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(Init.this));
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onItemClick(int position) {
        String div = bgdivs.get(position);
        String amt = amounts.get(position);
        String nameOfWork = name_of_work.get(position);
        String id = String.valueOf(ids.get(position));
        int id1 = Integer.parseInt(id);
        id1 = -id1;
        id = String.valueOf(id1);
        Intent i = new Intent(Init.this,InitForward.class);
        i.putExtra("Div",div);
        i.putExtra("amt",amt);
        i.putExtra("nameofwork",nameOfWork);
        i.putExtra("id",id);
        i.putExtra("Name",name);
        startActivity(i);
    }

    @Override
    public void onDelete(int position) {
        String id = String.valueOf(ids.get(position));
        int id1 = Integer.parseInt(id);
        id1 = -id1;
        data.child("Initiations").orderByChild("id").equalTo(id1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String key="";
                for(DataSnapshot keys: dataSnapshot.getChildren()){
                    key = keys.getKey();
                    break;
                }
                data.child("Initiations").child(key).removeValue();
                recreate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onForward(int position) {
        String nigam = bgnigams.get(position);
        String div = bgdivs.get(position);
        String amt = amounts.get(position);
        String nameOfWork = name_of_work.get(position);
        int id1 = ids.get(position);
        id1 = -1 * id1;
        String id = String.valueOf(id1);
        Intent i = new Intent(Init.this,Bg_Activity.class);
        i.putExtra("nigam",nigam);
        i.putExtra("Div",div);
        i.putExtra("amt",amt);
        i.putExtra("nameofwork",nameOfWork);
        i.putExtra("id",id);
        i.putExtra("Name",name);
        startActivity(i);
    }
}
