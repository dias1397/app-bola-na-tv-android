package com.diasjoao.bolanatv.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.model.Game;
import com.diasjoao.bolanatv.ui.ChannelStyle;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GameSheet extends BottomSheetDialogFragment {

    private static final String HOME = "home", AWAY = "away", COMP = "comp",
            CHANNEL = "channel", DATE = "date", TIME = "time";
    private static final TimeZone LISBON = TimeZone.getTimeZone("Europe/Lisbon");
    private static final Locale PT = Locale.forLanguageTag("pt-PT");

    public static GameSheet newInstance(Game g) {
        Bundle b = new Bundle();
        b.putString(HOME, g.getHome());
        b.putString(AWAY, g.getAway());
        b.putString(COMP, g.getCompetition());
        b.putString(CHANNEL, g.getChannel());
        b.putString(DATE, g.getDate());
        b.putString(TIME, g.getTime());
        GameSheet sheet = new GameSheet();
        sheet.setArguments(b);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sheet_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle a = requireArguments();
        String home = a.getString(HOME, "");
        String away = a.getString(AWAY, "");
        String comp = a.getString(COMP, "");
        String channel = a.getString(CHANNEL, "");
        String date = a.getString(DATE, "");
        String time = a.getString(TIME, "");

        ((TextView) view.findViewById(R.id.sheet_title)).setText(home + " – " + away);
        ((TextView) view.findViewById(R.id.sheet_competition)).setText(comp);
        ((TextView) view.findViewById(R.id.sheet_when)).setText(whenText(date, time));

        TextView tag = view.findViewById(R.id.sheet_channel);
        tag.setText(channel);
        ChannelStyle style = ChannelStyle.of(requireContext(), channel);
        ViewCompat.setBackgroundTintList(tag, ColorStateList.valueOf(style.background));
        tag.setTextColor(style.text);

        view.findViewById(R.id.sheet_calendar).setOnClickListener(v ->
                addToCalendar(home, away, comp, channel, date, time));
    }

    private long kickoffMillis(String date, String time) {
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.ROOT);
        f.setTimeZone(LISBON);
        try {
            Date d = f.parse(date + " " + time);
            return d == null ? -1 : d.getTime();
        } catch (ParseException e) {
            return -1;
        }
    }

    private String whenText(String date, String time) {
        long millis = kickoffMillis(date, time);
        if (millis < 0) return date + " " + time;
        SimpleDateFormat f = new SimpleDateFormat("EEEE, d MMM", PT);
        f.setTimeZone(LISBON);
        String day = f.format(new Date(millis)).replace(".", "");
        return day.substring(0, 1).toUpperCase(PT) + day.substring(1) + " · " + time;
    }

    private void addToCalendar(String home, String away, String comp, String channel,
                               String date, String time) {
        long start = kickoffMillis(date, time);
        if (start < 0) {
            Toast.makeText(requireContext(), "Data inválida", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, home + " – " + away)
                .putExtra(CalendarContract.Events.DESCRIPTION, comp + " · " + channel)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, start + 2 * 60 * 60 * 1000L);
        try {
            startActivity(intent);
            dismiss();
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "Sem aplicação de calendário",
                    Toast.LENGTH_SHORT).show();
        }
    }
}