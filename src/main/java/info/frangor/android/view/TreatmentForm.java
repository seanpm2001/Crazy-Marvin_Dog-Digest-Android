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
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import info.frangor.android.BuildConfig;
import info.frangor.android.Controller;
import info.frangor.android.exceptions.NoAddedItem;
import info.frangor.android.exceptions.NoDeletedItem;
import info.frangor.android.model.Pet;
import info.frangor.android.model.Treatment;
import info.frangor.android.R;

import java.lang.Integer;

/**
 * TreatmentForm class.
 */
public class TreatmentForm extends AppCompatDialogFragment {
    private static final String LOG_TAG = "laicare_treatmentForm";
    private AppCompatEditText tName = null;
    private AppCompatEditText tPeriod = null;
    private AppCompatEditText tTerm = null;
    private AppCompatSpinner tPlace = null;
    private AppCompatSpinner tSpecies = null;

    public TreatmentForm() {
    }

    public static TreatmentForm newInstance(int position) {
        TreatmentForm tForm = new TreatmentForm();
        Bundle args = new Bundle();
        args.putInt("position", position);
        tForm.setArguments(args);
        return tForm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.treatment_form, container);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Controller con = (Controller) getActivity()
            .getApplicationContext();

        int position = getArguments().getInt("position");

        tName = (AppCompatEditText) view.findViewById(R.id.treatment_name);
        tPeriod = (AppCompatEditText) view.findViewById(R.id.treatment_period);
        tTerm = (AppCompatEditText) view.findViewById(R.id.treatment_term);
        tPlace = (AppCompatSpinner) view.findViewById(R.id.treatment_places);
        tSpecies = (AppCompatSpinner) view.findViewById(R.id
                .treatment_species);

        // Delete button
        AppCompatButton delete = (AppCompatButton) view.findViewById(
                R.id.treatment_form_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTreatment(v);
            }
        });


        // Cancel button
        AppCompatButton cancel = (AppCompatButton) view.findViewById(
                R.id.treatment_form_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        // OK button
        AppCompatButton ok = (AppCompatButton) view.findViewById(
            R.id.treatment_form_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTreatmentOk(v);
            }
        });

        if (position<0) {
            getDialog().setTitle(getString(R.string.add_treatment));
            delete.setVisibility(View.GONE);
        } else {
            getDialog().setTitle(getString(R.string.edit_treatment));
            tName.setText(con.getTreatment(position).getName());
            if (con.getTreatment(position).getSpecie() != null) {
                tSpecies.setSelection(con.getTreatment(position).getSpecie()
                        .ordinal());
            } else {
                tSpecies.setSelection(2);
            }
            tPlace.setSelection(con.getTreatment(position).getPlace()
                    .ordinal());
            tPeriod.setText(String.valueOf(con.getTreatment(position)
                        .getPeriod()));
            tTerm.setText(String.valueOf(con.getTreatment(position)
                        .getTerm()));
        }
    }

    /**
     * Adding treatment.
     */
    public void addTreatmentOk(View v) {
        final Controller con = (Controller) getActivity()
            .getApplicationContext();

        int position = getArguments().getInt("position");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this.getContext());

        if (tName.getText().length()==0 || tPeriod.getText().length()==0 ||
                tTerm.getText().length()==0 ) {
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
        } else {
            int tId = -1;
            String name = tName.getText().toString();
            int period;
            int term;
            Pet.Specie specie = null;

            if (position >= 0) {
                tId = con.getTreatment(position).getId();
            }

            if (tSpecies.getSelectedItem().toString().equals(getString(
                            R.string.DOG))) {
                    specie = Pet.Specie.DOG;
            } else if (tSpecies.getSelectedItem().toString().equals(getString(
                            R.string.CAT))) {
                specie = Pet.Specie.CAT;
            } else {
                specie = null;
            }

            Treatment.Place place = null;

            if (tPlace.getSelectedItem().toString().equals(getString(
                            R.string.HOME))) {
                place = Treatment.Place.HOME;
            } else if (tPlace.getSelectedItem().toString().equals(getString(
                            R.string.VET))) {
                place = Treatment.Place.VET;
            } else if (tPlace.getSelectedItem().toString().equals(getString(
                            R.string.TRAINING))) {
                place = Treatment.Place.TRAINING;
            } else {
                place = Treatment.Place.OTHERS;
            }

            try {
                period = Integer.parseInt(tPeriod.getText().toString());
                term = Integer.parseInt(tTerm.getText().toString());
                con.addTreatment(new Treatment(tId, name, period, term, place,
                            specie));
                TreatmentList tList = (TreatmentList) getActivity();
                tList.updateTreatmentList();
                getDialog().dismiss();
                Toast.makeText(getActivity(), name +" "+
                        getString(R.string.added),
                        Toast.LENGTH_SHORT).show();
            } catch (NoAddedItem e) {
                if (BuildConfig.DEBUG)
                    Log.e(LOG_TAG, e.getMessage());
                getDialog().cancel();
            } catch (NumberFormatException e) {
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
    }

    /**
     * Deleting treatment.
     */
    public void deleteTreatment(View v) {
        final Controller con = (Controller) getActivity()
            .getApplicationContext();

        int position = getArguments().getInt("position");

        int treatmentId = con.getTreatment(position).getId();
        String name = con.getTreatment(position).getName();

        try {
            con.delTreatment(treatmentId);
            TreatmentList treatmentList = (TreatmentList) getActivity();
            treatmentList.updateTreatmentList();
            getDialog().dismiss();
            Toast.makeText(getActivity(), name +" "+
                    getString(R.string.deleted), Toast.LENGTH_SHORT).show();
        } catch (NoDeletedItem e) {
            if (BuildConfig.DEBUG)
                Log.e(LOG_TAG, e.getMessage());
            getDialog().cancel();
        }
    }
}
