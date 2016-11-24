package com.johan.subuwuliu.fragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class AppFragment extends Fragment {
	
	protected View layout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		layout = inflater.inflate(getContentView(), container, false);
		findId();
		init();
		return layout;
	}
	
	public abstract int getContentView();
	
	public abstract void findId();
	
	public abstract void init();
	
}
