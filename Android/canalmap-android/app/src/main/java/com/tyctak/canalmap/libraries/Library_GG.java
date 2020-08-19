package com.tyctak.canalmap.libraries;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.tyctak.canalmap.Global;
import com.tyctak.canalmap.Library;
import com.tyctak.canalmap.MyApp;
import com.tyctak.canalmap.R;
import com.tyctak.map.entities._MySettings;
import com.tyctak.map.libraries.XP_Library_CM;

import java.util.List;

import static com.tyctak.canalmap.Activity_Main.setRedrawMap;

public class Library_GG implements PurchasesUpdatedListener {

    private String TAG = "Library_GG";

    private BillingClient mBillingClient;
    private Activity parent;
    private PurchaseResponseListener listener;
    public static long ONE_MONTH = (30 * 24 * 3600 * 1000l);
    public static long ONE_WEEK = (7 * 24 * 3600 * 1000l);
    public static long ONE_MINUTE = (60 * 1000l);

    public interface PurchaseResponseListener {
        void onPurchaseResponse(final int responseCode);
    }

    public boolean isNull() {
        return (mBillingClient == null);
    }

    public boolean isReady() {
        return (mBillingClient != null ? mBillingClient.isReady() : false);
    }

    public boolean checkPremium() {
        boolean retval = false;

        _MySettings mySettings = Global.getInstance().getDb().getMySettings();

//        Library LIB = new Library();
        if (mySettings.PremiumContent > 0 && mySettings.PremiumContent < (XP_Library_CM.getDate(XP_Library_CM.now()) + ONE_WEEK )) {
            repairPremiumPurchased();
        } else {
            retval = true;
        }

        return retval;
    }

    public void endConnection() {
        if (mBillingClient != null) {
            if (mBillingClient.isReady()) mBillingClient.endConnection();
            mBillingClient = null;
        }
    }

    public Library_GG(Activity pParent) {
        parent = pParent;

        mBillingClient = BillingClient.newBuilder(MyApp.getContext()).setListener(this).build();

        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponse) { }
            @Override
            public void onBillingServiceDisconnected() { }
        });
    }

    public int buyListener(Context parent, String productId, PurchaseResponseListener pListener, String type) {
        listener = pListener;

        BillingFlowParams purchaseParams = BillingFlowParams.newBuilder().setSku(productId).setType(type).build();
//        BillingFlowParams purchaseParams = new BillingFlowParams.Builder().setSku(productId).setType(type).build();
        int responseCode = mBillingClient.launchBillingFlow((Activity) parent, purchaseParams);
        return responseCode;
    }

    public int repairPremiumPurchased() {
        Log.d(TAG, "repairPremiumPurchased START");

        Purchase.PurchasesResult result = mBillingClient.queryPurchases(SkuType.SUBS);
        List<Purchase> purchases = result.getPurchasesList();

        int messageId = 0;

        if (isReady()) {
            if (result.getResponseCode() == BillingClient.BillingResponse.OK) {
                boolean isPurchased = false;
                long expiry = 0l;

                Log.d(TAG, "purchases=" + purchases.size());

                for (int i = 0; i < purchases.size(); ++i) {
                    String sku = purchases.get(i).getSku();
                    if (sku.equals(Global.getInstance().getDb().getPremiumGuid())) {
                        Log.d(TAG, "sku-Y=" + sku);

                        _MySettings mySettings = Global.getInstance().getDb().getMySettings();
                        expiry = ((mySettings.PremiumContent == 0 ? purchases.get(i).getPurchaseTime() : mySettings.PremiumContent) + (3 * ONE_MONTH));
                        isPurchased = true;
                        break;
                    } else {
                        Log.d(TAG, "sku-N=" + sku + "-" + Global.getInstance().getDb().getPremiumGuid());
                    }
                }

                if (isPurchased) {
                    Global.getInstance().getDb().writePremium(expiry);
                    messageId = R.string.msg_success_response;
                } else {
                    Log.d(TAG, "repairPremiumPurchased msg failed " + expiry);
                    messageId = R.string.msg_failed_ready;
                }
            } else {
                messageId = R.string.msg_failed_response;
            }
        } else {
            messageId = R.string.msg_failed_debug;
        }

        Log.d(TAG, "repairPremiumPurchased END");

        return messageId;
    }

    public int repairRoutesPurchased() {
        Purchase.PurchasesResult result = mBillingClient.queryPurchases(SkuType.INAPP);
        List<Purchase> purchases = result.getPurchasesList();

        int messageId = 0;

        if (isReady()) {
            if (result.getResponseCode() == BillingClient.BillingResponse.OK) {
                boolean isPurchased = false;

                for (int i = 0; i < purchases.size(); ++i) {
                    String sku = purchases.get(i).getSku();
                    if (sku.equals(Global.getInstance().getDb().getAllRouteGuid()) || Global.getInstance().getDb().getHistoricRouteGuids().contains(sku)) {
                        isPurchased = true;
                        break;
                    }
                }

                if (isPurchased) {
                    Global.getInstance().getDb().writeBuyRoute(Global.getInstance().getDb().getAllRouteGuid());
                    messageId = R.string.msg_success_response;
                } else {
                    messageId = R.string.msg_failed_ready;
                }
            } else {
                messageId = R.string.msg_failed_response;
            }
        } else {
            messageId = R.string.msg_failed_debug;
        }

        return messageId;
    }

    @Override
    public void onPurchasesUpdated(int responseCode, List<Purchase> purchases) {
        if (listener != null) listener.onPurchaseResponse(responseCode);
    }

    public void buyProduct(final String type, final String productId, final Activity parent, final Runnable successCallback, final Runnable failureCallback) {
        int responseCode = buyListener(parent, productId, new PurchaseResponseListener() {
            @Override
            public void onPurchaseResponse(final int responseCode) {
                if (responseCode == BillingClient.BillingResponse.OK) {
                    Library LIB = new Library();
                    long expiry = XP_Library_CM.getDate(XP_Library_CM.now()) +  (3 * ONE_MONTH);
                    Global.getInstance().getDb().writePremium(expiry);

                    if (successCallback != null) successCallback.run();
                } else {
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Library_UI LIBUI = new Library_UI();
                            LIBUI.snackBar(parent, String.format("%s (%s)", parent.getString(R.string.msg_failed_billing1), responseCode));
                        }
                    });

                    if (failureCallback != null) failureCallback.run();
                }
            }
        }, type);

        if (responseCode != BillingClient.BillingResponse.OK) {
            try {
                Log.e(TAG, "Billing returned error so waiting 2s and trying again");
                Thread.sleep(2000);

                if (isReady()) {
                    repairPremiumPurchased();
                } else {
                    Log.e(TAG, "Billing was not ready so unable to check");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void buyRoute(final ArrayAdapter arrayAdapter, final String routeGuid, final Activity parent, final Runnable successCallback, final Runnable failureCallback) {
        int responseCode = buyListener(parent, routeGuid, new PurchaseResponseListener() {
            @Override
            public void onPurchaseResponse(final int responseCode) {
                if (responseCode == BillingClient.BillingResponse.OK) {
                    setRedrawMap(true);
                    Global.getInstance().getDb().writeBuyRoute(routeGuid);
                    if (arrayAdapter != null) arrayAdapter.notifyDataSetChanged();
                    if (successCallback != null) successCallback.run();
                } else {
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Library_UI LIBUI = new Library_UI();
                            LIBUI.snackBar(parent, String.format("%s (%s)", parent.getString(R.string.msg_failed_billing1), responseCode));
                            if (arrayAdapter != null) arrayAdapter.notifyDataSetChanged();
                            if (failureCallback != null) failureCallback.run();
                        }
                    });
                }
            }
        }, SkuType.INAPP);

        if (responseCode != BillingClient.BillingResponse.OK) {
            try {
                Log.e(TAG, "Billing returned error so waiting 5s and trying again");
                Thread.sleep(2000);

                if (isReady()) {
                    repairRoutesPurchased();
                } else {
                    Log.e(TAG, "Billing was not ready so unable to check");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
