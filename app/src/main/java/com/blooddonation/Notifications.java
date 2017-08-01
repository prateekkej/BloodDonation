package com.blooddonation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * Created by Prateek on 7/28/2017.
 */

public class Notifications extends Fragment {
    View v;
    RecyclerView listView;
    NotificationsCardAdapter notificationsCardAdapter;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.notifications,container,false);
        initializeViews();

                return v;
    }
    void initializeViews(){
        listView = (RecyclerView) v.findViewById(R.id.notifications_list);
        notificationsCardAdapter = new NotificationsCardAdapter();
        listView.setAdapter(notificationsCardAdapter);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));

    }
    class NotificationsCardAdapter extends RecyclerView.Adapter<Notification_card>{

        @Override
        public Notification_card onCreateViewHolder(ViewGroup parent, int viewType) {
            View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.notification,parent,false);
            return new Notification_card(v);
        }


        public void onBindViewHolder(Notification_card holder, int position) {
        }

        @Override
        public int getItemCount() {
            return 7;
        }
    }
    class Notification_card extends RecyclerView.ViewHolder{
        public Notification_card(View itemView) {
            super(itemView);
            ImageView userImage=(ImageView)itemView.findViewById(R.id.donorImage);
        }
    }


}
