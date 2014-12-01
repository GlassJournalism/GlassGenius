package io.glassjournalism.glassgenius.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.faizmalkani.floatingactionbutton.FloatingActionButton;
import com.firebase.client.Firebase;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.glassjournalism.glassgenius.R;
import io.glassjournalism.glassgenius.data.json.Constants;
import io.glassjournalism.glassgenius.firebase.FirebaseAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CardHistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CardHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardHistoryFragment extends Fragment {

    private Firebase sessionRef;

    @InjectView(R.id.cardListView)
    ListView cardListView;
    @InjectView(R.id.sessionFab)
    FloatingActionButton sessionFab;

    LayoutInflater layoutInflater;
    SharedPreferences sharedPrefs;
    View rootView;

    @OnClick(R.id.sessionFab)
    public void onSessionFabClick(View v) {
        // Set an EditText view to get user input
        final EditText input = new EditText(getActivity());
        input.setText(sharedPrefs.getString("session", null));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Glass Genius Session")
                .setMessage("Enter a session code")
                .setView(input)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (input.getText().toString().length() == 5) {
                            String sessionID = input.getText().toString();
                            sharedPrefs.edit().putString("session", sessionID).apply();
                            dialogInterface.dismiss();
                            FirebaseAdapter adapter = new FirebaseAdapter(getActivity(), sessionRef.child(sessionID));
                            cardListView.invalidate();
                            cardListView.setAdapter(adapter);
                        } else {
                            input.setError("Enter a 5-digit session ID");
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        if (!getActivity().isFinishing()) {
            try {
                dialog.show();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }
    }

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CardHistoryFragment.
     */
    public static CardHistoryFragment newInstance(String param1, String param2) {
        CardHistoryFragment fragment = new CardHistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public CardHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layoutInflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_card_history, container, false);
        ButterKnife.inject(this, rootView);
        sessionFab.setDrawable(getResources().getDrawable(R.drawable.ic_action_new));
        sessionFab.setColor(getResources().getColor(R.color.pink_a400));
        sessionRef = new Firebase(Constants.FIREBASE_URL).child("sessions");
        sharedPrefs =  PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
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
