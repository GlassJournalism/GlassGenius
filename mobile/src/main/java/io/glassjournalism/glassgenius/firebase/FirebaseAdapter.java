package io.glassjournalism.glassgenius.firebase;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.percolate.caffeine.MiscUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.glassjournalism.glassgenius.R;
import io.glassjournalism.glassgenius.data.json.CardFoundResponse;
import io.glassjournalism.glassgenius.data.json.Constants;

public class FirebaseAdapter extends BaseAdapter {

    private final String TAG = getClass().getName();
    protected Query ref;
    private List<CardFoundResponse> cards;
    private Map<String, CardFoundResponse> cardNames;
    private ChildEventListener listener;

    private Activity mActivity;

    public FirebaseAdapter(Activity activity, Firebase firebase) {
        mActivity = activity;
        this.ref = firebase;
        cards = new ArrayList<CardFoundResponse>();
        cardNames = new HashMap<String, CardFoundResponse>();

        // Look for all child events. We will then map them to our own internal ArrayList, which backs ListView
        listener = this.ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded");

                CardFoundResponse card = dataSnapshot.getValue(CardFoundResponse.class);
                cardNames.put(dataSnapshot.getKey(), card);

                // Insert into the correct location, based on previousChildName
                if (previousChildName == null) {
                    cards.add(0, card);
                } else {
                    CardFoundResponse previousCard = cardNames.get(previousChildName);
                    int previousIndex = cards.indexOf(previousCard);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == cards.size()) {
                        cards.add(card);
                    } else {
                        cards.add(nextIndex, card);
                    }
                }
                notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved");
                // A song was removed from the list. Remove it from our list and the name mapping
                String cardName = dataSnapshot.getKey();
                CardFoundResponse oldCard = cardNames.get(cardName);
                if (cards.contains(oldCard)) {
                    cards.remove(oldCard);
                    notifyDataSetChanged();
                }
                if (cardNames.containsKey(cardName)) {
                    cardNames.remove(cardName);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, "Listen was cancelled, no more updates will occur");
            }

        });
    }

    public void cleanup() {
        // We're being destroyed, let go of our listener and forget about all of the cards
        ref.removeEventListener(listener);
        cards.clear();
        cardNames.clear();
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public CardFoundResponse getItem(int i) {
        return cards.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (null != view) {
            holder = (ViewHolder) view.getTag();
        }
        if (null == holder) {
            view = View.inflate(mActivity, R.layout.card_history_card, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        CardFoundResponse card = cards.get(position);
        holder.cardHistoryTrigger.setText(TextUtils.join(", ", card.getTriggers()));
        holder.cardTitle.setText(card.getName());
        String cardImageURL = Constants.API_ROOT + "/card/render/" + card.getId();
        Log.d(TAG, cardImageURL);
        Picasso.with(mActivity).load(cardImageURL).into(holder.cardImage);

        int width = mActivity.getResources().getDisplayMetrics().widthPixels;
        int widthPicture = width - MiscUtils.dpToPx(mActivity, 16);
        int heightPicture = (int) ((widthPicture/16.0f) * 9.0f);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(widthPicture, heightPicture);

        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = MiscUtils.dpToPx(mActivity, 8);
        lp.rightMargin = lp.leftMargin;
        lp.topMargin = lp.leftMargin/2;
        lp.bottomMargin = lp.topMargin;

        view.setLayoutParams(lp);

        holder.cardImage.setLayoutParams(lp2);
        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.card)
        CardView card;
        @InjectView(R.id.cardTitle)
        TextView cardTitle;
        @InjectView(R.id.cardHistoryTrigger)
        TextView cardHistoryTrigger;
        @InjectView(R.id.cardImage)
        ImageView cardImage;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}