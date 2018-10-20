package edu.unf.alloway.happybrain.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.unf.alloway.happybrain.R;

/**
 * Created by rojas on 2/27/2018.
 */

public class HomeFragment extends Fragment {

    View theView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        theView = inflater.inflate(R.layout.home_screen,container,false);

        return theView;
    }
}
