package io.glassjournalism.glassgenius;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.glassjournalism.glassgenius.data.json.Constants;
import io.glassjournalism.glassgenius.data.json.GlassGeniusAPI;
import retrofit.RestAdapter;

public class GeniusCardAdapter extends CardScrollAdapter {

    private final String TAG = getClass().getName();
    private List<String> cardIDList = new ArrayList<String>();
    private GlassGeniusAPI glassGeniusAPI;
    private Activity mActivity;

    public GeniusCardAdapter(Activity activity) {
        this.mActivity = activity;
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Constants.API_ROOT).build();
        glassGeniusAPI = restAdapter.create(GlassGeniusAPI.class);
    }

    public void addCard(String cardId) {
        Log.d(TAG, "added: " + cardId);
        cardIDList.add(0, cardId);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cardIDList.size();
    }

    @Override
    public Object getItem(int position) {
        return cardIDList.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = new View(mActivity);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        String cardId = cardIDList.get(position);
        String cardImageURL = Constants.API_ROOT + "/card/render/" + cardId;
        Picasso.with(mActivity).setIndicatorsEnabled(true);
        Picasso.with(mActivity).load(cardImageURL).into(holder.imageView);
        return view;
    }

    @Override
    public int getPosition(Object o) {
        return 0;
    }

    static class ViewHolder {
        ImageView imageView;

        public ViewHolder(View view) {
            imageView = (ImageView) view;
        }


    }
}
