package com.example.tasks;

import android.view.View;

/* This interface allows you to add onClickListeners view item from where it is called, using "position"
parameter from the onBindViewHolder function in TaskAdapter, instead of add directly inside onBindViewHolder.
It was created that way to make better use of registerForActivityResult in MainActivity for Adapter ItemView OnClickListener*/
public interface AdapterInterface {
    View.OnClickListener getOnClickListener(int position);

    View.OnClickListener getBtnCompleteOnClickListener(int position);
}
