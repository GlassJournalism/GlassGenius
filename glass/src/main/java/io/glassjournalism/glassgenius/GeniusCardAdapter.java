package io.glassjournalism.glassgenius;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.glass.widget.CardScrollAdapter;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import io.glassjournalism.glassgenius.data.json.Constants;
import io.glassjournalism.glassgenius.data.json.GlassGeniusAPI;
import retrofit.RestAdapter;

public class GeniusCardAdapter extends CardScrollAdapter {

    private final String TAG = getClass().getName();
    private List<String> cardIDList = new ArrayList<String>();
    private GlassGeniusAPI glassGeniusAPI;
    private Activity mActivity;
//    private Picasso mPicasso;

    public GeniusCardAdapter(Activity activity) {
        this.mActivity = activity;
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Constants.API_ROOT).build();
        glassGeniusAPI = restAdapter.create(GlassGeniusAPI.class);
//        Picasso.Builder builder = new Picasso.Builder(mActivity);
//        builder.listener(new Picasso.Listener() {
//            @Override
//            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
//                Log.d("Picasso Failure", uri.toString() + " " + exception.getMessage());
//            }
//        });
//        mPicasso = builder.build();
//        mPicasso.setIndicatorsEnabled(true);
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
            view = new ImageView(mActivity);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        String cardId = cardIDList.get(position);
        final String cardImageURL = Constants.API_ROOT + "/card/render/" + cardId;
        Ion.with(holder.imageView).load(cardImageURL);
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
