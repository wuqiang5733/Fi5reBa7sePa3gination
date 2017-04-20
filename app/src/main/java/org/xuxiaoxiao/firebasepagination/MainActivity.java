package org.xuxiaoxiao.firebasepagination;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
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
    private SwipeRefreshLayout swipeRefreshLayout;
    private int adjustVariable = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pageUp = (Button) findViewById(R.id.page_up);
        pageUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pageUp();
            }
        });
        pageDown = (Button) findViewById(R.id.page_down);
        pageDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lastkey = arrayList.get(arrayList.size() - 1).getMessageID();
                arrayList.clear();
//                Log.d("WQWQ", lastkey + "__lastKey");
//                Log.d("WQWQ", "+-+-+-+-+-+-+-+-+-+-+");
                query.removeEventListener(childEventListener);
                query = mWilddogRef.orderByKey().startAt(lastkey).limitToFirst(adjustVariable);
                query.addChildEventListener(childEventListener);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Log.d("MainActivity_", "onRefresh");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                            pageUp();

                        }
                    }, 1000);
                }
            });
            swipeRefreshLayout.setColorSchemeColors(
                    Color.parseColor("#FF00DDFF"),
                    Color.parseColor("#FF99CC00"),
                    Color.parseColor("#FFFFBB33"),
                    Color.parseColor("#FFFF4444")
            );
        }

        WilddogOptions wilddogOptions = new WilddogOptions.Builder().setSyncUrl("https://xuxiaoxiao1314.wilddogio.com").build();
        wilddogApp = WilddogApp.initializeApp(this, wilddogOptions);
        mWilddogRef = WilddogSync.getInstance().getReference().child("chat");
        query = mWilddogRef.limitToLast(adjustVariable);
        childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                ChatMessage chatMessage = (ChatMessage) snapshot.getValue(ChatMessage.class);
                String string = chatMessage.getMessageID();
//                Log.d("WQWQ", string);
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

    private void pageUp() {
        String lastkey = arrayList.get(0).getMessageID();
        arrayList.clear();
//                Log.d("WQWQ", lastkey + "__lastKey");
//                Log.d("WQWQ", "+-+-+-+-+-+-+-+-+-+-+");
        query.removeEventListener(childEventListener);
        query = mWilddogRef.orderByKey().endAt(lastkey).limitToLast(adjustVariable);
        query.addChildEventListener(childEventListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        Drawable divider = getResources().getDrawable(R.drawable.item_divider);

        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration(divider));
        contentAdapter = new ContentAdapter(arrayList);
        recyclerView.setAdapter(contentAdapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int ydy = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("WQWQ", "onScrollStateChanged : " + String.valueOf(newState));
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem, visibleItemCount, totalItemCount, lastCompletelyVisibleItemPosition;
                int offset = dy - ydy;
                ydy = dy;
//                Log.d("WQWQ", "offset : " + String.valueOf(offset));
//                Log.d("WQWQ", "recyclerView.getScrollState() : " + String.valueOf(recyclerView.getScrollState()));
                boolean shouldRefresh = (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0)
                        && (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING) && offset > 15;
                if (shouldRefresh) {
                    //swipeRefreshLayout.setRefreshing(true);
                    //Refresh to load data here.
                    Log.d("WQWQ", "上上");
                    return;
                }
                boolean shouldPullUpRefresh = linearLayoutManager.findLastCompletelyVisibleItemPosition() == linearLayoutManager.getChildCount() - 1
                        && recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING && offset < -30;
                if (shouldPullUpRefresh) {
                    //swipeRefreshLayout.setRefreshing(true);
                    //refresh to load data here.
                    Log.d("WQWQ", "下下");
                    return;
                }
                swipeRefreshLayout.setRefreshing(false);

                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                lastCompletelyVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
//                if ((visibleItemCount + firstVisibleItem) == totalItemCount){
                    Log.d("WQWQ", "lastCompletelyVisibleItemPosition : " + String.valueOf(lastCompletelyVisibleItemPosition));
//                }
            }
        });
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
            holder.messageId.setText(arrayList.get(position + 1).getMessageID());
//            Log.d("WQWQ",String.valueOf(position));
        }

        @Override
        public int getItemCount() {
//            Log.d("WQWQ", String.valueOf(arrayList.size() + "getItemCount"));
            return arrayList.size() - 1;
        }
    }

    private class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView messageId;

        public ContentViewHolder(View itemView) {
            super(itemView);
            messageTextView = (TextView) itemView.findViewById(R.id.message);
            messageId = (TextView) itemView.findViewById(R.id.id);
        }
    }
    /**
     * recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
     int ydy = 0;
     @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
     super.onScrollStateChanged(recyclerView, newState);

     }

     @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
     super.onScrolled(recyclerView, dx, dy);
     int offset = dy - ydy;
     ydy = dy;
     boolean shouldRefresh = (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0)
     && (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING) && offset > 30;
     if (shouldRefresh) {
     //swipeRefreshLayout.setRefreshing(true);
     //Refresh to load data here.
     return;
     }
     boolean shouldPullUpRefresh = linearLayoutManager.findLastCompletelyVisibleItemPosition() == linearLayoutManager.getChildCount() - 1
     && recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING && offset < -30;
     if (shouldPullUpRefresh) {
     //swipeRefreshLayout.setRefreshing(true);
     //refresh to load data here.
     return;
     }
     swipeRefreshLayout.setRefreshing(false);
     }
     });
     */
}
