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
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import rocks.poopjournal.dogdigest.android.BuildConfig;
import rocks.poopjournal.dogdigest.android.Controller;
import rocks.poopjournal.dogdigest.android.exceptions.NoAddedItem;
import rocks.poopjournal.dogdigest.android.exceptions.NoDeletedItem;
import rocks.poopjournal.dogdigest.android.model.Appointment;
import rocks.poopjournal.dogdigest.android.model.Treatment;
import rocks.poopjournal.dogdigest.android.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * AppointmentForm class.
 */
public class AppointmentForm extends AppCompatDialogFragment {
    private static final String LOG_TAG = "laicare_apForm";
    private ArrayAdapter<Treatment> treatments;
    private AppCompatSpinner apTreatment = null;
    private AppCompatEditText apDate = null;
    private AppCompatEditText apAttended = null;
    private TextInputLayout inAttended;

    public AppointmentForm() {
    }

    public static AppointmentForm newInstance(int petId, int position) {
        AppointmentForm apForm = new AppointmentForm();
        Bundle args = new Bundle();
        args.putInt("petId", petId);
        args.putInt("position", position);
        apForm.setArguments(args);
        return apForm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.appointment_form, container);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Controller con = (Controller) getActivity()
            .getApplicationContext();
        final PetTabs petTabs = (PetTabs) getActivity();

        int position = getArguments().getInt("position");
        int petId = getArguments().getInt("petId");

        treatments = new ArrayAdapter<Treatment>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                con.getTreatments(petId));

        apTreatment = (AppCompatSpinner) view.findViewById(R.id
                .appointment_treatments);

        apTreatment.setAdapter(treatments);

        // Calendar picker
        apDate = (AppCompatEditText) view.findViewById(R.id
                .appointment_first_date);
        apDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v, apDate);
            }
        });

        apAttended = (AppCompatEditText) view.findViewById(R.id
                .appointment_attended);
        apAttended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v, apAttended);
            }
        });

        inAttended = (TextInputLayout) view.findViewById(R.id.attended);

        // Delete button
        AppCompatButton delete = (AppCompatButton) view.findViewById(
                R.id.appointment_form_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAppointment(v);
            }
        });


        // Cancel button
        AppCompatButton cancel = (AppCompatButton) view.findViewById(
                R.id.appointment_form_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        // OK button
        AppCompatButton ok = (AppCompatButton) view.findViewById(
             R.id.appointment_form_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAppointmentOk(v);
            }
        });

        if (position<0) {
            getDialog().setTitle(getString(R.string.add_appointment));
            delete.setVisibility(View.GONE);
        } else {
            for (int p=0; p < treatments.getCount(); p++) {
                if(treatments.getItem(p).getId() == petTabs
                        .getAppointment(position).getTreatment().getId()) {
                    apTreatment.setSelection(p);
                }
            }
            DateFormat dateFormat = android.text.format.DateFormat
                .getDateFormat(getActivity().getApplicationContext());
            String date = dateFormat.format(petTabs.getAppointment(position)
                        .getFirstDay());
            apDate.setText(date);
            getDialog().setTitle(getString(R.string.edit_appointment));
            if (petTabs.getAppointment(position).getAttended() != null) {
                inAttended.setVisibility(View.VISIBLE);
                apAttended.setText((dateFormat.format(petTabs
                                .getAppointment(position).getAttended())));
            }
        }
    }

    /**
     * Show calendar picker on date field.
     */
    public void showDatePickerDialog(View v, AppCompatEditText editText) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setEditText(editText);
        newFragment.show(getActivity().getSupportFragmentManager(),
                "datePicker");
    }

    /**
     * Adding Appointment.
     */
    public void addAppointmentOk(View v) {
        final Controller con = (Controller) getActivity()
            .getApplicationContext();

        int petId = getArguments().getInt("petId");
        int position = getArguments().getInt("position");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this.getContext());
        int appointmentId = -1;
        final PetTabs petTabs = (PetTabs) getActivity();
        if (position >= 0) {
            appointmentId = petTabs.getAppointment(position).getId();
        }
        DateFormat dateFormat = android.text.format.DateFormat
            .getDateFormat(getActivity().getApplicationContext());
        Date firstDay, attended;
        Treatment treatment;
        try {
            treatment = (Treatment) apTreatment.getSelectedItem();
            firstDay = dateFormat.parse(apDate.getText().toString());
            if (apAttended.length() == 0) {
                con.addAppointment(new Appointment(appointmentId,
                            firstDay, treatment, petId));
            } else {
                attended = dateFormat.parse(apAttended.getText().toString());
                con.addAppointment(new Appointment(appointmentId,
                            firstDay, attended, treatment, petId));
            }
            Toast.makeText(getActivity(), getString(R.string
                        .appointment_added), Toast.LENGTH_SHORT).show();
            petTabs.updateAppointmentList();
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
        }
    }

    /**
     * Deleting appointment.
     */
    public void deleteAppointment(View v) {
        final Controller con = (Controller) getActivity()
            .getApplicationContext();

        int position = getArguments().getInt("position");
        final PetTabs petTabs = (PetTabs) getActivity();
        int appointmentId = petTabs.getAppointment(position).getId();

        try {
            con.delAppointment(appointmentId);
            petTabs.updateAppointmentList();
            getDialog().dismiss();
            Toast.makeText(getActivity(), getString(R.string
                        .appointment_deleted), Toast.LENGTH_SHORT).show();
        } catch (NoDeletedItem e) {
            if (BuildConfig.DEBUG)
                Log.e(LOG_TAG, e.getMessage());
            getDialog().cancel();
        }
    }
}
