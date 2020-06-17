package my.app.bankguaranteemonitor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<String> bgnums,bgdivs,amounts,remarks,fnames,dates,works;
    ArrayList<Integer> ids;
    ArrayList<String> images;

    private ClickInterface mInterface;

    public MyAdapter(Context ct, ArrayList<String> bgn, ArrayList<String> bgd, ArrayList<String> amt, ArrayList<String> rem, ArrayList<String> fname, ArrayList<String> date, ArrayList<String> img,ArrayList<String> work,ArrayList<Integer> id,ClickInterface minterface){
        context = ct;
        ids = id;
        bgnums = bgn;
        bgdivs = bgd;
        amounts = amt;
        remarks = rem;
        dates = date;
        images = img;
        fnames = fname;
        works = work;
        mInterface = minterface;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.myrow,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if(ids == null){
            holder.idtxt.setVisibility(View.GONE);
            holder.id.setVisibility(View.GONE);
            holder.approve.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
            holder.forward.setVisibility(View.VISIBLE);
        }
        else{
            holder.id.setText(String.valueOf(ids.get(position)));
            holder.approve.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.VISIBLE);
            holder.forward.setVisibility(View.GONE);
        }
        if(bgnums == null){
            holder.bgn.setVisibility(View.GONE);
            holder.bgtxt.setVisibility(View.GONE);
        }
        else {
            holder.bgn.setText(bgnums.get(position));
        }
        holder.bgd.setText(bgdivs.get(position));
        holder.amt.setText(amounts.get(position));
        holder.fnam.setText(fnames.get(position));
        holder.rem.setText(remarks.get(position));
        holder.date.setText(dates.get(position));
        holder.name_of_work.setText(works.get(position));
        if(images == null) {
            holder.imageView.setVisibility(View.GONE);
        }
        else {
            Picasso.get().load(images.get(position)).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        if(bgdivs.size() == 0){
            Toast.makeText(context,"No new Notifications",Toast.LENGTH_LONG).show();
            return bgdivs.size();
        }
        return bgdivs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView bgtxt,name_of_work,bgn,bgd,amt,date,fnam,rem,id,idtxt;
        ImageView imageView;
        Button approve,delete,forward;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            idtxt = itemView.findViewById(R.id.textView7);
            id = itemView.findViewById(R.id.id);
            bgtxt = itemView.findViewById(R.id.textView6);
            name_of_work = itemView.findViewById(R.id.nameofwork);
            bgn = itemView.findViewById(R.id.bgn);
            bgd = itemView.findViewById(R.id.bgdiv);
            amt = itemView.findViewById(R.id.amt);
            date = itemView.findViewById(R.id.issdate);
            fnam = itemView.findViewById(R.id.fromname);
            rem = itemView.findViewById(R.id.rmk);
            forward = itemView.findViewById(R.id.button16);
            approve = itemView.findViewById(R.id.button10);
            delete = itemView.findViewById(R.id.button11);
            imageView = itemView.findViewById(R.id.imageView2);

            approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInterface.onItemClick(getAdapterPosition());
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInterface.onDelete(getAdapterPosition());
                }
            });

            forward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInterface.onForward(getAdapterPosition());
                }
            });

        }
    }
}
