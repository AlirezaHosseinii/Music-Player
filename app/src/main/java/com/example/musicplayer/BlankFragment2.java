package com.example.musicplayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BlankFragment2 extends Fragment {

    public BlankFragment2() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank2, container, false);
        System.out.println("BlankFragment2"+ "onCreateView called");
        return view;
    }
}