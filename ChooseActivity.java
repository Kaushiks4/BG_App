package my.app.bankguaranteemonitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseActivity extends AppCompatActivity {
    Button initiate,notify,init,add,forward;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        initiate = (Button) findViewById(R.id.button22);
        add = (Button) findViewById(R.id.button23);
        forward = (Button) findViewById(R.id.button30);
        init = (Button) findViewById(R.id.button7);
        notify = (Button) findViewById(R.id.button9);

        Intent i = getIntent();
        name = i.getStringExtra("Name");

        initiate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseActivity.this,InitiateHome.class);
                i.putExtra("Name",name);
                startActivity(i);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseActivity.this,HomeActivity.class);
                i.putExtra("Name",name);
                startActivity(i);
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseActivity.this,ForwardActivity.class);
                i.putExtra("Name",name);
                startActivity(i);
            }
        });
        init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseActivity.this,Init.class);
                i.putExtra("Name",name);
                startActivity(i);
            }
        });


        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseActivity.this,Notify.class);
                i.putExtra("Name",name);
                startActivity(i);
            }
        });
    }
}
