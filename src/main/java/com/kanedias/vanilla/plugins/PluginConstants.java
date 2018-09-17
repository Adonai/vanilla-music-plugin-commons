/*
 * Copyright (C) 2016 Oleg Chernovskiy <adonai@xaker.ru>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.kanedias.vanilla.plugins;

/**
 * This class constants should be synchronized with VanillaMusic <code>PluginUtils</code> class
 */
public class PluginConstants {

    private PluginConstants() {}

    // auxiliary constants for SAF interchange
    public static final String EXTRA_PARAM_SAF_P2P = "ch.blinkenlights.android.vanilla.extra.SAF_P2P";
    public static final String PREF_SDCARD_URI = "ch.blinkenlights.android.vanilla.pref.SDCARD_URI";

    // these actions are for passing between main player and plugins
    public static final String ACTION_REQUEST_PLUGIN_PARAMS = "ch.blinkenlights.android.vanilla.action.REQUEST_PLUGIN_PARAMS"; // broadcast
    public static final String ACTION_HANDLE_PLUGIN_PARAMS = "ch.blinkenlights.android.vanilla.action.HANDLE_PLUGIN_PARAMS"; // answer
    public static final String ACTION_WAKE_PLUGIN = "ch.blinkenlights.android.vanilla.action.WAKE_PLUGIN"; // targeted for each found
    public static final String ACTION_LAUNCH_PLUGIN = "ch.blinkenlights.android.vanilla.action.LAUNCH_PLUGIN"; // targeted at selected by user

    // these are used by plugins to describe themselves
    public static final String EXTRA_PARAM_PLUGIN_NAME = "ch.blinkenlights.android.vanilla.extra.PLUGIN_NAME";
    public static final String EXTRA_PARAM_PLUGIN_APP = "ch.blinkenlights.android.vanilla.extra.PLUGIN_APP";
    public static final String EXTRA_PARAM_PLUGIN_DESC = "ch.blinkenlights.android.vanilla.extra.PLUGIN_DESC";

    // this is passed to plugin when it is selected by user
    public static final String EXTRA_PARAM_URI = "ch.blinkenlights.android.vanilla.extra.URI";
    public static final String EXTRA_PARAM_SONG_TITLE = "ch.blinkenlights.android.vanilla.extra.SONG_TITLE";
    public static final String EXTRA_PARAM_SONG_ALBUM = "ch.blinkenlights.android.vanilla.extra.SONG_ALBUM";
    public static final String EXTRA_PARAM_SONG_ARTIST = "ch.blinkenlights.android.vanilla.extra.SONG_ARTIST";

    // plugin-to-plugin extras (pass EXTRA_PARAM_PLUGIN_APP too to know whom to answer)
    public static final String EXTRA_PARAM_P2P = "ch.blinkenlights.android.vanilla.extra.P2P"; // marker
    public static final String EXTRA_PARAM_P2P_KEY = "ch.blinkenlights.android.vanilla.extra.P2P_KEY";
    public static final String EXTRA_PARAM_P2P_VAL = "ch.blinkenlights.android.vanilla.extra.P2P_VALUE";

    // related to tag editor
    public static final String P2P_WRITE_TAG = "WRITE_TAG";
    public static final String P2P_READ_TAG = "READ_TAG";
    public static final String P2P_WRITE_ART = "WRITE_ART";
    public static final String P2P_READ_ART = "READ_ART";

    public static final String LOG_TAG = "Vanilla:Plugin";
}
