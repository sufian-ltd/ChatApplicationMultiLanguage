package com.example.smartchatapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartchatapp.Adapter.UserAdapter;
import com.example.smartchatapp.R;
import com.example.smartchatapp.model.Chatlist;
import com.example.smartchatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    FirebaseUser fUser;
    DatabaseReference reference;

    private List<Chatlist> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chats,container,false);
        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        userList=new ArrayList<>();

        reference=FirebaseDatabase.getInstance().getReference("Chatlist").child(fUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chatlist chatlist=snapshot.getValue(Chatlist.class);
                    userList.add(chatlist);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        reference= FirebaseDatabase.getInstance().getReference("Chats");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userList.clear();
//                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
//                    Chat chat=snapshot.getValue(Chat.class);
//                    if(chat.getSender().equals(fUser.getUid())){
//                        userList.add(chat.getReceiver());
//                    }
//                    if(chat.getReceiver().equals(fUser.getUid())){
//                        userList.add(chat.getSender());
//                    }
//                }
//                readChats();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        return view;
    }

    private void chatList(){
        mUsers=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user=snapshot.getValue(User.class);
                    for(Chatlist chatlist:userList){
                        if(user.getId().equals(chatlist.getId())){
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter=new UserAdapter(getContext(),mUsers,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    private void readChats(){
//        mUsers=new ArrayList<>();
//        reference=FirebaseDatabase.getInstance().getReference("Users");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mUsers.clear();
//                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
//                    User user=snapshot.getValue(User.class);
//                    for (String id:userList){
//                        if(user.getId().equals(id)){
//                            if(mUsers.size()!=0){
//                                for(User user1:mUsers){
//                                    if(!user.getId().equals(user1.getId())){
//                                        mUsers.add(user);
//                                    }
//                                }
//                            }
//                            else{
//                                mUsers.add(user);
//                            }
//                        }
//                    }
//                }
//                userAdapter=new UserAdapter(getContext(),mUsers,true);
//                recyclerView.setAdapter(userAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}
