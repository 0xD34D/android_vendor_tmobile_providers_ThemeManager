package com.tmobile.thememanager.provider;

import com.tmobile.thememanager.ThemeManager;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.CustomTheme;
import android.database.Cursor;
import android.net.Uri;

public class Themes {
    public static final String AUTHORITY = "com.tmobile.thememanager.themes";

    public static final Uri CONTENT_URI =
        Uri.parse("content://" + AUTHORITY);

    private Themes() {}

    public static Uri getThemeUri(Context context, String packageName, String themeId) {
        return ThemeColumns.CONTENT_URI.buildUpon()
            .appendPath(packageName)
            .appendPath(themeId).build();
    }

    public static Uri getDefaultThemeUri(Context context) {
        CustomTheme def = CustomTheme.getDefault();
        return getThemeUri(context, def.getThemePackageName(), def.getThemeId());
    }

    public static Cursor listThemes(Context context) {
        return listThemes(context, null);
    }

    public static Cursor listThemes(Context context, String[] projection) {
        return context.getContentResolver().query(ThemeColumns.CONTENT_PLURAL_URI,
                projection, null, null, null);
    }

    public static Cursor listThemesByPackage(Context context, String packageName) {
        return context.getContentResolver().query(ThemeColumns.CONTENT_PLURAL_URI,
                null, ThemeColumns.THEME_PACKAGE + " = ?",
                new String[] { packageName }, null);
    }

    public static Cursor getAppliedTheme(Context context) {
        return context.getContentResolver().query(ThemeColumns.CONTENT_PLURAL_URI,
                null, ThemeColumns.IS_APPLIED + "=1", null, null);
    }

    public static void deleteTheme(Context context, String packageName,
            String themeId) {
        context.getContentResolver().delete(
                ThemeColumns.CONTENT_PLURAL_URI, ThemeColumns.THEME_PACKAGE + " = ? AND " +
                    ThemeColumns.THEME_ID + " = ?",
                new String[] { packageName, themeId });
    }

    public static void deleteThemesByPackage(Context context, String packageName) {
        context.getContentResolver().delete(
                ThemeColumns.CONTENT_PLURAL_URI, ThemeColumns.THEME_PACKAGE + " = ?",
                new String[] { packageName });
    }

    public static void markAppliedTheme(Context context, String packageName, String themeId) {
        ContentValues values = new ContentValues();
        values.put(ThemeColumns.IS_APPLIED, 0);
        context.getContentResolver().update(ThemeColumns.CONTENT_PLURAL_URI, values, null, null);
        values.put(ThemeColumns.IS_APPLIED, 1);
        context.getContentResolver().update(ThemeColumns.CONTENT_PLURAL_URI, values,
                ThemeColumns.THEME_PACKAGE + " = ? AND " +
                    ThemeColumns.THEME_ID + " = ?",
                new String[] { packageName, themeId });
    }

    /**
     * Request a theme change by broadcasting to the ThemeManager. Must hold
     * permission {@link ThemeManager#PERMISSION_CHANGE_THEME}.
     */
    public static void changeTheme(Context context, Uri themeUri) {
        changeTheme(context, new Intent(ThemeManager.ACTION_CHANGE_THEME, themeUri));
    }

    public static void changeStyle(Context context, Uri styleUri) {
        changeTheme(context, new Intent(ThemeManager.ACTION_CHANGE_THEME).setDataAndType(styleUri,
                ThemeColumns.STYLE_CONTENT_ITEM_TYPE));
    }

    /**
     * Alternate API to {@link #changeTheme(Context, Uri)} which allows you to
     * customize the intent that is delivered. This is used to access more
     * advanced functionality like conditionalizing certain parts of the theme
     * that is going to be applied.
     */
    public static void changeTheme(Context context, Intent intent) {
        context.sendOrderedBroadcast(intent, Manifest.permission.CHANGE_CONFIGURATION);
    }

    public interface ThemeColumns {
        public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/theme");

        public static final Uri CONTENT_PLURAL_URI =
            Uri.parse("content://" + AUTHORITY + "/themes");

        public static final String CONTENT_TYPE = "vnd.tmobile.cursor.dir/theme";
        public static final String CONTENT_ITEM_TYPE = "vnd.tmobile.cursor.item/theme";

        public static final String STYLE_CONTENT_TYPE = "vnd.tmobile.cursor.dir/style";
        public static final String STYLE_CONTENT_ITEM_TYPE = "vnd.tmobile.cursor.item/style";

        public static final String _ID = "_id";
        public static final String THEME_ID = "theme_id";
        public static final String THEME_PACKAGE = "theme_package";

        public static final String IS_APPLIED = "is_applied";

        public static final String NAME = "name";
        public static final String STYLE_NAME = "style_name";
        public static final String AUTHOR = "author";
        public static final String IS_DRM = "is_drm";

        public static final String WALLPAPER_NAME = "wallpaper_name";
        public static final String WALLPAPER_URI = "wallpaper_uri";

        public static final String LOCK_WALLPAPER_NAME = "lock_wallpaper_name";
        public static final String LOCK_WALLPAPER_URI = "lock_wallpaper_uri";

        public static final String RINGTONE_NAME = "ringtone_name";
        public static final String RINGTONE_URI = "ringtone_uri";
        public static final String NOTIFICATION_RINGTONE_NAME = "notif_ringtone_name";
        public static final String NOTIFICATION_RINGTONE_URI = "notif_ringtone_uri";

        public static final String THUMBNAIL_URI = "thumbnail_uri";
        public static final String PREVIEW_URI = "preview_uri";

        public static final String IS_SYSTEM = "system";
    }
}
