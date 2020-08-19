package com.tyctak.canalmap.libraries;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Patterns;

import com.tyctak.canalmap.MyApp;
import com.tyctak.map.libraries.XP_Library_CM;

//import org.jsoup.helper.StringUtil;

import java.util.regex.Pattern;

import static android.R.attr.id;

public class Library_ME {

    public enum enmURLType {
        App,
        Web
    }

    public Uri ValidateYouTube(enmURLType urlType, String youtube) {
        Uri retval = Uri.parse("");

        try {
            String name = "";

            if (Patterns.WEB_URL.matcher(youtube).matches() && youtube.contains("youtube.com/")) {
                youtube = (youtube.endsWith("/") ? youtube.substring(0, youtube.length()-1) : youtube);
                name = youtube.substring(youtube.lastIndexOf('/') + 1);
            } else {
                name = youtube;
            }

            if (!name.isEmpty() && Pattern.matches("^[a-zA-Z0-9_.]{1,30}$", name)) {
                String uri = "";

                if (urlType == enmURLType.App) {
                    uri = "vnd.youtube://m.youtube.com/channel/" + name;
                } else {
                    uri = "https://youtube.com/" + name;
                }

                retval = Uri.parse(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retval;
    }


    public Uri ValidateInstagram(enmURLType urlType, String instagram) {
        Uri retval = Uri.parse("");

        try {
            String name = "";

            if (Patterns.WEB_URL.matcher(instagram).matches() && instagram.contains("instagram.com/")) {
                instagram = (instagram.endsWith("/") ? instagram.substring(0, instagram.length()-1) : instagram);
                name = instagram.substring(instagram.lastIndexOf('/') + 1);
            } else {
                name = instagram;
            }

            if (!name.isEmpty() && Pattern.matches("^[a-zA-Z0-9_.]{1,30}$", name)) {
                String uri = "";

                if (urlType == enmURLType.App) {
                    uri = "https://instagram.com/_u/" + name;
                } else {
                    uri = "https://instagram.com/" + name;
                }

                retval = Uri.parse(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retval;
    }

    public Uri ValidatePatreon(enmURLType urlType, String patreon) {
        Uri retval = Uri.parse("");

        try {
            String name = "";

            if (Patterns.WEB_URL.matcher(patreon).matches() && patreon.contains("patreon.com/")) {
                patreon = (patreon.endsWith("/") ? patreon.substring(0, patreon.length()-1) : patreon);
                name = patreon.substring(patreon.lastIndexOf('/') + 1);
            } else {
                name = patreon;
            }

            if (!name.isEmpty() && Pattern.matches("^[a-zA-Z0-9_]{1,15}$", name)) {
                String uri = "";

                if (urlType == enmURLType.App) {
                    uri = "patreon://user?screen_name=" + name;
                } else {
                    uri = "https://patreon.com/" + name;
                }

                retval = Uri.parse(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retval;
    }

    public Uri ValidateTwitter(enmURLType urlType, String twitter) {
        Uri retval = Uri.parse("");

        try {
            String name = "";

            if (Patterns.WEB_URL.matcher(twitter).matches() && twitter.contains("twitter.com/")) {
                twitter = (twitter.endsWith("/") ? twitter.substring(0, twitter.length()-1) : twitter);
                name = twitter.substring(twitter.lastIndexOf('/') + 1);
            } else {
                name = twitter;
            }

            if (!name.isEmpty() && Pattern.matches("^[a-zA-Z0-9_]{1,15}$", name)) {
                String uri = "";

                if (urlType == enmURLType.App) {
                    uri = "twitter://user?screen_name=" + name;
                } else {
                    uri = "https://twitter.com/" + name;
                }

                retval = Uri.parse(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retval;
    }

    public Uri ValidateFacebook(enmURLType urlType, String facebook) {
        Uri retval = Uri.parse("");

        try {
            String id = "";

            if (Patterns.WEB_URL.matcher(facebook).matches() && facebook.contains("facebook.com/")) {
                facebook = (facebook.endsWith("/") ? facebook.substring(0, facebook.length() - 1) : facebook);
                id = facebook.substring(facebook.lastIndexOf('/') + 1);
            } else {
                id = facebook;
            }

            if (!id.isEmpty() && Pattern.matches("^[a-zA-Z0-9_]{1,50}$", id)) {
                String uri = "";

                if (urlType == enmURLType.App) {
                    PackageManager pm = MyApp.getContext().getPackageManager();
                    int versionCode = pm.getPackageInfo("com.facebook.katana", 0).versionCode;
                    if (versionCode >= 3002850) {
                        uri = "fb://facewebmodal/f?href=" + id;
                    } else {
                        uri = "fb://page/" + id;
                    }
                } else {
                    uri = "https://www.facebook.com/" + id;
                }

                retval = Uri.parse(uri);
            }
        } catch (PackageManager.NameNotFoundException e) {
            retval = Uri.parse("https://www.facebook.com/" + id);

            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retval;
    }

    public boolean LoadFacebook(Context context, String facebook) {
        boolean retval = false;
        XP_Library_CM LIBCM = new XP_Library_CM();

        try {
            if (!LIBCM.isBlank(facebook)) {
                Intent intent = null;
                Uri uri = Uri.parse("");

                try {
                    PackageManager pm = MyApp.getContext().getPackageManager();
                    ApplicationInfo ai = pm.getApplicationInfo("com.facebook.katana", 0);

                    // Having trouble trying to get this to work

//                    if (ai.enabled) {
//                        uri = ValidateFacebook(enmURLType.App, facebook);
//                        intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setData(uri);
//                        intent.setPackage("com.facebook.katana");
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    } else {
                    uri = ValidateFacebook(enmURLType.Web, facebook);
                    intent = new Intent(Intent.ACTION_VIEW, uri);
//                    }
                } catch (Exception e) {
                    uri = ValidateFacebook(enmURLType.Web, facebook);
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                }

                if (!uri.toString().isEmpty()) {
                    retval = true;
                    context.startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retval;
    }

    public boolean LoadPatreon(Context context, String patreon) {
        boolean retval = false;
        XP_Library_CM LIBCM = new XP_Library_CM();

        try {
            if (!LIBCM.isBlank(patreon)) {
                Intent intent = null;
                Uri uri = Uri.parse("");

                try {
                    PackageManager pm = MyApp.getContext().getPackageManager();
                    ApplicationInfo ai = pm.getApplicationInfo("com.patreon.android", 0);

                    if (ai.enabled) {
                        uri = ValidatePatreon(enmURLType.App, patreon);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setPackage("com.patreon.android");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    } else {
                        uri = ValidatePatreon(enmURLType.Web, patreon);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                    }
                } catch (Exception e) {
                    uri = ValidatePatreon(enmURLType.Web, patreon);
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                }

                if (!uri.toString().isEmpty()) {
                    retval = true;
                    context.startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retval;
    }

    public boolean LoadTwitter(Context context, String twitter) {
        boolean retval = false;
        XP_Library_CM LIBCM = new XP_Library_CM();

        try {
            if (!LIBCM.isBlank(twitter)) {
                Intent intent = null;
                Uri uri = Uri.parse("");

                try {
                    PackageManager pm = MyApp.getContext().getPackageManager();
                    ApplicationInfo ai = pm.getApplicationInfo("com.twitter.android", 0);

                    if (ai.enabled) {
                        uri = ValidateTwitter(enmURLType.App, twitter);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setPackage("com.twitter.android");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    } else {
                        uri = ValidateTwitter(enmURLType.Web, twitter);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                    }
                } catch (Exception e) {
                    uri = ValidateTwitter(enmURLType.Web, twitter);
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                }

                if (!uri.toString().isEmpty()) {
                    retval = true;
                    context.startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retval;
    }

    public boolean LoadInstagram(Context context, String instagram) {
        boolean retval = false;
        XP_Library_CM LIBCM = new XP_Library_CM();

        try {
            if (!LIBCM.isBlank(instagram)) {
                Intent intent = null;
                Uri uri = Uri.parse("");

                try {
                    PackageManager pm = MyApp.getContext().getPackageManager();
                    ApplicationInfo ai = pm.getApplicationInfo("com.instagram.android", 0);

                    if (ai.enabled) {
                        uri = ValidateInstagram(enmURLType.App, instagram);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setPackage("com.instagram.android");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    } else {
                        uri = ValidateInstagram(enmURLType.Web, instagram);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                    }
                } catch (Exception e) {
                    uri = ValidateInstagram(enmURLType.Web, instagram);
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                }

                if (!uri.toString().isEmpty()) {
                    retval = true;
                    context.startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retval;
    }

    public boolean LoadWebsite(Context context, String website) {
        boolean retval = false;
        XP_Library_CM LIBCM = new XP_Library_CM();

        try {
            if (!LIBCM.isBlank(website) && (Patterns.WEB_URL.matcher(website).matches())) {
                Uri uri = Uri.parse(website);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                retval = true;
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retval;
    }

    public boolean LoadYoutube(Context context, String youtube) {
        boolean retval = false;
        XP_Library_CM LIBCM = new XP_Library_CM();

        try {
            if (!LIBCM.isBlank(youtube)) {
                Intent intent = null;
                Uri uri = Uri.parse("");

                try {
                    PackageManager pm = MyApp.getContext().getPackageManager();
                    ApplicationInfo ai = pm.getApplicationInfo("com.google.android.youtube", 0);

                    if (ai.enabled) {
                        uri = ValidateYouTube(enmURLType.App, youtube);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setPackage("com.google.android.youtube");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    } else {
                        uri = ValidateYouTube(enmURLType.Web, youtube);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                    }
                } catch (Exception e) {
                    uri = ValidateYouTube(enmURLType.Web, youtube);
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                }

                if (!uri.toString().isEmpty()) {
                    retval = true;
                    context.startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retval;
    }
}