package com.example.whatschat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.StringBuilderPrinter;
import android.view.Menu;
import android.view.MenuItem;

import com.example.whatschat.Adapters.MessagesAdapter;
import com.example.whatschat.Adapters.StatusAdapter;
import com.example.whatschat.Models.User_status;
import com.example.whatschat.Models.status;
import com.example.whatschat.R;
import com.example.whatschat.Models.User;
import com.example.whatschat.Adapters.UsersAdapter;
import com.example.whatschat.databinding.ActivityMainBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseDatabase database;
    //to take a data from users
    ArrayList<User> users;
    UsersAdapter usersAdapter;

    StatusAdapter statusAdapter;
    ArrayList<User_status> userStatuses;
        ProgressDialog dialog;
       User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog=new ProgressDialog(this);
        dialog.setMessage("uploading story...");
        dialog.setCancelable(false);
        database = FirebaseDatabase.getInstance();
        users = new ArrayList<>();
        userStatuses = new ArrayList<>();

        database.getReference().child("users").child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        usersAdapter = new UsersAdapter(this,users);
        statusAdapter = new StatusAdapter(this,userStatuses);
       // binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.statuslist.setLayoutManager(layoutManager);

        binding.statuslist.setAdapter(statusAdapter);

        binding.recyclerView.setAdapter(usersAdapter);
        //TAKING DATA FROM FIREBASE
        database.getReference().child("users").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                //adding children of users in arraylist
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    User user = snapshot1.getValue(User.class);
                    users.add(user);

                }
                //notify to update data base or not
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.status:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,75);
                        break;
                }
                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data !=null){
            dialog.show();
            if(data.getData()!=null){
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("status").child(date.getTime()+"");

                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    User_status user_status=new User_status();
                                    user_status.setName(user.getName());
                                    user_status.setProfileImage(user.getProfileImage());
                                    user_status.setLastUpdated(date.getTime());

                                    HashMap<String,Object>obj = new HashMap<>();
                                    obj.put("name",user_status.getName());
                                    obj.put("profileImage",user_status.getProfileImage());
                                    obj.put("lastUpdated",user_status.getLastUpdated());

                                    String imageUrl = uri.toString();
                                    //Status status = new Status(imageUrl,user_status.getLastUpdated());

                                    database.getReference()
                                                    .child("stories")
                                            .child(FirebaseAuth.getInstance().getUid())
                                                    .updateChildren(obj);
                                    database.getReference().child("stories")
                                                    .child((FirebaseAuth.getInstance().getUid()))
                                                            .child("statuses")
                                                                    .push();
                                                                            //.setValue(status);

                                dialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu,menu);
        return super.onCreateOptionsMenu(menu);

    }
}