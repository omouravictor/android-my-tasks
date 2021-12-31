package com.example.tasks;

import android.view.View;

/* This interface allows you to add the TaskAdapter's onClickListeners from where it's called using the "position" parameter
from the onBindViewHolder function in TaskAdapter, instead of adding directly inside the onBindViewHolder.
It was created that way to make better use of ActivityResultLauncher in MainActivity */
public interface AdapterInterface {
    View.OnClickListener getOnClickListener(int position);

    View.OnClickListener getBtnCompleteOnClickListener(int position);
}
