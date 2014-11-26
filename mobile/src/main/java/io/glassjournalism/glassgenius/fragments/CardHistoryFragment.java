package io.glassjournalism.glassgenius.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.InjectView;
import io.glassjournalism.glassgenius.R;
import io.glassjournalism.glassgenius.data.json.GeniusCard;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CardHistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CardHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardHistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @InjectView(R.id.cardScrollView)
    ScrollView cardScrollView;
    @InjectView(R.id.cardScrollLayout)
    LinearLayout cardScrollLayout;

    LayoutInflater layoutInflater;
    View rootView;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CardHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CardHistoryFragment newInstance(String param1, String param2) {
        CardHistoryFragment fragment = new CardHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CardHistoryFragment() {
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
        layoutInflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_card_history, container, false);

        return rootView;
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

    public void addCard(GeniusCard geniusCard) {
        View newCard = layoutInflater.inflate(R.layout.card_history_card, null);
        TextView title = (TextView) newCard.findViewById(R.id.cardTitle);
        TextView triggerWord = (TextView) newCard.findViewById(R.id.cardHistoryTrigger);
        ImageView cardImage = (ImageView) newCard.findViewById(R.id.cardImage);

        title.setText(geniusCard.getName());
        triggerWord.setText("Trigger phrase goes here");
        Picasso.with(getActivity()).load("http://glacial-ridge-6503.herokuapp.com/card/preview/" + geniusCard.getId()).into(cardImage);

        cardScrollLayout.addView(newCard);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
