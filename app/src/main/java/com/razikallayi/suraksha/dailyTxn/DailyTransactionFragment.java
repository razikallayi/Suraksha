package com.razikallayi.suraksha.dailyTxn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.razikallayi.suraksha.utils.CalendarUtils;
import com.razikallayi.suraksha.R;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailyTransactionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyTransactionFragment extends Fragment{


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_DATE = "date";

    private DailyTransactionAdapter mDailyTxnAdapter;

    private long mDate;
    //private OnFragmentInteractionListener mListener;

    public DailyTransactionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param date Parameter 1.
     * @return A new instance of fragment DailyTransactionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DailyTransactionFragment newInstance(long date) {
        DailyTransactionFragment fragment = new DailyTransactionFragment();
        Log.d("FISH", "new Instance Fragment");
        Log.d("FISH", "new Instance Fragment"+date);
        Bundle args = new Bundle();
        args.putLong(ARG_DATE, date);
        fragment.setArguments(args);
        Log.d("FISH", "newInstance: " + fragment.getArguments().getLong(ARG_DATE));
        Log.d("FISH", "newInstance: "+fragment.getArguments().toString());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("FISH", "OnCreate Fragment"+this.getClass().getSimpleName());
        Log.d("FISH", "OnCreate Fragment"+this.getArguments());

//        Activity activity = this.getActivity();
//        CollapsingToolbarLayout appBarLayout =
//                (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
//        if (appBarLayout != null) {
//            Log.d("fish", "appbar layout Fragment");
//            appBarLayout.setTitle("Details");
//        }

        if (getArguments() != null) {
            Log.d("fish", "has arguments");
            Log.d("fish", "has arguments"+getArguments().getLong(ARG_DATE));
            mDate = getArguments().getLong(ARG_DATE);
        }
        Log.d("fish", "Oncreate end Fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_daily_transaction, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.txnDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mDate);
        Log.d("FISH", "onCreateView Date:  "+mDate);
        Log.d("FISH", "onCreateView: "+calendar.getTimeInMillis());
        textView.setText(CalendarUtils.formatDate(calendar.getTimeInMillis()));

            return rootView;
    }
/*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/
/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
*/


/*
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
*/
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
 /*   public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
*/
}
