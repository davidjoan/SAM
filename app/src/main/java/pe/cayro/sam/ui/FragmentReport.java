package pe.cayro.sam.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pe.cayro.sam.R;

/**
 * Created by David on 20/01/16.
 */
public class FragmentReport extends Fragment {

    public static FragmentReport newInstance() {
        Bundle args = new Bundle();

        FragmentReport fragment = new FragmentReport();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.reports);

        return view;
    }
}
