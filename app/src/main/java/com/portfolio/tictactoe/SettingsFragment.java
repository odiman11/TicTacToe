package com.portfolio.tictactoe;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

public class SettingsFragment extends PreferenceFragmentCompat {
    SwitchPreference isRandom, player2;
    PreferenceCategory player_choice, difficulty;
    CheckBoxPreference playerIsX, playerIsO, aiIsEasy, aiIsHard;
    SharedPreferences sharedPreferences;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        sharedPreferences = getPreferenceManager().getSharedPreferences();

        //setup player 1 random switch
        isRandom = findPreference("settings_player1_random");
        isRandom.setChecked(sharedPreferences.getBoolean("settings_player1_random" , true));

        player_choice = findPreference("settings_player1_choice");
        if(isRandom.isChecked()){
            player_choice.setVisible(false);
            isRandom.setTitle(R.string.Random);
        } else{
            isRandom.setTitle(R.string.Choose1);
            player_choice.setVisible(true);
        }

        playerIsX = findPreference("Player1_is_X");
        playerIsX.setChecked(sharedPreferences.getBoolean("Player1_is_X", true));
        playerIsO = findPreference("Player1_is_O");
        playerIsO.setChecked(sharedPreferences.getBoolean("Player1_is_O", false));

        if(playerIsX.isChecked()){
            playerIsO.setEnabled(false);
        }else if (playerIsO.isChecked()){
            playerIsX.setEnabled(false);
        } else {
            playerIsO.setEnabled(true);
            playerIsX.setEnabled(true);
        }

        player2 = findPreference("settings_player2_choice");
        player2.setChecked(sharedPreferences.getBoolean("settings_player2_choice", true));
        difficulty = findPreference("difficulty_choice");
        if(player2.isChecked()){
            difficulty.setVisible(true);
            player2.setTitle(R.string.computer);
        } else {
            player2.setTitle(R.string.human);
            difficulty.setVisible(false);
        }

        aiIsEasy = findPreference("check_box_easy");
        aiIsEasy.setChecked(sharedPreferences.getBoolean("check_box_easy", false));
        aiIsHard = findPreference("check_box_hard");
        aiIsHard.setChecked(sharedPreferences.getBoolean("check_box_hard", true));

        if(aiIsEasy.isChecked()){
            aiIsHard.setEnabled(false);
        } else if (aiIsHard.isChecked()){
            aiIsEasy.setEnabled(false);
        } else {
            aiIsEasy.setEnabled(true);
            aiIsHard.setEnabled(true);
        }

        //LISTENERS
        aiIsEasy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(aiIsEasy.isChecked()){
                    aiIsHard.setEnabled(false);
                } else {
                    aiIsHard.setEnabled(true);
                }
                return false;
            }
        });

        aiIsHard.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(aiIsHard.isChecked()){
                    aiIsEasy.setEnabled(false);
                } else {
                    aiIsEasy.setEnabled(true);
                }
                return false;
            }
        });

        playerIsX.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(playerIsX.isChecked()){
                    playerIsO.setEnabled(false);
                } else {
                    playerIsO.setEnabled(true);
                }
                return false;
            }
        });

        playerIsO.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(playerIsO.isChecked()){
                    playerIsX.setEnabled(false);
                } else {
                    playerIsX.setEnabled(true);
                }
                return false;
            }
        });

        player2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(player2.isChecked()){
                    player2.setTitle(R.string.computer);
                    difficulty.setVisible(true);
                } else {
                    player2.setTitle(R.string.human);
                    difficulty.setVisible(false);
                }
                return false;
            }
        });

        isRandom.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(!isRandom.isChecked()){
                    player_choice.setVisible(true);
                    isRandom.setTitle(R.string.Choose1);
                } else {
                    player_choice.setVisible(false);
                    isRandom.setTitle(R.string.Random);
                }
                return false;
            }
        });

        SharedPreferences.OnSharedPreferenceChangeListener listenerPreferenceChange =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                    }
                };
    }

}
