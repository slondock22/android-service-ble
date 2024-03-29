package com.michaelfotiadis.ibeaconscanner.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.commonsware.cwac.merge.MergeAdapter;
import com.michaelfotiadis.ibeaconscanner.R;
import com.michaelfotiadis.ibeaconscanner.containers.CustomConstants;
import com.michaelfotiadis.ibeaconscanner.utils.TimeFormatter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconType;
import uk.co.alt236.bluetoothlelib.device.beacon.BeaconUtils;
import uk.co.alt236.bluetoothlelib.device.beacon.ibeacon.IBeaconManufacturerData;
import uk.co.alt236.bluetoothlelib.resolvers.CompanyIdentifierResolver;


public class DeviceActivity extends BaseActivity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDisplayHomeAsUpEnabled(true);


        final BluetoothLeDevice device = getIntent().getParcelableExtra(CustomConstants.Payloads.PAYLOAD_1.toString());

        setTitle("tes");

        final ListView listView = (ListView) findViewById(R.id.list_view);

        populateDetails(listView, device);

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_device;
    }

    @SuppressLint("InflateParams")
    private void appendDeviceInfo(final MergeAdapter adapter, final BluetoothLeDevice device) {
        final View layout = getLayoutInflater().inflate(R.layout.list_item_view_device_info, null);
        final TextView textViewName = (TextView) layout.findViewById(R.id.deviceName);
        final TextView textViewAddress = (TextView) layout.findViewById(R.id.deviceAddress);

        textViewName.setText(device.getName());
        textViewAddress.setText(device.getAddress());

        adapter.addView(layout);
    }

    /**
     * Append a header to the MergeAdapter
     *
     * @param adapter {@link MergeAdapter} to be used
     * @param title   String title to be appended
     */
    @SuppressLint("InflateParams")
    private void appendHeader(final MergeAdapter adapter, final String title) {
        final View layout = getLayoutInflater().inflate(R.layout.list_item_view_header, null);
        final TextView textViewTitle = (TextView) layout.findViewById(R.id.title);
        textViewTitle.setText(title);

        adapter.addView(layout);
    }

    /**
     * Append body text to the MergeAdapter
     *
     * @param adapter {@link MergeAdapter} to be used
     * @param data    String text to be appended
     */
    @SuppressLint("InflateParams")
    private void appendSimpleText(final MergeAdapter adapter, final String data) {
        final View lt = getLayoutInflater().inflate(R.layout.list_item_view_textview, null);
        final TextView tvData = (TextView) lt.findViewById(R.id.data);

        tvData.setText(data);

        adapter.addView(lt);
    }

    @SuppressLint("InflateParams")
    private void appendIBeaconInfo(final MergeAdapter adapter, final IBeaconManufacturerData iBeaconData) {
        final View lt = getLayoutInflater().inflate(R.layout.list_item_view_ibeacon_details, null);
        hitAPI(iBeaconData.getUUID());
        final TextView tvCompanyId = (TextView) lt.findViewById(R.id.companyId);
        final TextView tvUUID = (TextView) lt.findViewById(R.id.uuid);
        final TextView tvMajor = (TextView) lt.findViewById(R.id.major);
        final TextView tvMinor = (TextView) lt.findViewById(R.id.minor);
        final TextView tvTxPower = (TextView) lt.findViewById(R.id.txpower);

        tvCompanyId.setText(
                CompanyIdentifierResolver.getCompanyName(iBeaconData.getCompanyIdentifier(), "Not Available")
                        + " (" + hexEncode(iBeaconData.getCompanyIdentifier()) + ")");
        tvUUID.setText(iBeaconData.getUUID());
        tvMajor.setText(iBeaconData.getMajor() + " (" + hexEncode(iBeaconData.getMajor()) + ")");
        tvMinor.setText(iBeaconData.getMinor() + " (" + hexEncode(iBeaconData.getMinor()) + ")");
        tvTxPower.setText(iBeaconData.getCalibratedTxPower() + " (" + hexEncode(iBeaconData.getCalibratedTxPower()) + ")");

        adapter.addView(lt);
    }

    @SuppressLint("InflateParams")
    private void appendRssiInfo(final MergeAdapter adapter, final BluetoothLeDevice device) {
        final View lt = getLayoutInflater().inflate(R.layout.list_item_view_rssi_info, null);
        final TextView tvLastTimestamp = (TextView) lt.findViewById(R.id.lastTimestamp);
        final TextView tvLastRssi = (TextView) lt.findViewById(R.id.lastRssi);

        tvLastTimestamp.setText(formatTime(device.getTimestamp()));
        tvLastRssi.setText(formatRssi(device.getRssi()));

        adapter.addView(lt);
    }

    private String formatRssi(final int rssi) {
        return getString(R.string.formatter_db, String.valueOf(rssi));
    }

    public void hitAPI(final String uuid) {

        RequestQueue queue = Volley.newRequestQueue(this);  // this = context
        String url = "https://trade2gov.com/bleuid";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        Log.d("Response uuid", uuid);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getStackTrace().toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("UID", uuid);
//                params.put("QR_CODE", "JDJ5JDEwJGJ2UzdXTFBIYjlEeE5TZ1FhNjd3WWVjVm9DdHBOZ3NkcFNpdHViRWE1aGxnV01qY1l2WWp5fHxZV2x5YkdGdVoyZGhMbkJoYzIxcGEyRkFaV1JwTFdsdVpHOXVaWE5wWVM1amJ5NXBaQT09fHxlZGlpY211c2Vy");

                return params;
            }
        };
        queue.add(postRequest);
    }

    private void populateDetails(final ListView listView, final BluetoothLeDevice device) {
        final MergeAdapter adapter = new MergeAdapter();

        if (device == null) {
            appendHeader(adapter, "Device Info");
            appendSimpleText(adapter, "Invalid Device");
        } else {
            appendHeader(adapter, "Device Info");
            appendDeviceInfo(adapter, device);

            if (BeaconUtils.getBeaconType(device) == BeaconType.IBEACON) {
                final IBeaconManufacturerData iBeaconData = new IBeaconManufacturerData(device);
                appendHeader(adapter, "iBeacon Data");
                appendIBeaconInfo(adapter, iBeaconData);
            }

            appendHeader(adapter, "RSSI Info");
            appendRssiInfo(adapter, device);

        }

        listView.setAdapter(adapter);
    }

    private static String formatTime(final long time) {
        return TimeFormatter.getIsoDateTime(time);
    }

    private static String hexEncode(final int integer) {
        return "0x" + Integer.toHexString(integer).toUpperCase(Locale.US);
    }


}
