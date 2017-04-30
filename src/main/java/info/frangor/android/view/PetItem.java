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
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import info.frangor.android.Controller;
import info.frangor.android.R;

import java.lang.String;
import java.text.DateFormat;
import java.util.Date;

/**
 * Pet item view.
 */
public class PetItem extends RecyclerView.Adapter<PetItem.ViewHolder> {

    private static Controller con;
    private static Context context;

    public PetItem(Controller con) {
        this.con = con;
    }

    @Override
    public PetItem.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new pet view.
        context = parent.getContext();
        View petView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.pet_item, parent, false);

        // Create ViewHolder.
        ViewHolder viewHolder = new ViewHolder(petView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        String[] colors;
        int i = 0;

        String name = con.getPet(position).getName();

        DateFormat dateFormat = android.text.format.DateFormat
            .getDateFormat(con);
        Date today = new Date();
        Date birthday = con.getPet(position).getBirthday();
        String age = dateFormat.format(con.getPet(position).getBirthday());
        viewHolder.txtViewName.setText(name);

        long time = today.getTime()/1000 - birthday.getTime()/1000;
        int years = Math.round(time) / 31536000;
        int months = Math.round(time) / 2628000;
        int days = Math.round(time) / 86400;

        if (years > 0) {
            viewHolder.txtViewAge.setText(String.format("%s (%s)",
                        age, con.getResources()
                        .getQuantityString(R.plurals.years, years, years)));
        } else if (months > 0) {
            viewHolder.txtViewAge.setText(String.format("%s (%s)",
                        age, con.getResources()
                        .getQuantityString(R.plurals.months, months, months)));
        } else {
            viewHolder.txtViewAge.setText(String.format("%s (%s)",
                        age, con.getResources()
                        .getQuantityString(R.plurals.days, days, days)));
        }

        colors = con.getResources().getStringArray(R.array.box_colors);

        for (char c : name.toCharArray()) {
            i += (int) c;
        }
        i = i % colors.length;

        switch (con.getPet(position).getSpecie().name()) {
            case "DOG":
                viewHolder.imgSpecie.setImageResource(R.drawable.ic_dog_white);
                break;
            default:
                viewHolder.imgSpecie.setImageResource(R.drawable.ic_cat_white);
                break;
        }
        viewHolder.imgSpecie.setBackgroundColor(Color.parseColor(colors[i]));
    }

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener, View.OnClickListener {

        public TextView txtViewName;
        public TextView txtViewAge;
        public RoundedImageView imgSpecie;
        public View view;

        public ViewHolder(View v) {
            super(v);
            this.view = v;
            txtViewName = (TextView) v.findViewById(R.id.pet_name);
            txtViewAge = (TextView) v.findViewById(R.id.pet_age);
            imgSpecie = (RoundedImageView) v.findViewById(R.id.pet_icon);
            v.setOnLongClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            showAddPetDialog(v, getAdapterPosition());
            return true;
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Intent intent = new Intent(con, PetTabs.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("petId", con.getPet(position).getId());
            intent.putExtra("petName", con.getPet(position).getName());
            con.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        return con.getPetListSize();
    }

    /**
     * Long Click pet item.
     */
    public void showAddPetDialog(View view, int position) {
        final AppCompatActivity activity = (AppCompatActivity) context;
        PetForm addPetDialog = PetForm.newInstance(position);
        addPetDialog.show(activity.getSupportFragmentManager(), "petform");
    }
}
