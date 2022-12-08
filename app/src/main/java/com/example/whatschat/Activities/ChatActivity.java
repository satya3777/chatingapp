package com.example.whatschat.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.whatschat.Adapters.MessagesAdapter;
import com.example.whatschat.Models.Message;
import com.example.whatschat.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    MessagesAdapter adapter;
    ArrayList<Message> messages;

    String senderRoom, receiverRoom;
    FirebaseDatabase database;
    FirebaseStorage storage;

    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messages = new ArrayList<>();
        adapter = new MessagesAdapter(this,messages,senderRoom,receiverRoom);
        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));
        binding.recycleView.setAdapter(adapter);



        dialog = new ProgressDialog(this);
        dialog.setMessage("uploading image...");
        dialog.setCancelable(false);

        String name = getIntent().getStringExtra("name");
        String receiverUid = getIntent().getStringExtra("uid");
        //for taking current uid
        String senderUid = FirebaseAuth.getInstance().getUid();

        //creating unique room
        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;



        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        //to acces a messege of sender
        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                    .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                messages.clear();
                                for (DataSnapshot snapshot1:snapshot.getChildren()){
                                    Message message=snapshot1.getValue(Message.class);
                                    message.setMessageId(snapshot1.getKey());
                                    messages.add(message);

                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
        //creating button
        binding.btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageTxt = binding.messagebox.getText().toString();
                //to send at database
                Date date = new Date();
                Message message = new Message(messageTxt,senderUid, date.getTime());
                binding.messagebox.setText("");

                String keyvalue = database.getReference().push().getKey();

                HashMap<String,Object> lastMsgobj = new HashMap<>();
                lastMsgobj.put("lastMsg",message.getMessage());
                lastMsgobj.put("lastMsgTime",date.getTime());

                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgobj);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgobj);

                //sended to database message
                //child = creating folder
                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(keyvalue)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            //taking from database as receiver
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .child("messages")
                                        .child(keyvalue)
                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });


                            }
                                        });
                            }
                        });
        //to send image using attachment pins
        binding.attchment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,25);
            }
        });

        //action or title back icon
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode==25){
            if(data !=null){
                if(data.getData() !=null){
                    Uri selectedImage = data.getData();
                    Calendar calendar= Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis()+"");
                    dialog.show();
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                      String filePath = uri.toString();
                                        Toast.makeText(ChatActivity.this, "uploaded", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }

    //to get back action on chat activty
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}