package com.razikallayi.suraksha.account;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.razikallayi.suraksha.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountSummeryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountSummeryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountSummeryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AccountSummeryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountSummeryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountSummeryFragment newInstance(String param1, String param2) {
        AccountSummeryFragment fragment = new AccountSummeryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



//    private void drawSummery() {
//        final CardView cvDepositDue = cvSummeryAMF;
//        if (mMember.hasDepositDue(getContext())) {
//            TextView lblSummeryAMF = (TextView) cvDepositDue.findViewById(R.id.lblSummeryAMF);
//            //Get Next deposit Month
//            final Calendar nextDepositMonth = mMember.getNextDepositMonthCalendar(getContext());
//            String summeryPendingMonths = CalendarUtils.readableDepositMonth(nextDepositMonth);
//            Calendar calCurrentDate = Calendar.getInstance();
//            calCurrentDate.setTimeInMillis(CalendarUtils.normalizeDate(System.currentTimeMillis()));
//            while (nextDepositMonth.getTimeInMillis() < calCurrentDate.getTimeInMillis()){
//                summeryPendingMonths += CalendarUtils.readableDepositMonth(nextDepositMonth);
//                nextDepositMonth.add(Calendar.MONTH,1);
//            }
//            lblSummeryAMF.setText(summeryPendingMonths);
////
////            final String depositAMount = Utility.formatAmountInRupees(getContext(), Utility.getMonthlyDepositAmount());
////            ((TextView) cvDepositDue.findViewById(R.id.lblDepositAmountAMF)).setText(depositAMount);
////            cvDepositDue.findViewById(R.id.cvDepositAMF).setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
//////                    final String remarks = ((EditText) cvDepositDue.findViewById(R.id.txtRemarksDepositDue)).getText().toString();
////                    final View remarksView = mInflater.inflate(R.layout.remarks_dialog_content, null);
////                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
////                    builder.setMessage("Deposit " + depositAMount + " for " + mMember.getName() + " in account "
////                            + mMember.getAccountNo());
////                    builder.setView(remarksView);
////                    builder.setPositiveButton("Deposit", new DialogInterface.OnClickListener() {
////                        @Override
////                        public void onClick(DialogInterface dialog, int which) {
////                            final String strNarration = String.valueOf(
////                                    ((EditText) (remarksView.findViewById(R.id.txtRemarks))).getText());
////                            makeDeposit(nextDepositMonth.getTimeInMillis(), strNarration);
////                        }
////                    });
////                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
////                        @Override
////                        public void onClick(DialogInterface dialog, int which) {
////                            dialog.cancel();
////                        }
////                    });
////                    builder.show();
////                }
////            });
//        }
//    }
//

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.account_summery_fragment, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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
        void onFragmentInteraction(Uri uri);
    }
}
