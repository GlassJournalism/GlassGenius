package io.glassjournalism.glassgenius.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.faizmalkani.floatingactionbutton.FloatingActionButton;

import java.util.List;

import io.glassjournalism.glassgenius.R;
import io.glassjournalism.glassgenius.data.json.Category;
import io.glassjournalism.glassgenius.data.json.GlassGeniusAPI;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StoryListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StoryListFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class StoryListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private boolean hasStories = false;

    private OnFragmentInteractionListener mListener;

    private LayoutInflater layoutInflater;

    private View view;
    private ScrollView scrollView;
    private LinearLayout scrollViewLinearLayout;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StoryListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StoryListFragment newInstance(String param1, String param2) {
        StoryListFragment fragment = new StoryListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public StoryListFragment() {
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
        view = inflater.inflate(R.layout.fragment_story_list, container, false);

        // Bind views
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        scrollViewLinearLayout = (LinearLayout) view.findViewById(R.id.scrollViewLinearLayout);

        loadStories();

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

    public void addStory(String title) {

        if (!hasStories) {
            scrollView.setBackgroundColor(getResources().getColor(R.color.grey_0));
            hasStories = true;
        }

        View newStory = layoutInflater.inflate(R.layout.story_list_item, null);
        TextView storyTitle = (TextView) newStory.findViewById(R.id.storyTitle);

        storyTitle.setText(title);

        newStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, StoryFragment.newInstance("", ""), "StoryFragment").addToBackStack("StoryListFragment").commit();
            }
        });

        scrollViewLinearLayout.addView(newStory);
    }

    public void onFragmentInteraction(Uri uri) {

    }

    public void showNewStoryDialog() {
//        View dialogView = layoutInflater.inflate(R.layout.dialog_new_story, null);
//        final EditText inputTitle = ButterKnife.findById(dialogView, R.id.storyTitleEditText);
//        final EditText inputDescription = ButterKnife.findById(dialogView, R.id.storyDescriptionEditText);
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//
//        builder.setView(dialogView);
//        final CustomDialog newDialog = builder.build();
//        newDialog.setCustomView(dialogView)
//                .setClickListener(new CustomDialog.ClickListener() {
//                    @Override
//                    public void onConfirmClick() {
//                        if (inputTitle.getText().length() != 0 && inputDescription.getText().length() != 0) {
//                            addStory(inputTitle.getText().toString(), inputDescription.getText().toString());
//                            newDialog.dismiss();
//                        }
//                        else if (inputTitle.getText().length() == 0 && inputDescription.getText().length() != 0) {
//                            ToastUtils.quickToast(getActivity(), "Please enter a title.");
//                        }
//                        else if (inputTitle.getText().length() != 0 && inputDescription.getText().length() == 0) {
//                            ToastUtils.quickToast(getActivity(), "Please enter a description.");
//                        }
//                        else {
//                            ToastUtils.quickToast(getActivity(), "Please enter a title and description.");
//                        }
//                    }
//
//                    @Override
//                    public void onCancelClick() {
//                        //do nothing
//                    }
//                });
//        newDialog.show();
    }


    public void loadStories() {
        GlassGeniusAPI.GlassGeniusAPI.getCategories(new Callback<List<Category>>() {

            @Override
            public void success(List<Category> categories, Response response) {

                for (int i = 0; i < categories.size(); i++) {
                    addStory(categories.get(i).getName());
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

}
