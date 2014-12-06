package io.glassjournalism.glassgenius.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.faizmalkani.floatingactionbutton.FloatingActionButton;
import com.koushikdutta.ion.Ion;
import com.percolate.caffeine.MiscUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.glassjournalism.glassgenius.R;
import io.glassjournalism.glassgenius.data.json.Constants;
import io.glassjournalism.glassgenius.data.json.GeniusCard;
import io.glassjournalism.glassgenius.data.json.GlassGeniusAPI;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class StoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LayoutInflater inflater;
    private View view;

    @InjectView(R.id.scrollView) ScrollView scrollView;
    @InjectView(R.id.scrollViewLinearLayout) LinearLayout scrollViewLinearLayout;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StoryFragment newInstance(String param1, String param2) {
        StoryFragment fragment = new StoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public StoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        view = inflater.inflate(R.layout.fragment_story, container, false);

        ButterKnife.inject(this, view);

        loadCards();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void openNewGlassCardFragment() {
        GlassCardCreationFragment gccf = GlassCardCreationFragment.newInstance("", "");
        getFragmentManager().beginTransaction().replace(R.id.container, gccf).addToBackStack("StoryFragment").commit();
    }

//    public void addSampleCards() {
//
//        for (int i = 0; i < 10; i++) {
//            View newCard = inflater.inflate(R.layout.card_preview_card, null);
//
//            FrameLayout frameLayout = ViewUtils.findViewById(newCard, R.id.cardImageLayout);
//
//            int x = getResources().getDisplayMetrics().widthPixels - MiscUtils.dpToPx(getActivity(), 32);
//            int y = x * 9;
//            y = (int) (((float) y)/16.0f);
//
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(x, y);
//
//            frameLayout.setLayoutParams(lp);
//
//            final WebView wv = new WebView(getActivity());
//
//            wv.setWebChromeClient(new WebChromeClient() {
//                public void onProgressChanged(WebView view, int progress) {
//                    if (progress == 100) {
//                        new Background(wv, iv).execute();
//                        Log.i(getTag(), "WebView done loading card.");
//                    }
//                }
//            });
//
//            wv.loadUrl("http://vinnie.io/sample-card.html");
//
//            scrollViewLinearLayout.addView(newCard);
//        }
//    }

    public void addCard(final GeniusCard geniusCard) {
        View view = inflater.inflate(R.layout.card_preview_card, null);
        TextView title = (TextView) view.findViewById(R.id.cardTitle);

        ImageView imageView = (ImageView) view.findViewById(R.id.cardImageView);

        int width = getResources().getDisplayMetrics().widthPixels - MiscUtils.dpToPx(getActivity(), 32);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) ((9.f/16.f) * ((float) width)));

        TextView triggerWordTitle = (TextView) view.findViewById(R.id.triggerWordTitle);
        final ImageView triggerWordToggle = (ImageView) view.findViewById(R.id.triggerWordToggle);

        final LinearLayout triggerWordLinearLayout = (LinearLayout) view.findViewById(R.id.triggerWordLinearLayout);

        imageView.setLayoutParams(layoutParams);

        title.setText(geniusCard.getName());

        String mime = "text/html";
        String encoding = "utf-8";

        int wvWidth = getActivity().getResources().getDisplayMetrics().widthPixels - MiscUtils.dpToPx(getActivity(), 32);
        int wvHeight = (int) (9.f/16.f) * wvWidth;
        int scalePercent = (int) (((double) wvWidth)/640.f * 100);

        List<String> triggerWords = geniusCard.getTriggerWords();

        if (triggerWords.size() == 1 && triggerWords.get(0).equals("")) {
            triggerWordTitle.setText("No trigger words");
            triggerWordToggle.setVisibility(View.GONE);
        } else {
            triggerWordTitle.setText("Show " + triggerWords.size() + " trigger phrases");
            triggerWordToggle.setOnClickListener(new View.OnClickListener() {
                boolean isExpanded = false;

                @Override
                public void onClick(View view) {
                    if (!isExpanded) {
                        triggerWordLinearLayout.setVisibility(View.GONE);
                        triggerWordToggle.setRotation(0);
                    } else {
                        triggerWordLinearLayout.setVisibility(View.VISIBLE);
                        triggerWordToggle.setRotation(180);
                    }

                    isExpanded = !isExpanded;
                }
            });
        }

        for (int i = 0; i < triggerWords.size(); i++) {
            TextView textView = new TextView(getActivity());
            textView.setText(triggerWords.get(i));
            textView.setTextAppearance(getActivity(), R.style.TriggerWordTextStyle);

            triggerWordLinearLayout.addView(textView);
        }

        scrollViewLinearLayout.addView(view);

        Picasso.with(getActivity()).load(Constants.API_ROOT + "/card/render/" + geniusCard.getId()).into(imageView);

    }

    public void loadCards() {
        GlassGeniusAPI.GlassGeniusAPI.getCards(new Callback<List<GeniusCard>>() {

            @Override
            public void success(List<GeniusCard> geniusCards, Response response) {
                for (int i = 0; i < geniusCards.size(); i++) {
                    addCard(geniusCards.get(i));
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    class Background extends AsyncTask<Void, Void, Bitmap> {
        ImageView imageView;
        WebView webView;
        String url;

        public Background(WebView webView, ImageView imageView) {
            this.imageView = imageView;
            this.webView = webView;
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
                int width = getActivity().getResources().getDisplayMetrics().widthPixels - MiscUtils.dpToPx(getActivity(), 32);
                int height = (int) (9.f/16.f) * width;
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                webView.draw(canvas);
                return bitmap;
            } catch (Exception e) { }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

}
