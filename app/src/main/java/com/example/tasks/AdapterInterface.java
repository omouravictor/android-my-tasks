package com.example.tasks;

import android.view.View;

/* This interface allows you to add the onClickListener view item from where it is called, using "position"
parameter from the onBindViewHolder function in TaskAdapter, instead of add directly inside onBindViewHolder.
It was created that way to make better use of registerForActivityResult in MainActivity */
public interface AdapterInterface {
    View.OnClickListener getOnClickListener(int position);
}
