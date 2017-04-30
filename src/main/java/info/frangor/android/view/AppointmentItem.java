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

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.util.Log;

import com.makeramen.roundedimageview.RoundedImageView;

import info.frangor.android.BuildConfig;
import info.frangor.android.Controller;
import info.frangor.android.exceptions.NoAddedItem;
import info.frangor.android.model.Appointment;
import info.frangor.android.R;

import java.lang.String;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Appointment item view.
 */
public class AppointmentItem
    extends RecyclerView.Adapter<AppointmentItem.ViewHolder> {

    private static Controller con;
    private static Context context;
    private static int petId;
    private static List<Appointment> appointments;

    private static final String LOG_TAG = "laicare_apItem";

    public AppointmentItem(Controller con, int petId) {
        this.con = con;
        this.petId = petId;
        this.appointments = new ArrayList<Appointment>();
    }

    @Override
    public AppointmentItem.ViewHolder onCreateViewHolder(ViewGroup parent,
            int viewType) {
        // Create a new checkpoint view.
        context = parent.getContext();
        View appointmentView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.appointment_item, parent, false);

        // Create ViewHolder.
        ViewHolder viewHolder = new ViewHolder(appointmentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        DateFormat dateFormat = android.text.format.DateFormat
            .getDateFormat(con);
        String name = appointments.get(position).getTreatment().getName();
        String date = dateFormat.format(appointments.get(position).nextDate());

        viewHolder.txtViewTreatment.setText(name);
        viewHolder.txtViewDate.setText(date);

        if ( appointments.get(position).isCovered() ) {
            viewHolder.imgInfo.setImageDrawable(ContextCompat
                    .getDrawable(con,
                        R.drawable.ic_assignment_turned_in_black));
            viewHolder.imgInfo.setColorFilter(ContextCompat.getColor(con,
                        R.color.green));
        } else {
            viewHolder.imgInfo.setImageDrawable(ContextCompat
                    .getDrawable(con,
                        R.drawable.ic_assignment_late_black));
            viewHolder.imgInfo.setColorFilter(ContextCompat.getColor(con,
                        R.color.red));

        }

        switch (appointments.get(position).getTreatment().getPlace().name()) {
            case "HOME":
                viewHolder.imgPlace.setImageResource(R.drawable.ic_home_white);
                viewHolder.imgPlace.setBackgroundColor(ContextCompat.getColor(
                            con, R.color.home_background));
                break;
            case "VET":
                viewHolder.imgPlace.setImageResource(R.drawable
                        .ic_local_hospital_white);
                viewHolder.imgPlace.setBackgroundColor(ContextCompat.getColor(
                            con, R.color.vet_background));
                break;
            case "TRAINING":
                viewHolder.imgPlace.setImageResource(R.drawable
                        .ic_school_white);
                viewHolder.imgPlace.setBackgroundColor(ContextCompat.getColor(
                            con, R.color.training_background));
                break;
            default:
                viewHolder.imgPlace.setImageResource(R.drawable
                        .ic_place_white);
                viewHolder.imgPlace.setBackgroundColor(ContextCompat.getColor(
                            con, R.color.icon_background));
                break;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener, View.OnClickListener {

        public TextView txtViewTreatment;
        public TextView txtViewDate;
        public ImageView imgInfo;
        public RoundedImageView imgPlace;
        public View view;

        public ViewHolder(View v) {
            super(v);
            this.view = v;
            txtViewTreatment = (TextView) v.findViewById(R.id.treatment_name);
            txtViewDate = (TextView) v.findViewById(R.id.appointment_date);
            imgPlace = (RoundedImageView) v.findViewById(R.id.place_icon);
            imgInfo = (ImageView) v.findViewById(R.id.appointment_info);
            v.setOnLongClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            showAddAppointmentDialog(v, getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View v) {
            AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
                .create();
            alertDialog.setMessage(con.getString(R.string.mark_as_attended));
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
                    con.getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                int which) {
                            Appointment myap = appointments
                                .get(getAdapterPosition());
                            myap.setAttended(new Date());
                            try {
                                con.addAppointment(myap);
                                update();
                            } catch (NoAddedItem e) {
                                if (BuildConfig.DEBUG)
                                    Log.e(LOG_TAG, e.getMessage());
                            }
                            dialog.dismiss();
                        }
            });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                    con.getString(android.R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
            });
            alertDialog.show();
        }
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public Appointment getAppointment(int position) {
        return appointments.get(position);
    }

    /**
     * Long Click checkpoint item.
     */
    public void showAddAppointmentDialog(View view, int position) {
        final AppCompatActivity activity = (AppCompatActivity) context;
        AppointmentForm addAppointmentDialog = AppointmentForm
            .newInstance(petId, position);
        addAppointmentDialog.show(activity.getSupportFragmentManager(),
                "appointmentform");
    }

    public void update() {
        this.appointments = con.getAppointments(petId);
        Collections.sort(appointments);
        this.notifyDataSetChanged();
    }
}
