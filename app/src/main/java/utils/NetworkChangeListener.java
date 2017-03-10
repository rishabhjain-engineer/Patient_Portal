package utils;


/**
 * Created by ayaz on 5/12/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hs.userportal.AppAplication;

import java.util.ArrayList;
import java.util.List;

public class NetworkChangeListener extends BroadcastReceiver {

    private static List<onNetworkChanged> mNetworkChangeListenersList = new ArrayList<onNetworkChanged>();

    private enum ConnectionType {
        MOBILE,
        WIFIE,
        NONE,
        NOTKNOWN
    }

    private NetworkInfo mNetworkInfo;

    public interface onNetworkChanged {
        void netWorkStatusChanged(CurrentNetworkInfo currentNetworkInfo);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkInfo = connectivityManager.getActiveNetworkInfo();
        CurrentNetworkInfo currentNetworkInfo = null;
        if (mNetworkInfo == null) {
            currentNetworkInfo = new CurrentNetworkInfo(false, false, ConnectionType.NONE);
        } else {
            if (mNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                currentNetworkInfo = new CurrentNetworkInfo(mNetworkInfo.isConnected(), mNetworkInfo.isRoaming(), ConnectionType.MOBILE);
            } else if (mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                currentNetworkInfo = new CurrentNetworkInfo(mNetworkInfo.isConnected(), mNetworkInfo.isRoaming(), ConnectionType.WIFIE);
            } else {
                currentNetworkInfo = new CurrentNetworkInfo(mNetworkInfo.isConnected(), mNetworkInfo.isRoaming(), ConnectionType.NOTKNOWN);
            }
        }

        for (onNetworkChanged networkChanged : mNetworkChangeListenersList) {
            networkChanged.netWorkStatusChanged(currentNetworkInfo);
        }
    }

    public static void registerNetworkChangeListener(onNetworkChanged networkChangeListener) {
        mNetworkChangeListenersList.add(networkChangeListener);
    }

    public static void unRegisterNetworkChangeListener(onNetworkChanged networkChangeListener) {
        mNetworkChangeListenersList.remove(networkChangeListener);
    }


    public static CurrentNetworkInfo getNetworkStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager) AppAplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        CurrentNetworkInfo currentNetworkInfo = null;
        if (networkInfo == null) {
            currentNetworkInfo = new CurrentNetworkInfo(false, false, ConnectionType.NONE);
        } else {
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                currentNetworkInfo = new CurrentNetworkInfo(networkInfo.isConnected(), networkInfo.isRoaming(), ConnectionType.MOBILE);
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                currentNetworkInfo = new CurrentNetworkInfo(networkInfo.isConnected(), networkInfo.isRoaming(), ConnectionType.WIFIE);
            } else {
                currentNetworkInfo = new CurrentNetworkInfo(networkInfo.isConnected(), networkInfo.isRoaming(), ConnectionType.NOTKNOWN);
            }
        }
        return currentNetworkInfo;
        //return null;
    }

    public static class CurrentNetworkInfo {
        private boolean isConnected;
        private boolean isRoaming;
        private ConnectionType connectionType;

        public CurrentNetworkInfo(boolean isConnected, boolean isRoaming, ConnectionType connectionType) {
            this.isConnected = isConnected;
            this.isRoaming = isRoaming;
            this.connectionType = connectionType;
        }

        public boolean isConnected() {
            return isConnected;
        }

        public boolean isRoaming() {
            return isRoaming;
        }

        public ConnectionType getConnectionType() {
            return connectionType;
        }

    }
}


