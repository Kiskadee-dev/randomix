package com.minar.randomix.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.minar.randomix.activities.MainActivity;
import com.minar.randomix.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RouletteFragment extends androidx.fragment.app.Fragment implements OnClickListener, View.OnLongClickListener, TextView.OnEditorActionListener {
    private final List<String> options = new ArrayList<>();
    private final RouletteBottomSheet bottomSheet = new RouletteBottomSheet(this);
    private SharedPreferences sp = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_roulette, container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        // Hide description if needed, and save as last opened page
        sp.edit().putString("last_page", "roulette").apply();
        if (sp.getBoolean("hide_descriptions", false))
            v.findViewById(R.id.descriptionRoulette).setVisibility(View.GONE);

        // Set the listener
        ImageView insert = v.findViewById(R.id.insertButton);
        ImageView recent = v.findViewById(R.id.recentButton);
        ImageView spin = v.findViewById(R.id.buttonSpinRoulette);
        EditText textInsert = v.findViewById(R.id.entryRoulette);

        insert.setOnClickListener(this);
        recent.setOnClickListener(this);
        recent.setOnLongClickListener(this);
        spin.setOnClickListener(this);
        spin.setOnLongClickListener(this);
        textInsert.setOnEditorActionListener(this);

        return v;
    }

    @Override
    public boolean onLongClick(View v) {
        Activity act = getActivity();
        int pressedId = v.getId();
        if (pressedId == R.id.recentButton) {
            // Vibrate using the common method in MainActivity
            if (act instanceof MainActivity) ((MainActivity) act).vibrate();
            // Restore the last used options
            bottomSheet.restoreLatest(getContext());
            return true;
        }
        if (pressedId == R.id.buttonSpinRoulette) {
            // Vibrate using the common method in MainActivity
            if (act instanceof MainActivity) ((MainActivity) act).vibrate();

            // Insert three options manually and spin the roulette, or clear the options
            if (options.isEmpty()) {
                String option1 = getResources().getString(R.string.generic_option) + "1";
                String option2 = getResources().getString(R.string.generic_option) + "2";
                String option3 = getResources().getString(R.string.generic_option) + "3";
                insertRouletteChip(option1, true);
                insertRouletteChip(option2, true);
                insertRouletteChip(option3, true);
            } else {
                removeAllChips();
            }
            return true;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Activity act = getActivity();
        final ImageView recentAnimation = requireView().findViewById(R.id.recentButton);
        final ChipGroup optionsList = requireView().findViewById(R.id.rouletteChipList);
        int pressedId = v.getId();
        // Open recent choices
        if (pressedId == R.id.recentButton) {
            // Start the animated vector drawable
            Drawable recent = recentAnimation.getDrawable();
            if (recent instanceof Animatable) ((Animatable) recent).start();
            // Vibrate and play sound using the common method in MainActivity
            if (act instanceof MainActivity) ((MainActivity) act).vibrate();

            // Open a dialog with the recent searches
            if (bottomSheet.isAdded()) return;
            bottomSheet.show(getChildFragmentManager(), "roulette_bottom_sheet");
            return;
        }

        // Insert entry in roulette
        if (pressedId == R.id.insertButton) {
            // Start the animated vector drawable
            ImageView insertAnimation = requireView().findViewById(R.id.insertButton);
            Drawable insert = insertAnimation.getDrawable();
            if (insert instanceof Animatable) ((Animatable) insert).start();
            // Vibrate and play sound using the common method in MainActivity
            if (act instanceof MainActivity) {
                ((MainActivity) act).vibrate();
                ((MainActivity) act).playSound(1);
            }
            // Insert in both the list and the layout
            insertRouletteChip("", true);
            return;
        }

        // Spin the roulette
        if (pressedId == R.id.buttonSpinRoulette) {
            // Break the case if the list is empty to avoid crashes and null pointers
            if (options.size() < 2) {
                Toast.makeText(getContext(), getString(R.string.no_entry_roulette), Toast.LENGTH_SHORT).show();
                return;
            }
            // Start the animated vector drawable, make the button not clickable during the execution
            final ImageView spinAnimation = requireView().findViewById(R.id.buttonSpinRoulette);

            recentAnimation.setClickable(false);
            recentAnimation.setLongClickable(false);
            spinAnimation.setClickable(false);
            spinAnimation.setLongClickable(false);
            final int childCount = optionsList.getChildCount();
            for (int i = 0; i < childCount; i++) {
                Chip option = (Chip) optionsList.getChildAt(i);
                option.setClickable(false);
            }

            Drawable spin = spinAnimation.getDrawable();
            if (spin instanceof Animatable) ((Animatable) spin).start();

            // Vibrate and play sound using the common method in MainActivity
            if (act instanceof MainActivity) {
                ((MainActivity) act).vibrate();
                ((MainActivity) act).playSound(1);
            }
            Random ran = new Random();
            final int n = ran.nextInt(options.size());

            // Get the text view and set its value depending on n (using a delay)
            final TextView textViewResult = requireView().findViewById(R.id.resultRoulette);

            // Insert in the recent list
            bottomSheet.updateRecent(options, getContext());

            // Create the animations
            final Animation animIn = new AlphaAnimation(1.0f, 0.0f);
            animIn.setDuration(1500);
            textViewResult.startAnimation(animIn);
            final Animation animOut = new AlphaAnimation(0.0f, 1.0f);
            animOut.setDuration(1000);

            requireView().postDelayed(() -> {
                textViewResult.setText(options.get(n));
                textViewResult.startAnimation(animOut);
                spinAnimation.setClickable(true);
                spinAnimation.setLongClickable(true);
                recentAnimation.setClickable(true);
                recentAnimation.setLongClickable(true);
                for (int i = 0; i < childCount; i++) {
                    Chip option = (Chip) optionsList.getChildAt(i);
                    option.setClickable(true);
                }
            }, 1500);
        }
    }

    // Handle the keyboard actions, like enter, done, send and so on.
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEND) {
            // Start the animated vector drawable
            ImageView insertAnimation = requireView().findViewById(R.id.insertButton);
            Drawable insert = insertAnimation.getDrawable();
            if (insert instanceof Animatable) ((Animatable) insert).start();
            // Insert in both the list and the layout
            insertRouletteChip("", true);
            return true;
        }
        return false;
    }

    // Insert a chip in the roulette (15 chips limit)
    private void insertRouletteChip(String option, boolean limitNumber) {
        String currentOption;
        boolean allowEquals = sp.getBoolean("allow_equals", false);
        if (!option.equals("")) currentOption = option;
        else {
            TextView entry = requireView().findViewById(R.id.entryRoulette);
            currentOption = entry.getText().toString().trim();
            currentOption = currentOption.replaceAll("\\s+", " ");
            // Return if the string entered is a duplicate, reset the text field
            if ((!allowEquals && options.contains(currentOption)) || currentOption.equals(""))
                return;
            entry.setText("");
        }
        final ChipGroup optionsList = requireView().findViewById(R.id.rouletteChipList);

        // Check if the limit is reached
        if (options.size() > 14 && limitNumber) {
            Toast.makeText(getContext(), getString(R.string.too_much_entries_roulette), Toast.LENGTH_SHORT).show();
            System.out.println(options.toString());
            return;
        }

        // Add to the list
        options.add(currentOption);

        // Inflate the layout and its onclick action
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final Chip chip = (Chip) inflater.inflate(R.layout.chip_roulette, optionsList, false);
        chip.setText(currentOption);
        chip.setId(options.size());

        // Add the chip with an animation
        optionsList.addView(chip);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.chip_enter_anim);
        chip.startAnimation(animation);

        // Remove the chip and the element from the list
        chip.setOnClickListener(view -> removeChip(chip));
    }

    // Remove a single chip
    private void removeChip(final Chip chip) {
        final ChipGroup optionsList = requireView().findViewById(R.id.rouletteChipList);
        final ImageView spinAnimation = requireView().findViewById(R.id.buttonSpinRoulette);
        // Remove the chip with an animation
        if (chip == null) return;
        options.remove(chip.getText().toString());
        spinAnimation.setClickable(false);
        spinAnimation.setLongClickable(false);
        final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.chip_exit_anim);
        chip.startAnimation(animation);
        chip.postDelayed(() -> {
            optionsList.removeView(chip);
            spinAnimation.setClickable(true);
            spinAnimation.setLongClickable(true);
        }, 400);
    }

    // Remove every chip in the list
    private void removeAllChips() {
        final ChipGroup optionsList = requireView().findViewById(R.id.rouletteChipList);
        final int childCount = optionsList.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Chip chip = (Chip) optionsList.getChildAt(i);
            removeChip(chip);
        }
    }

    // Insert a certain combination in the roulette
    void restoreOption(List<String> option) {
        removeAllChips();
        for (String item : option) {
            insertRouletteChip(item, false);
        }
    }

}
