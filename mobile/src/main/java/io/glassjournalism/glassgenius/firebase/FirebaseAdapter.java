package io.glassjournalism.glassgenius.firebase;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
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
        if (cards.size() == 1) {
            return 0;
        } else {
            return cards.size();
        }
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
        holder.cardHistoryTrigger.setText(card.getTriggers().toString());
        String cardImageURL = Constants.API_ROOT + "/card/render/" + card.getId();
        Log.d(TAG, cardImageURL);
        Picasso.with(mActivity).load(cardImageURL).into(holder.cardImage);
        return view;
    }

    static class ViewHolder {
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