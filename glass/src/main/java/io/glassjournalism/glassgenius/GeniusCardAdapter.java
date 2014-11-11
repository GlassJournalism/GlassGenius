package io.glassjournalism.glassgenius;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.glass.widget.CardScrollAdapter;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.glassjournalism.glassgenius.data.json.CardFoundResponse;
import io.glassjournalism.glassgenius.data.json.Constants;
import io.glassjournalism.glassgenius.data.json.GlassGeniusAPI;
import retrofit.RestAdapter;

public class GeniusCardAdapter extends CardScrollAdapter {

    private final String TAG = getClass().getName();
    private List<CardFoundResponse> cardList = new ArrayList<CardFoundResponse>();
    private GlassGeniusAPI glassGeniusAPI;
    private Activity mActivity;

    public GeniusCardAdapter(Activity activity) {
        this.mActivity = activity;
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Constants.API_ROOT).build();
        glassGeniusAPI = restAdapter.create(GlassGeniusAPI.class);
    }

    public void addCard(CardFoundResponse card) {
        Log.d(TAG, "added: " + card.getId());
        cardList.add(0, card);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cardList.size();
    }

    @Override
    public Object getItem(int position) {
        return cardList.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = View.inflate(mActivity, R.layout.card_layout, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        String cardId = cardList.get(position).getId();
        String cardImageURL = Constants.API_ROOT + "/card/render/" + cardId;
        Ion.with(holder.imageView).load(cardImageURL);
        holder.triggers.setText(TextUtils.join(", ", cardList.get(position).getTriggers()));
        return view;
    }

    @Override
    public int getPosition(Object o) {
        return 0;
    }

    static class ViewHolder {
        @InjectView(R.id.image)
        ImageView imageView;
        @InjectView(R.id.triggers)
        TextView triggers;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }
}
