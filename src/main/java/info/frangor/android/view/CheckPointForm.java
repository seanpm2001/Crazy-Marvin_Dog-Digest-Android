/*   This file is part of LaiCare.
 *
 *   LaiCare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   LaiCare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with My Expenses.  If not, see <http://www.gnu.org/licenses/>.
 */

package info.frangor.android.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import info.frangor.android.BuildConfig;
import info.frangor.android.Controller;
import info.frangor.android.exceptions.NoAddedItem;
import info.frangor.android.exceptions.NoDeletedItem;
import info.frangor.android.model.CheckPoint;
import info.frangor.android.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * CheckPointForm class.
 */
public class CheckPointForm extends AppCompatDialogFragment {
    private static final String LOG_TAG = "laicare_cpForm";
    private AppCompatEditText cpGrams = null;
    private AppCompatEditText cpDate = null;

    public CheckPointForm() {
    }

    public static CheckPointForm newInstance(int petId, int position) {
        CheckPointForm cpForm = new CheckPointForm();
        Bundle args = new Bundle();
        args.putInt("petId", petId);
        args.putInt("position", position);
        cpForm.setArguments(args);
        return cpForm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.checkpoint_form, container);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Controller con = (Controller) getActivity()
            .getApplicationContext();
        final PetTabs petTabs = (PetTabs) getActivity();

        int position = getArguments().getInt("position");

        cpGrams = (AppCompatEditText) view.findViewById(R.id.checkpoint_grams);

        // Calendar picker
        cpDate = (AppCompatEditText) view.findViewById(R.id.checkpoint_date);
        cpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        // Delete button
        AppCompatButton delete = (AppCompatButton) view.findViewById(
                R.id.checkpoint_form_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCheckPoint(v);
            }
        });


        // Cancel button
        AppCompatButton cancel = (AppCompatButton) view.findViewById(
                R.id.checkpoint_form_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        // OK button
        AppCompatButton ok = (AppCompatButton) view.findViewById(
             R.id.checkpoint_form_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCheckPointOk(v);
            }
        });

        if (position<0) {
            getDialog().setTitle(getString(R.string.add_checkpoint));
            DateFormat dateFormat = android.text.format.DateFormat
                .getDateFormat(getActivity().getApplicationContext());
            String date = dateFormat.format(new Date());
            cpDate.setText(date);
            delete.setVisibility(View.GONE);
        } else {
            cpGrams.setText(String.valueOf(petTabs.getCheckPoint(position)
                        .getGrams()));
            DateFormat dateFormat = android.text.format.DateFormat
                .getDateFormat(getActivity().getApplicationContext());
            String date = dateFormat.format(petTabs.getCheckPoint(position)
                        .getDate());
            cpDate.setText(date);
            getDialog().setTitle(getString(R.string.edit_checkpoint));
        }
    }

    /**
     * Show calendar picker on date field.
     */
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setEditText(cpDate);
        newFragment.show(getActivity().getSupportFragmentManager(),
                "datePicker");
    }

    /**
     * Adding CheckPoint.
     */
    public void addCheckPointOk(View v) {
        final Controller con = (Controller) getActivity()
            .getApplicationContext();

        int petId = getArguments().getInt("petId");
        int position = getArguments().getInt("position");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this.getContext());
            int checkPointId = -1;
            final PetTabs petTabs = (PetTabs) getActivity();
            if (position >= 0) {
                checkPointId = petTabs.getCheckPoint(position).getId();
            }
            DateFormat dateFormat = android.text.format.DateFormat
                .getDateFormat(getActivity().getApplicationContext());
            Date date;
            int grams;
            try {
                grams = Integer.parseInt(cpGrams.getText().toString());
                date = dateFormat.parse(cpDate.getText().toString());
                con.addCheckPoint(new CheckPoint(checkPointId,
                            grams, date, petId));
                Toast.makeText(getActivity(), getString(R.string
                            .checkpoint_added), Toast.LENGTH_SHORT).show();
                petTabs.updateCheckPointList();
                getDialog().dismiss();
            } catch (NoAddedItem e) {
                if (BuildConfig.DEBUG)
                    Log.e(LOG_TAG, e.getMessage());
                getDialog().cancel();
            } catch (ParseException | NumberFormatException e) {
                if (BuildConfig.DEBUG)
                    Log.e(LOG_TAG, e.getMessage());
                alertDialogBuilder.setTitle(getString(
                            android.R.string.dialog_alert_title))
                    .setMessage(getString(R.string.empty_field))
                    .setPositiveButton(getString(android.R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    dialog.dismiss();
                                }
                            });
                alertDialogBuilder.show();
                getDialog().cancel();
            }
    }

    /**
     * Deleting checkpoint.
     */
    public void deleteCheckPoint(View v) {
        final Controller con = (Controller) getActivity()
            .getApplicationContext();

        int position = getArguments().getInt("position");
        final PetTabs petTabs = (PetTabs) getActivity();
        int checkPointId = petTabs.getCheckPoint(position).getId();

        try {
            con.delCheckPoint(checkPointId);
            petTabs.updateCheckPointList();
            getDialog().dismiss();
            Toast.makeText(getActivity(), getString(R.string
                        .checkpoint_deleted), Toast.LENGTH_SHORT).show();
        } catch (NoDeletedItem e) {
            if (BuildConfig.DEBUG)
                Log.e(LOG_TAG, e.getMessage());
            getDialog().cancel();
        }
    }
}
