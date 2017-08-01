package com.blooddonation;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.Map;

/**
 * Created by Prateek on 7/30/2017.
 */

public class DonorsListFragment extends Fragment {
    public View view;
    public RecyclerView list;
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
       //check if a marker has been filtered out.
        if(MapsFragment.closePeople.get(position).marker.isVisible()) {
            if(MapsFragment.closePeople.get(position).clicked){
                holder.itemView.setBackgroundColor(ColorUtils.setAlphaComponent(Color.CYAN,60));
                MapsFragment.closePeople.get(position).clicked=false;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       holder.itemView.setBackgroundColor(Color.WHITE);
                    }
                },1000);
            }
            holder.itemView.setVisibility(View.VISIBLE);
            holder.name.setText(MapsFragment.closePeople.get(position).name);
            holder.phone.setText(MapsFragment.closePeople.get(position).phone);
            holder.loc.setText(MapsFragment.closePeople.get(position).city);
            holder.bloodgroup.setText(MapsFragment.closePeople.get(position).bloodgroup);
            Glide.with(getContext()).load(MapsFragment.closePeople.get(position).photo).into(holder.donorImage);
            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MapsFragment.closePeople.get(position).phone != null) {
                        Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + MapsFragment.closePeople.get(position).phone));
                        startActivity(call);
                    }
                }
            });
            holder.sms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sms = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + MapsFragment.closePeople.get(position).phone));
                    sms.putExtra("sms_body", "Hey! I got your number from Blood Donation App.I am in urgent need of your blood group." +
                            "Please revert asap.\n\nThanks.");
                    startActivity(sms);
                }
            });
            holder.email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent em = new Intent(Intent.ACTION_SENDTO);
                    em.setData(Uri.parse("mailto:"));
                    em.putExtra(Intent.EXTRA_SUBJECT, "Blood Required[URGENT]");
                    em.putExtra(Intent.EXTRA_EMAIL, MapsFragment.closePeople.get(position).email);
                    em.putExtra(Intent.EXTRA_TEXT, "Hello " + MapsFragment.closePeople.get(position).name + "\n\n" +
                            "I am writing this mail , as i am in the urgent need of blood same as your blood group. " +
                            "Any help from your side would be great.\n\n" +
                            "Yours truly \n" + Dashboard.user.mname);
                    startActivity(em);
                }
            });
        }else{//hides a card if not in filter range
            holder.itemView.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return MapsFragment.closePeople.size();
    }
}
    class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageButton sms,call,email;
        public ImageView donorImage;
        public TextView name,loc,phone,bloodgroup;

        public MyViewHolder(View view){
            super(view);
            name=(TextView)view.findViewById(R.id.donorName);
            loc=(TextView)view.findViewById(R.id.donorCity);
            phone=(TextView)view.findViewById(R.id.donorPhone);
            donorImage=(ImageView) view.findViewById(R.id.donorImage);
            sms=(ImageButton) view.findViewById(R.id.smsDonor);
            call=(ImageButton)view.findViewById(R.id.callDonor);
            bloodgroup=(TextView)view.findViewById(R.id.bloodDonor);
            email=(ImageButton)view.findViewById(R.id.emailDonor);
        }


    }

}
