package io.glassjournalism.glassgenius.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.percolate.caffeine.MiscUtils;
import com.percolate.caffeine.ViewUtils;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.glassjournalism.glassgenius.R;


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

        addSampleCards();

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

    public void addSampleCards() {

        for (int i = 0; i < 10; i++) {
            View newCard = inflater.inflate(R.layout.card_preview_card, null);

            final ImageView iv = ViewUtils.findViewById(newCard, R.id.cardImage);

            FrameLayout frameLayout = ViewUtils.findViewById(newCard, R.id.cardImageLayout);

            int x = getResources().getDisplayMetrics().widthPixels - MiscUtils.dpToPx(getActivity(), 32);
            int y = x * 9;
            y = (int) (((float) y)/16.0f);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(x, y);

            frameLayout.setLayoutParams(lp);

            final WebView wv = new WebView(getActivity());

            wv.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    if (progress == 100) {
                        new Background(wv, iv).execute();
                        Log.i(getTag(), "WebView done loading card.");
                    }
                }
            });

            wv.loadUrl("http://vinnie.io/sample-card.html");

            scrollViewLinearLayout.addView(newCard);
        }
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

        public Background(WebView webView, ImageView imageView) {
            this.imageView = imageView;
            this.webView = webView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
                Bitmap bitmap = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
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
