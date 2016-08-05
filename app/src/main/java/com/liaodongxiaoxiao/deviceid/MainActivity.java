package com.liaodongxiaoxiao.deviceid;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @BindView(R.id.imei_value)
    TextView imeiValue;
    @BindView(R.id.pseudo_unique_id)
    TextView pseudoUniqueId;
    @BindView(R.id.android_id)
    TextView androidId;
    @BindView(R.id.wlan_mac_address)
    TextView wlanMacAddress;
    @BindView(R.id.bt_mac_address)
    TextView btMacAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //IMEI
        imeiValue.setText(getIMEI());
        //pseudo unique id
        pseudoUniqueId.setText(getPseudoUniqueId());
        //android id
        androidId.setText(getAndroidId());
        // WLan mac address
        wlanMacAddress.setText(getWlanMacAddress());
        // bluetooth mac address
        btMacAddress.setText(getBTMacAddress());
    }

    private String getIMEI() {
        final TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG, "Single or Dula Sim "+tm.getPhoneCount());
            Log.i(TAG, "Single 1 "+tm.getDeviceId(0));
            Log.i(TAG, "Single 2 "+tm.getDeviceId(1));
            return tm.getDeviceId(0)+"\n"+tm.getDeviceId(1);
        }
        return tm.getDeviceId();
    }

    private String getPseudoUniqueId() {
        return "35" + //we make this look like a valid IMEI
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits
    }

    private String getAndroidId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private String getWlanMacAddress() {
        WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        return wm.getConnectionInfo().getMacAddress();
    }

    private String getBTMacAddress() {
        BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return m_BluetoothAdapter.getAddress();
    }

    private String getDeviceId() {
        String m_szLongID = getIMEI() + getPseudoUniqueId() + getAndroidId()
                + getWlanMacAddress() + getBTMacAddress();
        // compute md5
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
        // get md5 bytes
        byte p_md5Data[] = m.digest();
        // create a hex string
        String m_szUniqueID = new String();
        for (int i = 0; i < p_md5Data.length; i++) {
            int b = (0xFF & p_md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper padding)
            if (b <= 0xF) m_szUniqueID += "0";
            // add number to string
            m_szUniqueID += Integer.toHexString(b);
        }
        // hex string to uppercase
        return m_szUniqueID.toUpperCase();

    }
}
