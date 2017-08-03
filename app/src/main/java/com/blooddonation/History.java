package com.blooddonation;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by Prateek on 7/28/2017.
 */

public class History extends Fragment {
    View v;
    RecyclerView listView;
    java.util.concurrent.CopyOnWriteArrayList<Story> myHistory;
    NotificationsCardAdapter notificationsCardAdapter;
    TextView noHistoryBanner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.notifications,container,false);
        myHistory= new java.util.concurrent.CopyOnWriteArrayList<>();
        initializeViews();
        Dashboard.databaseReference.child("users-stories").child(Dashboard.me.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot i:dataSnapshot.getChildren()){
                    Story a=i.getValue(Story.class);
                    myHistory.add(a);
                }
            notificationsCardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
            View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.storycard,parent,false);
            return new Notification_card(v);
        }


        public void onBindViewHolder(final Notification_card holder, final int position) {
            try{Glide.with(getActivity()).load(myHistory.get(position).photoURL).into(holder.donationImage);}catch (NullPointerException e){}
            holder.to.setText(myHistory.get(position).to);
            holder.donationImage.buildDrawingCache();
            holder.stor.setText(myHistory.get(position).story);
            holder.on.setText(myHistory.get(position).d+" / "+(myHistory.get(position).m)+" / "+ myHistory.get(position).y);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog a= new AlertDialog.Builder(getActivity()).setMessage("Do you want to delete this story?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final ProgressDialog p=new ProgressDialog(getContext());
                            p.setMessage("Deleting Story");
                            p.setCancelable(false);
                            p.show();
                            Dashboard.databaseReference.child("users-stories").child(Dashboard.me.getUid()).child(myHistory.get(position).key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Dashboard.storageReference.child("users-stories").child(Dashboard.me.getUid()).child(myHistory.get(position).key).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "Story Deleted.", Toast.LENGTH_SHORT).show();
                                            myHistory.remove(position);
                                            notificationsCardAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            p.dismiss();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create();
                    a.show();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return myHistory.size();
        }
    }
    class Notification_card extends RecyclerView.ViewHolder{
        ImageView donationImage;
        TextView stor,to,on;

        public Notification_card(View itemView) {
            super(itemView);
            donationImage = (ImageView)itemView.findViewById(R.id.donationimage);
            stor = (TextView)itemView.findViewById(R.id.storytext);
            to = (TextView)itemView.findViewById(R.id.donatedtotext);
            on = (TextView)itemView.findViewById(R.id.dinationDate);

        }
    }


}
