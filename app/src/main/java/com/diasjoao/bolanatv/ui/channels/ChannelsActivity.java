package com.diasjoao.bolanatv.ui.channels;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.settings.ChannelSettings;
import com.diasjoao.bolanatv.ui.games.ChannelStyle;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChannelsActivity extends AppCompatActivity {

    private static final class Row {
        String key;
        TextView tag, status;
        MaterialSwitch toggle;
    }

    private final List<Row> rows = new ArrayList<>();
    private TextView summary;
    private View showAll;
    private boolean updating; // true while the code (not the user) changes the switches

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);

        View header = findViewById(R.id.header);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            header.setPadding(0, bars.top, 0, 0);
            v.setPadding(0, 0, 0, bars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        summary = findViewById(R.id.summary);
        showAll = findViewById(R.id.show_all);
        showAll.setOnClickListener(v -> showAllChannels());

        buildRows();
        updateSummary();
    }

    private void buildRows() {
        android.widget.LinearLayout list = findViewById(R.id.list);
        Set<String> hidden = ChannelSettings.hiddenKeys(this);

        for (String name : ChannelSettings.CHANNELS) {
            View view = getLayoutInflater().inflate(R.layout.item_channel, list, false);

            Row r = new Row();
            r.key = ChannelSettings.keyOf(name);
            r.tag = view.findViewById(R.id.tag);
            r.status = view.findViewById(R.id.status);
            r.toggle = view.findViewById(R.id.toggle);

            r.tag.setText(name);
            ChannelStyle style = ChannelStyle.of(this, name);
            ViewCompat.setBackgroundTintList(r.tag, ColorStateList.valueOf(style.background));
            r.tag.setTextColor(style.text);
            r.toggle.setContentDescription(name);

            boolean visible = !hidden.contains(r.key);
            r.toggle.setChecked(visible);
            applyRowState(r, visible);

            r.toggle.setOnCheckedChangeListener((button, checked) -> {
                if (updating) return;
                ChannelSettings.setVisible(this, r.key, checked);
                applyRowState(r, checked);
                updateSummary();
            });
            view.setOnClickListener(v -> r.toggle.toggle()); // the whole row is tappable

            rows.add(r);
            list.addView(view);
        }
    }

    private void applyRowState(Row r, boolean visible) {
        r.status.setText(visible ? R.string.channel_visible : R.string.channel_hidden);
        r.tag.setAlpha(visible ? 1f : 0.4f);
    }

    private void showAllChannels() {
        updating = true;
        ChannelSettings.showAll(this);
        for (Row r : rows) {
            r.toggle.setChecked(true);
            applyRowState(r, true);
        }
        updating = false;
        updateSummary();
    }

    private void updateSummary() {
        int visible = 0;
        for (Row r : rows) {
            if (r.toggle.isChecked()) visible++;
        }
        summary.setText(getString(R.string.channels_summary, visible, rows.size()));
        showAll.setVisibility(visible < rows.size() ? View.VISIBLE : View.INVISIBLE);
    }
}