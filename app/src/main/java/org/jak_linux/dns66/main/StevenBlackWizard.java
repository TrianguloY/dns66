package org.jak_linux.dns66.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import org.jak_linux.dns66.Configuration;
import org.jak_linux.dns66.FileHelper;
import org.jak_linux.dns66.MainActivity;
import org.jak_linux.dns66.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Allows editing the StevenBlack hosts files with a simple checklist dialog
 */
public class StevenBlackWizard implements DialogInterface.OnMultiChoiceClickListener {

    // base elements
    private Context cntx; // base context
    private final ItemRecyclerViewAdapter listAdapter; // list of items

    /**
     * Constructor
     *
     * @param cntx        base context (for context operations)
     * @param listAdapter where the items are stored and displayed
     */
    public StevenBlackWizard(Context cntx, ItemRecyclerViewAdapter listAdapter) {
        this.cntx = cntx;
        this.listAdapter = listAdapter;
    }

    // ------------------- Public -------------------

    /**
     * Main entry, loads the configuration and shows the dialog
     */
    public void showWizard() {
        // load config
        loadCurrentSettings();

        // show dialog
        new AlertDialog.Builder(cntx)
                .setTitle(R.string.stevenblack_config)
                .setMultiChoiceItems(R.array.stevenblack_items, checked, this) // clicked => #onClick
                .setPositiveButton(R.string.button_apply, (dialog, which) -> saveSettings()) // apply => save
                .setNegativeButton(R.string.button_cancel, null) // cancel => nothing
                .show();
    }

    // ------------------- private -------------------

    /**
     * List of all possible urls
     * URLS, TITLES and CHECKS must follow same order
     */
    private static final ArrayList<String> URLS = new ArrayList<>(Arrays.asList(
            "https://raw.githubusercontent.com/StevenBlack/hosts/master/hosts",
            "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/fakenews/hosts",
            "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/gambling/hosts",
            "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/porn/hosts",
            "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/social/hosts",
            "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/fakenews-gambling/hosts",
            "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/fakenews-porn/hosts",
            "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/fakenews-social/hosts",
            "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/gambling-porn/hosts",
            "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/gambling-social/hosts",
            "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/porn-social/hosts",
            "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/fakenews-gambling-porn/hosts",
            "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/fakenews-gambling-social/hosts",
            "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/fakenews-porn-social/hosts",
            "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/gambling-porn-social/hosts",
            "https://raw.githubusercontent.com/StevenBlack/hosts/master/alternates/fakenews-gambling-porn-social/hosts"
    ));

    /**
     * List of all possible titles
     * URLS, TITLES and CHECKS must follow same order
     */
    private static final ArrayList<String> TITLES = new ArrayList<>(Arrays.asList(
            "StevenBlack's hosts file (includes all others)",
            "StevenBlack's hosts file (includes all others) + fakenews",
            "StevenBlack's hosts file (includes all others) + gambling",
            "StevenBlack's hosts file (includes all others) + porn",
            "StevenBlack's hosts file (includes all others) + social",
            "StevenBlack's hosts file (includes all others) + fakenews + gambling",
            "StevenBlack's hosts file (includes all others) + fakenews + porn",
            "StevenBlack's hosts file (includes all others) + fakenews + social",
            "StevenBlack's hosts file (includes all others) + gambling + porn",
            "StevenBlack's hosts file (includes all others) + gambling + social",
            "StevenBlack's hosts file (includes all others) + porn + social",
            "StevenBlack's hosts file (includes all others) + fakenews + gambling + porn",
            "StevenBlack's hosts file (includes all others) + fakenews + gambling + social",
            "StevenBlack's hosts file (includes all others) + fakenews + porn + social",
            "StevenBlack's hosts file (includes all others) + gambling + porn + social",
            "StevenBlack's hosts file (includes all others) + fakenews + gambling + porn + social"
    ));

    /**
     * List of the corresponding checked values
     * URLS, TITLES and CHECKS must follow same order
     */
    private static final ArrayList<BooleanArray> CHECKS = new ArrayList<>(Arrays.asList(
            new BooleanArray(true, false, false, false, false),
            new BooleanArray(true, true, false, false, false),
            new BooleanArray(true, false, true, false, false),
            new BooleanArray(true, false, false, true, false),
            new BooleanArray(true, false, false, false, true),
            new BooleanArray(true, true, true, false, false),
            new BooleanArray(true, true, false, true, false),
            new BooleanArray(true, true, false, false, true),
            new BooleanArray(true, false, true, true, false),
            new BooleanArray(true, false, true, false, true),
            new BooleanArray(true, false, false, true, true),
            new BooleanArray(true, true, true, true, false),
            new BooleanArray(true, true, true, false, true),
            new BooleanArray(true, true, false, true, true),
            new BooleanArray(true, false, true, true, true),
            new BooleanArray(true, true, true, true, true)
    ));

    // variables
    private boolean[] checked = new boolean[5]; // shown checkboxes in the dialog
    private int hostIndex; // index of host item being edited

    /**
     * When an element in the dialog is clicked
     *
     * @param dialog    the dialog shown
     * @param which     index of the element clicked
     * @param isChecked whether it was checked or unchecked
     */
    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (which == 0 && !isChecked) {
            // when 'ads and malware' is disabled, disable the rest
            for (int i = 1; i < 5; ++i) {
                // disable and update all except the first
                checked[i] = false;
                ((AlertDialog) dialog).getListView().setItemChecked(i, false);
            }
        }

        if (which != 0 && isChecked) {
            // enable 'ads and malware' if another one is enabled
            checked[0] = true;
            ((AlertDialog) dialog).getListView().setItemChecked(0, true);
        }
    }

    /**
     * Loads the current settings by finding the entry and extracting the selected elements based on the url and the state
     */
    private void loadCurrentSettings() {
        // find host index

        // start with all disabled (in case nothing is found)
        hostIndex = -1;
        Arrays.fill(checked, false);

        // find the entry, search starting from the bottom one
        for (int i = listAdapter.items.size() - 1; i >= 0; i--) {
            // for each entry check the url
            Configuration.Item item = listAdapter.items.get(i);
            final int index = URLS.indexOf(item.location);
            if (index != -1) {
                // url valid, item found
                hostIndex = i;

                // get elements
                switch (item.state) {
                    case Configuration.Item.STATE_DENY:
                        // item enabled, valid
                        checked = CHECKS.get(index).getArray();
                        // stop search
                        return;
                    case Configuration.Item.STATE_ALLOW:
                        // item allowed? treat as ignored
                    case Configuration.Item.STATE_IGNORE:
                        // item disabled, set all as unchecked
                        Arrays.fill(checked, false);
                        // but continue searching in case there is another enabled file
                        break;
                }
            }
        }
    }

    /**
     * Apply the selected settings
     */
    private void saveSettings() {
        // get item to edit
        Configuration.Item item;
        if (hostIndex == -1) {
            // no old entry, create new at the beginning
            item = new Configuration.Item();
            listAdapter.items.add(0, item);
        } else {
            // get found entry
            item = listAdapter.items.get(hostIndex);
        }

        // get setting index
        int index = CHECKS.indexOf(new BooleanArray(checked));

        // set item settings
        if (index == -1) {
            // not found => all unselected => disabled
            item.state = Configuration.Item.STATE_IGNORE;

            // the rest of the settings are set same as the default
            index = 0;
        } else {
            // found => enabled
            item.state = Configuration.Item.STATE_DENY;
        }

        item.location = URLS.get(index);
        item.title = TITLES.get(index);


        // update
        listAdapter.notifyDataSetChanged();
        FileHelper.writeSettings(cntx, MainActivity.config);
    }


    // ------------------- utils -------------------

    /**
     * When using an Arraylist, ArrayList#indexOf uses equals.
     * If the ArrayList contains arrays (boolean[]) no objects are found (equals check for same objects, not content).
     * This wrapper overrides equals to use Arrays#equals, allowing searching.
     * Also allows to modify the returned array (the getter returns a clone)
     */
    private static class BooleanArray {
        private final boolean[] array;

        private BooleanArray(boolean... params) {
            array = params;
        }

        private boolean[] getArray() {
            return array.clone(); // as clone
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BooleanArray that = (BooleanArray) o;

            return Arrays.equals(array, that.array); // check content
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(array);
        }
    }

}
