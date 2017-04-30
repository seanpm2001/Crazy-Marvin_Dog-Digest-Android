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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.frangor.android.Controller;
import info.frangor.android.model.CheckPoint;
import info.frangor.android.R;

import java.lang.String;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * CheckPoint item view.
 */
public class CheckPointItem
    extends RecyclerView.Adapter<CheckPointItem.ViewHolder> {

    private Controller con;
    private Context context;
    private int petId;
    private List<CheckPoint> checkpoints;

    public CheckPointItem(Controller con, int petId) {
        this.con = con;
        this.petId = petId;
        this.checkpoints = new ArrayList<CheckPoint>();
    }

    @Override
    public CheckPointItem.ViewHolder onCreateViewHolder(ViewGroup parent,
            int viewType) {
        // Create a new checkpoint view.
        context = parent.getContext();
        View checkPointView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.checkpoint_item, parent, false);

        // Create ViewHolder.
        ViewHolder viewHolder = new ViewHolder(checkPointView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        DateFormat dateFormat = android.text.format.DateFormat
            .getDateFormat(con);
        int igrams = checkpoints.get(position).getGrams();
        String grams = con.getResources().getQuantityString(
                R.plurals.grams, igrams, igrams);
        String date = dateFormat.format(checkpoints.get(position).getDate());

        viewHolder.txtViewGrams.setText(grams);
        viewHolder.txtViewDate.setText(date);
    }

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnLongClickListener {

        public TextView txtViewGrams;
        public TextView txtViewDate;
        public View view;

        public ViewHolder(View v) {
            super(v);
            this.view = v;
            txtViewGrams = (TextView) v.findViewById(R.id.checkpoint_grams);
            txtViewDate = (TextView) v.findViewById(R.id.checkpoint_date);
            v.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            showAddCheckPointDialog(v, getAdapterPosition());
            return true;
        }

    }

    @Override
    public int getItemCount() {
        return checkpoints.size();
    }

    public CheckPoint getCheckPoint(int position) {
        return checkpoints.get(position);
    }

    /**
     * Long Click checkpoint item.
     */
    public void showAddCheckPointDialog(View view, int position) {
        final AppCompatActivity activity = (AppCompatActivity) context;
        CheckPointForm addCheckPointDialog = CheckPointForm
            .newInstance(petId, position);
        addCheckPointDialog.show(activity.getSupportFragmentManager(),
                "checkpointform");
    }

    public void update() {
        this.checkpoints = con.getCheckPoints(petId);
        this.notifyDataSetChanged();
    }
}
