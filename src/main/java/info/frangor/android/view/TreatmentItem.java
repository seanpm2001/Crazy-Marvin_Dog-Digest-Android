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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import info.frangor.android.Controller;
import info.frangor.android.R;

/**
 * Treatment item view.
 */
public class TreatmentItem
    extends RecyclerView.Adapter<TreatmentItem.ViewHolder> {

    private static Controller con;
    private static Context context;

    public TreatmentItem(Controller con) {
        this.con = con;
    }

    @Override
    public TreatmentItem.ViewHolder onCreateViewHolder(ViewGroup parent,
            int viewType) {
        // Create a new treatment view.
        context = parent.getContext();
        View appointmentView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.treatment_item, parent, false);

        // Create ViewHolder.
        ViewHolder viewHolder = new ViewHolder(appointmentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.txtViewName.setText(con.getTreatment(position).getName());
        if (con.getTreatment(position).getSpecie() != null) {
            switch (con.getTreatment(position).getSpecie().name()) {
                case "DOG":
                    viewHolder.txtViewSpecies.setText(R.string.dogs);
                    break;
                case "CAT":
                    viewHolder.txtViewSpecies.setText(R.string.cats);
                    break;
                default:
                    viewHolder.txtViewSpecies.setText(R.string.any);
                    break;
            }
        } else {
            viewHolder.txtViewSpecies.setText(R.string.any);
        }
        switch (con.getTreatment(position).getPlace().name()) {
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
            implements View.OnLongClickListener {

        public TextView txtViewName;
        public TextView txtViewSpecies;
        public RoundedImageView imgPlace;
        public View view;

        public ViewHolder(View v) {
            super(v);
            this.view = v;
            txtViewName = (TextView) v.findViewById(R.id.treatment_name);
            txtViewSpecies = (TextView) v.findViewById(R.id.treatment_species);
            imgPlace = (RoundedImageView) v.findViewById(R.id.place_icon);
            v.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            showAddTreatmentDialog(v, getAdapterPosition());
            return true;
        }
    }

    @Override
    public int getItemCount() {
        return con.getTreatmentListSize();
    }

    /**
     * Long Click treatment item.
     */
    public void showAddTreatmentDialog(View view, int position) {
        final AppCompatActivity activity = (AppCompatActivity) context;
        TreatmentForm addTreatmentDialog = TreatmentForm.newInstance(position);
        addTreatmentDialog.show(activity.getSupportFragmentManager(),
                "treatmentform");
    }
}
