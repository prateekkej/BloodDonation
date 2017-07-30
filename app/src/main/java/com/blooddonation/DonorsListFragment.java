package com.blooddonation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

/**
 * Created by Prateek on 7/30/2017.
 */

public class DonorsListFragment extends Fragment {
View view;
    private RecyclerView list;
   public static MyRecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initializeViews(inflater,container);
    return view;
    }
    public void initializeViews(LayoutInflater inflater,ViewGroup container){
        view= inflater.inflate(R.layout.donors_list_fragment,container,false);
        list=(RecyclerView)view.findViewById(R.id.donors_list);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
         adapter= new MyRecyclerAdapter();
        list.setAdapter(adapter);

            }

class MyRecyclerAdapter extends RecyclerView.Adapter<MyViewHolder>{


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.donords_list_card,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.name.setText(MapsFragment.closePeople.get(position).name);
        holder.phone.setText(MapsFragment.closePeople.get(position).phone);
        holder.loc.setText(MapsFragment.closePeople.get(position).city);
        Glide.with(getContext()).load(MapsFragment.closePeople.get(position).photo).into(holder.donorImage);

    }

    @Override
    public int getItemCount() {
        return MapsFragment.closePeople.size();
    }
}
    class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageButton sms,call;
        public ImageView donorImage;
        public TextView name,loc,phone;
        public MyViewHolder(View view){
            super(view);
            name=(TextView)view.findViewById(R.id.donorName);
            loc=(TextView)view.findViewById(R.id.donorCity);
            phone=(TextView)view.findViewById(R.id.donorPhone);
            donorImage=(ImageView) view.findViewById(R.id.donorImage);
            sms=(ImageButton) view.findViewById(R.id.smsDonor);
            call=(ImageButton)view.findViewById(R.id.callDonor);


        }


    }

}
