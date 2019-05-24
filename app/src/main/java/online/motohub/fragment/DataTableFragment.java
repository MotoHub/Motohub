/**
 * This is sample code provided by Wowza Media Systems, LLC.  All sample code is intended to be a reference for the
 * purpose of educating developers, and is not intended to be used in any production environment.
 * <p>
 * IN NO EVENT SHALL WOWZA MEDIA SYSTEMS, LLC BE LIABLE TO YOU OR ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL,
 * OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION,
 * EVEN IF WOWZA MEDIA SYSTEMS, LLC HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <p>
 * WOWZA MEDIA SYSTEMS, LLC SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. ALL CODE PROVIDED HEREUNDER IS PROVIDED "AS IS".
 * WOWZA MEDIA SYSTEMS, LLC HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * <p>
 * Copyright © 2015 Wowza Media Systems, LLC. All rights reserved.
 */

package online.motohub.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.wowza.gocoder.sdk.api.data.WZData;
import com.wowza.gocoder.sdk.api.data.WZDataItem;
import com.wowza.gocoder.sdk.api.data.WZDataList;
import com.wowza.gocoder.sdk.api.data.WZDataMap;
import com.wowza.gocoder.sdk.api.data.WZDataType;

import java.util.Arrays;

import online.motohub.R;

public class DataTableFragment extends Fragment {

    //private OnFragmentInteractionListener mListener;
    int mRestoreUIVisibility;

    int mRowNumber;

    public DataTableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of the fragment.
     */
    public static DataTableFragment newInstance(String tableTitle, WZDataMap tableData,
                                                boolean sortKeyNames, boolean annotateCollections) {

        DataTableFragment dataTableFragment = new DataTableFragment();

        Bundle args = new Bundle();
        args.putSerializable("tableData", tableData);
        args.putString("tableTitle", tableTitle);
        args.putBoolean("sortKeyNames", sortKeyNames);
        dataTableFragment.setArguments(args);

        return dataTableFragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of the fragment.
     */
    public static DataTableFragment newInstance(String tableTitle, WZDataList tableData,
                                                boolean sortKeyNames, boolean annotateCollections) {

        DataTableFragment dataTableFragment = new DataTableFragment();

        Bundle args = new Bundle();
        args.putSerializable("tableData", tableData);
        args.putString("tableTitle", tableTitle);
        args.putBoolean("sortKeyNames", sortKeyNames);
        args.putBoolean("annotateCollections", annotateCollections);
        dataTableFragment.setArguments(args);

        return dataTableFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void addDataItem(WZDataItem dataItem, TableLayout table, String label) {
        TableRow row = new TableRow(getActivity());
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(rowParams);

        TextView dataLabel = new TextView(getActivity());
        dataLabel.setText(label);
        dataLabel.setTextAppearance(getActivity(), R.style.DataTableLabelAppearance);
        dataLabel.setPadding(15, 15, 15, 15);
        TableRow.LayoutParams labelParams = new TableRow.LayoutParams(1);
        labelParams.weight = 1.0f;
        labelParams.gravity = Gravity.END;
        dataLabel.setLayoutParams(labelParams);

        TextView dataValue = new TextView(getActivity());
        dataValue.setText(dataItem.toString());
        dataValue.setTextAppearance(getActivity(), R.style.DataTableValueAppearance);
        dataValue.setPadding(15, 15, 15, 15);
        TableRow.LayoutParams valueParams = new TableRow.LayoutParams(2);
        valueParams.weight = 1.0f;
        dataValue.setLayoutParams(valueParams);

        row.addView(dataLabel);
        row.addView(dataValue);

        setRowBackground(row);
        table.addView(row);
    }

    private void addSubSectionHeader(TableLayout table, String label, boolean annotateCollections) {
        if (label != null) {
            TableRow row = new TableRow(getActivity());
            row.setBackgroundResource(R.color.dataTableSubHeaderBackground);
            TableRow.LayoutParams rowParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(rowParams);

            TextView dataLabel = new TextView(getActivity());
            dataLabel.setText(label);
            dataLabel.setTextAppearance(getActivity(), R.style.DataTableSubHeaderAppearance);
            dataLabel.setPadding(15, 15, 15, 15);

            //TableRow.LayoutParams labelParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TableRow.LayoutParams labelParams = new TableRow.LayoutParams(1);
            labelParams.weight = 1.0f;

            if (annotateCollections) {
                labelParams.gravity = Gravity.END;
            } else {
                labelParams.span = 2;
                labelParams.gravity = Gravity.CENTER;
            }

            dataLabel.setLayoutParams(labelParams);
            row.addView(dataLabel);

            if (annotateCollections) {
                TextView dataValue = new TextView(getActivity());
                dataValue.setText("{");
                dataValue.setTextAppearance(getActivity(), R.style.DataTableLabelAppearance);
                dataValue.setPadding(15, 15, 15, 15);
                TableRow.LayoutParams valueParams = new TableRow.LayoutParams(2);
                valueParams.weight = 1.0f;
                dataValue.setLayoutParams(valueParams);
                row.addView(dataValue);
            }

            setRowBackground(row);
            table.addView(row);
        }
    }

    private void setRowBackground(TableRow row) {
        row.setBackgroundResource(mRowNumber % 2 == 0 ? R.color.dataTableRowBackground1 : R.color.dataTableRowBackground2);
        mRowNumber++;
    }

    private void addSubSectionFooter(TableLayout table, String label) {
        TableRow row = new TableRow(getActivity());
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(rowParams);

        TextView dataLabel = new TextView(getActivity());
        //dataLabel.setText(label);
        //dataLabel.setTextAppearance(getActivity(), R.style.DataTableLabelAppearance);
        dataLabel.setPadding(15, 15, 15, 15);

        //TableRow.LayoutParams labelParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //TableRow.LayoutParams labelParams = new TableRow.LayoutParams(1);
        //labelParams.weight = 1.0f;
        //labelParams.gravity = Gravity.END;
        //dataLabel.setLayoutParams(labelParams);

        TextView dataValue = new TextView(getActivity());
        dataValue.setText("}" + (label != null ? " (" + label + ")" : ""));
        dataValue.setTextAppearance(getActivity(), R.style.DataTableLabelAppearance);
        dataValue.setPadding(15, 15, 15, 15);
        TableRow.LayoutParams valueParams = new TableRow.LayoutParams(2);
        valueParams.weight = 1.0f;
        dataValue.setLayoutParams(valueParams);

        row.addView(dataLabel);
        row.addView(dataValue);

        setRowBackground(row);
        table.addView(row);
    }

    private void buildTableRows(WZDataMap tableData, TableLayout table, boolean sortKeyNames, boolean annotateCollections) {
        String[] keyNames = (sortKeyNames ? tableData.keys().clone() : tableData.keys());
        if (sortKeyNames)
            Arrays.sort(keyNames);

        for (String key : keyNames) {
            WZDataType dataType = tableData.get(key).getDataType();
            switch (dataType) {
                case DATA_MAP:
                    WZDataMap dataMap = (WZDataMap) tableData.get(key);
                    addSubSectionHeader(table, key, annotateCollections);
                    buildTableRows(dataMap, table, sortKeyNames, annotateCollections);
                    if (annotateCollections)
                        addSubSectionFooter(table, key);
                    break;

                case DATA_LIST:
                    WZDataList dataList = (WZDataList) tableData.get(key);
                    buildTableRows(dataList, table, key, sortKeyNames, annotateCollections);
                    break;

                default:
                    WZDataItem dataItem = (WZDataItem) tableData.get(key);
                    addDataItem(dataItem, table, key);
                    break;
            }
        }
    }

    private void buildTableRows(WZDataList tableData, TableLayout table, String key, boolean sortKeyNames, boolean annotateCollections) {
        for (int rowNum = 0; rowNum < tableData.size(); rowNum++) {
            WZDataType dataType = tableData.get(rowNum).getDataType();

            switch (dataType) {
                case DATA_MAP:
                    WZDataMap dataMap = (WZDataMap) tableData.get(rowNum);
                    addSubSectionHeader(table, (key != null ? key : "") + "[" + rowNum + "]", annotateCollections);
                    buildTableRows(dataMap, table, sortKeyNames, annotateCollections);
                    if (annotateCollections)
                        addSubSectionFooter(table, (key != null ? key : "") + "[" + rowNum + "]");
                    break;

                case DATA_LIST:
                    WZDataList dataList = (WZDataList) tableData.get(rowNum);
                    buildTableRows(dataList, table, "[" + rowNum + "]", sortKeyNames, annotateCollections);
                    break;

                default:
                    WZDataItem dataItem = (WZDataItem) tableData.get(rowNum);
                    addDataItem(dataItem, table, "[" + rowNum + "]");
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RelativeLayout fragmentLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_datagrid, container, false);
        TableLayout dataTable = fragmentLayout.findViewById(R.id.tblDataTable);
        boolean sortKeyNames = getArguments().getBoolean("sortKeyNames");
        boolean annotateCollections = getArguments().getBoolean("annotateCollections");

        mRowNumber = 0;
        if (dataTable != null) {
            String tableTitleText = null;
            TextView tableTitle = fragmentLayout.findViewById(R.id.txtTableTitle);

            if (getArguments().containsKey("tableTitle")) {
                tableTitleText = getArguments().getString("tableTitle");
                if (tableTitleText != null && tableTitleText.trim().length() > 0)
                    tableTitle.setText(tableTitleText);
            } else if (tableTitleText == null)
                dataTable.removeView(tableTitle);

            if (getArguments().containsKey("tableData")) {
                WZData tableData = (WZData) getArguments().getSerializable("tableData");
                if (tableData != null) {
                    if (tableData.getDataType() == WZDataType.DATA_MAP)
                        buildTableRows((WZDataMap) tableData, dataTable, sortKeyNames, annotateCollections);
                    else if (tableData.getDataType() == WZDataType.DATA_LIST)
                        buildTableRows((WZDataList) tableData, dataTable, null, sortKeyNames, annotateCollections);
                }
            }

        }

        View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView != null) {
            mRestoreUIVisibility = rootView.getSystemUiVisibility();
            rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

        return fragmentLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView != null)
            rootView.setSystemUiVisibility(mRestoreUIVisibility);
        //mListener = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
/*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /*
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
/*
     public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
     }
*/
}
