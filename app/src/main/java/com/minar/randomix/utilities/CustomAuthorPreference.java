package com.minar.randomix.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.minar.randomix.R;
import com.minar.randomix.activities.MainActivity;

public class CustomAuthorPreference extends Preference implements View.OnClickListener {
    // Easter egg stuff, why not
    private int easterEgg = 0;

    public CustomAuthorPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        View v = holder.itemView;

        // Make the icons clickable
        ImageView logo = v.findViewById(R.id.imageMinar);
        ImageView l1 = v.findViewById(R.id.minarig);
        ImageView l2 = v.findViewById(R.id.minartt);
        ImageView l3 = v.findViewById(R.id.minarps);
        ImageView l4 = v.findViewById(R.id.minargit);
        ImageView l5 = v.findViewById(R.id.minarsite);
        logo.setOnClickListener(this);
        l1.setOnClickListener(this);
        l2.setOnClickListener(this);
        l3.setOnClickListener(this);
        l4.setOnClickListener(this);
        l5.setOnClickListener(this);
        Drawable logoDrawable = logo.getDrawable();

        // Delay the execution of the logo animation a bit
        v.postDelayed(((Animatable) logoDrawable)::start, 200);
    }

    @Override
    public void onClick(View v) {
        // Vibrate and play sound using the common method in MainActivity
        Activity act = (Activity) getContext();
        Uri uri;
        int pressedId = v.getId();

        // Easter egg
        if (pressedId == R.id.imageMinar) {
            if (this.easterEgg == 3) {
                Toast.makeText(getContext(), getContext().getString(R.string.easter_egg), Toast.LENGTH_SHORT).show();
                this.easterEgg = 0;
                return;
            } else this.easterEgg++;
            return;
        }

        // Instagram
        if (pressedId == R.id.minarig) {
            if (act instanceof MainActivity) ((MainActivity) act).vibrate();
            uri = Uri.parse(getContext().getString(R.string.dev_instagram));
            Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent1);
            return;
        }

        // Twitter
        if (pressedId == R.id.minartt) {
            if (act instanceof MainActivity) ((MainActivity) act).vibrate();
            uri = Uri.parse(getContext().getString(R.string.dev_twitter));
            Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent2);
            return;
        }

        // Play Store other apps
        if (pressedId == R.id.minarps) {
            if (act instanceof MainActivity) ((MainActivity) act).vibrate();
            uri = Uri.parse(getContext().getString(R.string.dev_other_apps));
            Intent intent3 = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent3);
            return;
        }

        // Github
        if (pressedId == R.id.minargit) {
            if (act instanceof MainActivity) ((MainActivity) act).vibrate();
            uri = Uri.parse(getContext().getString(R.string.dev_github));
            Intent intent4 = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent4);
            return;
        }

        // Personal site
        if (pressedId == R.id.minarsite) {
            if (act instanceof MainActivity) ((MainActivity) act).vibrate();
            uri = Uri.parse(getContext().getString(R.string.dev_personal_site));
            Intent intent5 = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent5);
        }
    }
}
