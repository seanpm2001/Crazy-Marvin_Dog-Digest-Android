package info.frangor.android.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.DefaultItemAnimator;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;

import info.frangor.android.model.CheckPoint;
import info.frangor.android.view.utils.DividerItemDecoration;
import info.frangor.android.Controller;
import info.frangor.android.R;

public class CheckPointList extends Fragment {
    private static RecyclerView recyclerView;
    private static CheckPointItem checkPointAdapter;
    private static NestedScrollView scrollView;
    private static AppCompatTextView emptyView;
    private static GraphView graph;
    private static LineGraphSeries<DataPoint> series;

    public CheckPointList() {
    }

    public static CheckPointList newInstance(int petId) {
        CheckPointList f = new CheckPointList();
        Bundle args = new Bundle();
        args.putInt("petId", petId);
        f.setArguments(args);
        return f;
    }

    public int getPetId() {
        return getArguments().getInt("petId", 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.checkpoint_list, container, false);

        final Controller con = (Controller) getActivity()
            .getApplicationContext();

        recyclerView = (RecyclerView) v.findViewById(R.id.checkpoint_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        checkPointAdapter = new CheckPointItem(con, getPetId());
        recyclerView.setAdapter(checkPointAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                    null));

        emptyView = (AppCompatTextView) v.findViewById(R.id
                .empty_checkpoint_view);

        scrollView = (NestedScrollView) v.findViewById(R.id.scroll);

        graph = (GraphView) v.findViewById(R.id.graph);

        series = new LineGraphSeries<DataPoint>(generateData());
        graph.addSeries(series);
        series.setColor(ContextCompat.getColor(con, R.color.accent));
        graph.getGridLabelRenderer().setLabelFormatter(
                new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        setAxis();
        update();
        return v;
    }

    private DataPoint[] generateData() {
        DataPoint[] dataPoints = null;
        dataPoints = new DataPoint[checkPointAdapter.getItemCount()];
        for  (int i=0; i<checkPointAdapter.getItemCount(); i++) {
            CheckPoint cp = checkPointAdapter.getCheckPoint(i);
            dataPoints[i] = new DataPoint(cp.getDate(), cp.getGrams());
        }
        return dataPoints;
    }

    public void update() {
        checkPointAdapter.update();
        if (checkPointAdapter.getItemCount() == 0) {
            scrollView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            series.resetData(generateData());
            setAxis();
            scrollView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    public CheckPoint getCheckPoint(int position) {
        return checkPointAdapter.getCheckPoint(position);
    }

    private void setAxis() {
        if (checkPointAdapter.getItemCount() > 1) {
            graph.getViewport().setMinX(checkPointAdapter.getCheckPoint(0)
                    .getDate().getTime());
            graph.getViewport().setMaxX(checkPointAdapter.getCheckPoint(
                        checkPointAdapter.getItemCount() - 1)
                    .getDate().getTime());
            graph.getViewport().setXAxisBoundsManual(true);
            graph.setVisibility(View.VISIBLE);
        } else {
            graph.setVisibility(View.GONE);
        }
    }
}
