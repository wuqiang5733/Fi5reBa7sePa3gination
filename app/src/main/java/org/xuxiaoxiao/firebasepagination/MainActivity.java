package org.xuxiaoxiao.firebasepagination;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wilddog.client.ChildEventListener;
import com.wilddog.client.DataSnapshot;
import com.wilddog.client.Query;
import com.wilddog.client.SyncError;
import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    WilddogApp wilddogApp;
    private SyncReference mWilddogRef;
    ContentAdapter contentAdapter;
    Button pageUp;
    Button pageDown;
    private ArrayList<ChatMessage> arrayList = new ArrayList<>();
    private Query query;
    private ChildEventListener childEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pageUp = (Button) findViewById(R.id.page_up);
        pageUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lastkey = arrayList.get(0).getMessageID();
                arrayList.clear();
                Log.d("WQWQ", lastkey + "__lastKey");
                Log.d("WQWQ", "+-+-+-+-+-+-+-+-+-+-+");
                query.removeEventListener(childEventListener);
                query = mWilddogRef.orderByKey().endAt(lastkey).limitToLast(6);
                query.addChildEventListener(childEventListener);

            }
        });
        pageDown = (Button) findViewById(R.id.page_down);
        pageDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lastkey = arrayList.get(5).getMessageID();
                arrayList.clear();
                Log.d("WQWQ", lastkey + "__lastKey");
                Log.d("WQWQ", "+-+-+-+-+-+-+-+-+-+-+");
                query.removeEventListener(childEventListener);
                query = mWilddogRef.orderByKey().startAt(lastkey).limitToFirst(6);
                query.addChildEventListener(childEventListener);
            }
        });
        WilddogOptions wilddogOptions = new WilddogOptions.Builder().setSyncUrl("https://xuxiaoxiao1314.wilddogio.com").build();
        wilddogApp = WilddogApp.initializeApp(this, wilddogOptions);
        mWilddogRef = WilddogSync.getInstance().getReference().child("chat");
        query = mWilddogRef.limitToLast(6);
        childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                ChatMessage chatMessage = (ChatMessage) snapshot.getValue(ChatMessage.class);
                String string = chatMessage.getMessageID();
                Log.d("WQWQ", string);
                arrayList.add(chatMessage);
//                Log.d("WQWQ", String.valueOf(arrayList.size()));
                contentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(SyncError error) {

            }
        };
        query.addChildEventListener(childEventListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contentAdapter = new ContentAdapter(arrayList);
        recyclerView.setAdapter(contentAdapter);
    }

    private class ContentAdapter extends RecyclerView.Adapter<ContentViewHolder> {
        ArrayList<ChatMessage> arrayList;

        public ContentAdapter(ArrayList<ChatMessage> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.recycler_view_content, parent, false);
//            Log.d("WQWQ", "onCreateViewHolder");
            return new ContentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ContentViewHolder holder, int position) {
            holder.messageTextView.setText(arrayList.get(position + 1).getMessage());
        }

        @Override
        public int getItemCount() {
//            Log.d("WQWQ", String.valueOf(arrayList.size() + "getItemCount"));
            return arrayList.size() - 1;
        }
    }

    private class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public ContentViewHolder(View itemView) {
            super(itemView);
            messageTextView = (TextView) itemView.findViewById(R.id.message);
        }
    }
}
