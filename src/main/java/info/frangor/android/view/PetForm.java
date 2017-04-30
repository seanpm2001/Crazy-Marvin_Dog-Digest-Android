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
import info.frangor.android.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * PetForm class.
 */
public class PetForm extends AppCompatDialogFragment {
    private static final String LOG_TAG = "laicare_petForm";
    private AppCompatEditText petName = null;
    private AppCompatEditText petBirthday = null;
    private AppCompatSpinner petSpecies = null;

    public PetForm() {
    }

    public static PetForm newInstance(int position) {
        PetForm petform = new PetForm();
        Bundle args = new Bundle();
        args.putInt("position", position);
        petform.setArguments(args);
        return petform;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pet_form, container);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Controller con = (Controller) getActivity()
            .getApplicationContext();

        int position = getArguments().getInt("position");

        petName = (AppCompatEditText) view.findViewById(R.id.pet_name);

        // Calendar picker
        petBirthday = (AppCompatEditText) view.findViewById(R.id.pet_birthday);
        petBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        petSpecies = (AppCompatSpinner) view.findViewById(R.id.pet_species);

        // Delete button
        AppCompatButton delete = (AppCompatButton) view.findViewById(
                R.id.pet_form_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePet(v);
            }
        });


        // Cancel button
        AppCompatButton cancel = (AppCompatButton) view.findViewById(
                R.id.pet_form_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });

        // OK button
        AppCompatButton ok = (AppCompatButton) view.findViewById(
            R.id.pet_form_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPetOk(v);
            }
        });

        if (position<0) {
            getDialog().setTitle(getString(R.string.add_pet));
            delete.setVisibility(View.GONE);
        } else {
            DateFormat dateFormat = android.text.format.DateFormat
                .getDateFormat(getActivity().getApplicationContext());
            String birthday = dateFormat.format(con.getPet(position)
                    .getBirthday());
            petBirthday.setText(birthday);
            getDialog().setTitle(getString(R.string.edit_pet));
            petName.setText(con.getPet(position).getName());
            petSpecies.setSelection(con.getPet(position).getSpecie().ordinal());
        }
    }

    /**
     * Show calendar picker on birthday field.
     */
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setEditText(petBirthday);
        newFragment.show(getActivity().getSupportFragmentManager(),
                "datePicker");
    }

    /**
     * Adding pet.
     */
    public void addPetOk(View v) {
        final Controller con = (Controller) getActivity()
            .getApplicationContext();

        int position = getArguments().getInt("position");

        if (petBirthday.getText().length()==0 ||
                petName.getText().length()==0) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this.getContext());
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
            DateFormat dateFormat = android.text.format.DateFormat
                .getDateFormat(getActivity().getApplicationContext());
            int petId = -1;
            String name = petName.getText().toString();
            Pet.Specie specie;
            Date birthday;

            if (position >= 0) {
                petId = con.getPet(position).getId();
            }

            if (petSpecies.getSelectedItem().toString().equals(getString(
                            R.string.DOG))) {
                    specie = Pet.Specie.DOG;
            } else {
                specie = Pet.Specie.CAT;
            }

            try {
                int initial = con.getPetListSize();
                birthday = dateFormat.parse(petBirthday.getText()
                        .toString());
                con.addPet(new Pet(petId, name, birthday, specie));
                PetList petList = (PetList) getActivity();
                petList.updatePetList();
                getDialog().dismiss();
                Toast.makeText(getActivity(), name +" "+
                        getString(R.string.added),
                        Toast.LENGTH_SHORT).show();
            } catch (ParseException | NoAddedItem e) {
                if (BuildConfig.DEBUG)
                    Log.e(LOG_TAG, e.getMessage());
                getDialog().cancel();
            }
        }
    }

    /**
     * Deleting pet.
     */
    public void deletePet(View v) {
        final Controller con = (Controller) getActivity()
            .getApplicationContext();

        int position = getArguments().getInt("position");

        int petId = con.getPet(position).getId();
        String name = con.getPet(position).getName();
        try {
            con.delPet(petId);
            PetList petList = (PetList) getActivity();
            petList.updatePetList();
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
