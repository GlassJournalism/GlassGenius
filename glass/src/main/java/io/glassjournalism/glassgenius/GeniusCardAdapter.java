package io.glassjournalism.glassgenius;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import com.google.android.glass.widget.CardScrollAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.glassjournalism.glassgenius.data.json.Constants;
import io.glassjournalism.glassgenius.data.json.GeniusCard;
import io.glassjournalism.glassgenius.data.json.GlassGeniusAPI;
import retrofit.RestAdapter;

public class GeniusCardAdapter extends CardScrollAdapter {

    private final String TAG = getClass().getName();
    private List<String> cardIdList = new ArrayList<String>();
    private GlassGeniusAPI glassGeniusAPI;
    private Activity mActivity;

    public GeniusCardAdapter(Activity activity) {
        this.mActivity = activity;
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Constants.API_ROOT).build();
        glassGeniusAPI = restAdapter.create(GlassGeniusAPI.class);
    }

    public void addCard(String cardId) {
        Log.d(TAG, "added: " + cardId);
        cardIdList.add(0, cardId);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cardIdList.size();
    }

    @Override
    public Object getItem(int position) {
        return cardIdList.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = new ImageView(mActivity);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        String cardId = cardIdList.get(position);
        String cardImageURL = Constants.API_ROOT + "/card/render/" + cardId;
        Picasso.with(mActivity).load(cardImageURL).into(holder.imageView);
//        WebView webView = new WebView(mActivity);
//        webView.setInitialScale(100);
//        webView.setBackgroundColor(Color.TRANSPARENT);
//        String url = Constants.API_ROOT + "/card/preview/" + cardId;
//        Log.d(TAG, url);
//        webView.loadUrl(url);
//        return webView;
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
