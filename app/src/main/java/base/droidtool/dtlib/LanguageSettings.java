package base.droidtool.dtlib;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.text.TextUtils;

import java.util.Locale;

import anindya.sample.smackchat.R;
import base.droidtool.DroidTool;

import static base.droidtool.config.Constants.mLanguage;


public class LanguageSettings {

    DroidTool dt;

    public LanguageSettings(DroidTool droidTool) {
        dt = droidTool;
    }

    public interface onSetLanguageListener {
        void onSet();
    }

    public void setLanguage(onSetLanguageListener listener){

        final onSetLanguageListener customListener = listener;

        final String shortLanguage[] = new String[]{"bn", "en", "en"};
        String fullLanguage[] = new String[]{"Bengali", "English", "Default"};

        dt.ui.modal.buttonLessSingleChoiceModal(dt.gStr(R.string.language_settings), fullLanguage, new Ux.onModalListItemClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dt.pref.set(mLanguage, shortLanguage[id]);
                if(customListener != null) customListener.onSet();
            }
        });
    }

    public void configLanguage(){
        String language = dt.pref.getString(mLanguage);
        if(!TextUtils.isEmpty(language)){
            Locale locale = new Locale(language);
            Configuration config = ((Activity)dt.c).getBaseContext().getResources().getConfiguration();
            config.locale = locale;
            ((Activity)dt.c).getBaseContext().getResources().updateConfiguration(config, ((Activity)dt.c).getBaseContext().getResources().getDisplayMetrics());
        }
    }



}
