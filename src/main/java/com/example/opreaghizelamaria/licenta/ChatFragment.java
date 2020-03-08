package com.example.opreaghizelamaria.licenta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.scaledrone.lib.HistoryRoomListener;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;
import com.scaledrone.lib.SubscribeOptions;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;


public class ChatFragment extends Fragment implements RoomListener {


    private String channelID = "ZymFsK5YUCi1C9gx";
    private String roomName = "observable-room";
    private EditText editText;
    private Scaledrone scaledrone;
    private MessageAdapter messageAdapter;
    private ListView messagesView;
    private NavigationView nv;
    SharedPreferences sharedPreferences;

    Intent intent;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;

    private static String text;




   public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment f = new ChatFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);


        dl = (DrawerLayout)v.findViewById(R.id.activity_main);

        t = new ActionBarDrawerToggle(getActivity(), dl,R.string.app_name,R.string.app_name);
        sharedPreferences=((AppCompatActivity)getActivity()).getSharedPreferences("Start",MODE_PRIVATE);

        dl.addDrawerListener(t);
        t.syncState();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        nv = (NavigationView)v.findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.profile) {
                    intent = new Intent(getActivity(), Profile.class);
                    startActivity(intent);
                }

                else if (id == R.id.chat) {
                    intent = new Intent(getActivity(), ChatMap.class);
                    startActivity(intent);
                }
                else if (id == R.id.home) {
                    intent = new Intent(getActivity(), Main.class);
                    startActivity(intent);
                }
                return true;




            }
        });
        editText = (EditText) v.findViewById(R.id.editText);
        ImageButton send=(ImageButton) v.findViewById(R.id.send);

        messageAdapter = new MessageAdapter(getContext());
        messagesView = (ListView) v.findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(v);
            }
        });


        scaledrone = new Scaledrone(channelID);
        scaledrone.connect(new Listener() {
            @Override
            public void onOpen() {
                System.out.println("Scaledrone connection open");


                scaledrone.subscribe(roomName, ChatFragment.this, new SubscribeOptions(20)).listenToHistoryEvents(new HistoryRoomListener() {
                    @Override
                    public void onHistoryMessage(Room room, com.scaledrone.lib.Message recmessage) {

                        if(sharedPreferences.getString("Id","null")=="null"){
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("Id",scaledrone.getClientID());
                            editor.apply();

                        }

                        String dataReceiver = recmessage.getData().toString();

                        try {
                            JSONObject obj = new JSONObject(dataReceiver);
                            boolean belongsToCurrentUser;

                            if(sharedPreferences.getString("Id","null").equals(obj.get("id")))
                                belongsToCurrentUser = true;
                            else belongsToCurrentUser=false;
                            final MSG mdata = new MSG(obj.get("name").toString(),obj.get("message").toString(),obj.get("id").toString());
                            final Message message = new Message( obj, belongsToCurrentUser);
                            ((AppCompatActivity)getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageAdapter.add(message);
                                    messagesView.setSelection(messagesView.getCount() - 1);
                                }
                            });

                        } catch (Exception e) {
                            System.out.println("nu e json");
                        }

                    }

                });
            }

            @Override
            public void onOpenFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onClosed(String reason) {
                System.err.println(reason);
            }
        });


        return v;


    }



    public void sendMessage(View view) {

        String message = editText.getText().toString();
        if (message.length() > 0) {
            MSG msg=new MSG(message,sharedPreferences.getString("Nume","anonim"),sharedPreferences.getString("Id","null"));
            System.out.println(message);
            scaledrone.publish(roomName, msg);
            editText.getText().clear();
        }
    }

    @Override
    public void onOpen(Room room) {
        System.out.println("Conneted to room");
    }

    @Override
    public void onOpenFailure(Room room, Exception ex) {
        System.err.println(ex+"    fail");
    }

    @Override
    public void onMessage(Room room, com.scaledrone.lib.Message receivedMessage) {



        String dataReceiver=receivedMessage.getData().toString();

        try{
            JSONObject obj=new JSONObject(dataReceiver);


            final MSG mdata = new MSG(obj.get("name").toString(),obj.get("message").toString(),obj.get("id").toString());
            boolean belongsToCurrentUser = receivedMessage.getClientID().equals(scaledrone.getClientID());
            final Message message = new Message(obj, belongsToCurrentUser);
            ((AppCompatActivity)getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.add(message);
                    messagesView.setSelection(messagesView.getCount() - 1);
                }
            });

        }catch (Exception e){
            System.out.println("nu e json");
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }



}



class MSG{

    private String message, color,name,id;

    public MSG(String message,String name,String id){
        this.message=message;
        this.name=name;
        this.id=id;
    }
    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getMessage(){return message;}

    public String getId() { return id; }
}
