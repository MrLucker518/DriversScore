package cz.aspone.drivers_score.driversscore.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.aspone.drivers_score.driversscore.BO.Plate;
import cz.aspone.drivers_score.driversscore.DB.Loader;
import cz.aspone.drivers_score.driversscore.Helpers.General;
import cz.aspone.drivers_score.driversscore.Helpers.PlatesAdapter;
import cz.aspone.drivers_score.driversscore.Helpers.RenderHelper;
import cz.aspone.drivers_score.driversscore.R;

/**
 * Created by ondrej.vondra on 03.02.2018.
 */
public class PlateFragment extends Fragment {
    private View view;
    private SwipeRefreshLayout mSwipeRefresh;
    private RenderHelper helper;
    private PlatesAdapter adapter;
    RecyclerView platesView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.content_plates, container, false);

        setRecyclerView();
        return view;

    }

    //Setting recycler view
    private void setRecyclerView() {
        helper = new RenderHelper(getActivity());

        adapter = new PlatesAdapter(Loader.getPlates());

        platesView = (RecyclerView) view.findViewById(R.id.rvPlates);
        platesView.setHasFixedSize(false);
        platesView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.setOnItemClickListener(new PlatesAdapter.ClickListener() {
            @Override
            public void onItemClick(Plate plate, View v) {
                String sFinalString = General.formatSpeechOutput("Moje hodnocení je:", plate.getMyScore(), "Celkové hodnocení je:", plate.getScoreAvg(), getActivity());
                helper.getSpeechFromText(sFinalString);
            }

            @Override
            public void onItemLongClick(Plate plate, View v) {
                if (plate.getMyScore() != 0) {
                    Loader.deleteScore(plate.getPlateNumber());
                    helper.promptSpeechInput(plate);
                }
            }
        });

        platesView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mSwipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.mainRefresh);
                mSwipeRefresh.setEnabled(topRowVerticalPosition >= 0);
                mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorTextDark);
                mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshList();
                        mSwipeRefresh.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        platesView.setAdapter(adapter);// set adapter on RecyclerView
    }

    public void refreshList() {
        adapter.swap(Loader.getPlates());
    }
}