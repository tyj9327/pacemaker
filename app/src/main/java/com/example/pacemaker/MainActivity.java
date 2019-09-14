package com.example.pacemaker;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    // Constants
    private static final float SOJU_VOLUME = 360f;
    private static final float BEER_VOLUME_DEFAULT = 500f;

    // Views
    private Toolbar toolbar;
    private TextView titleToolbar;
    private TextView name;
    private TextView alcoholPercent;
    private float alcoholBarSize;
    private Button btConnectButton;
    private Button btSendData;

    // Charts
    private CombinedChart combinedChart;
    private FrameLayout barchart_top;

    // ChartData
    private int count;

    // Bluetooth
    BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    List<String> pairedDevicesList;
    Handler bluetoothHandler;
    BluetoothSPP btSPP;

    //Data
    public String receivedData;
    public String outgoingData;
    private String userAlcoholCapacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        settingToolbar();
        settingCombinedChart();
        settingBlueTooth();
        setHorizontalBarChart();
        String user_info = getData("USER_NAME") + " " + getData("USER_GENDER");
        name.setText(user_info);



    }

    private void findViews() {
        toolbar = findViewById(R.id.toolbar);
        titleToolbar = findViewById(R.id.title_toolbar);
//        lineChart = findViewById(R.id.line_chart);
        barchart_top = findViewById(R.id.barchart_top);
        name = findViewById(R.id.main_name);
        alcoholPercent = findViewById(R.id.main_bar_percent);
        btConnectButton = findViewById(R.id.bluetooth_connect);
        btSendData = findViewById(R.id.bt_send_data);
//        barChart = findViewById(R.id.bar_chart);
        combinedChart = findViewById(R.id.combined_chart);

    }

    private void settingToolbar() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.home);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.home_white);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.background_start));
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.menubar));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StartActivity.class));
            }
        });

    }

    private void settingCombinedChart() {

        Log.d("taeyoung", "settingCombinedChart");

        count = 10;

        combinedChart.getDescription().setEnabled(false);
        combinedChart.setBackgroundColor(Color.WHITE);
        combinedChart.setDrawGridBackground(false);
        combinedChart.setDrawBarShadow(false);
        combinedChart.setHighlightFullBarEnabled(false);

        combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        Legend l = combinedChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String[] format = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
                Log.d("taeyoung", "format value: " + value);
                return format[(int) value];
            }
        });

        CombinedData cData = new CombinedData();

        cData.setData(generateLineData());
        cData.setData(generateBarData());

        xAxis.setAxisMaximum(cData.getXMax() + 0.25f);

        combinedChart.setData(cData);
        combinedChart.notifyDataSetChanged();
        combinedChart.invalidate();
    }

    private LineData generateLineData() {
        LineData lineData = new LineData();

        ArrayList<Entry> entries = new ArrayList<>();
        float[] datas = {30, 50, 100, 120, 150, 180, 280, 300, 330, 400};
        for(int index = 0; index < count; index++) {
            entries.add(new Entry(index + 1f, datas[index]));
        }

        LineDataSet set = new LineDataSet(entries, "Line DataSet");
        set.setColor(R.color.background_start);
        set.setLineWidth(4f);
        set.setCircleColor(R.color.colorPrimary);
        set.setCircleRadius(5f);
        set.setFillColor(R.color.colorAccent);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(R.color.colorAccent);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineData.addDataSet(set);
        return lineData;

    }

    private BarData generateBarData() {
        ArrayList<BarEntry> entries = new ArrayList<>();

        float[] datas = {30, 20, 50, 20, 30, 30, 100, 20, 30, 70};
        for(int index = 0; index < count; index++) {
            entries.add(new BarEntry(index + 1f, datas[index]));
        }

        BarDataSet set = new BarDataSet(entries, "BAR");
        set.setColor(Color.rgb(60, 220, 78));
        set.setValueTextColor(Color.rgb(60, 220, 78));
        set.setValueTextSize(10f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        float groupSpace = 0.06f;
        float barSpace = 0.02f;
        float barWidth = 0.45f;

        BarData d = new BarData(set);
        d.setBarWidth(barWidth);
//        d.groupBars(0, groupSpace, barSpace);

        return d;
    }


    private void settingBlueTooth() {
        btSPP = new BluetoothSPP(this);

        if(!btSPP.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth 사용 불가", Toast.LENGTH_SHORT).show();
        }

        btSPP.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {
                receivedData = message;
                Toast.makeText(MainActivity.this, "수신 DATA: " + message, Toast.LENGTH_SHORT).show();
            }
        });

        btSPP.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            @Override
            public void onDeviceConnected(String name, String address) {
                btConnectButton.setText("BT해제");
                Toast.makeText(getApplicationContext(), "Connected to " + name + "\n" + address, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceDisconnected() {
                btConnectButton.setText("BT연결");
                Toast.makeText(getApplicationContext(), "Connection lost", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onDeviceConnectionFailed() {
                btConnectButton.setText("BT연결");
                Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        btConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btSPP.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    btSPP.disconnect();
                    btConnectButton.setText("BT연결");
                }else  {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                    btConnectButton.setText("BT연결중");
                }


            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btSPP.stopService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!btSPP.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        }else {
            if(!btSPP.isServiceAvailable()) {
                btSPP.setupService();
                btSPP.startService(BluetoothState.DEVICE_OTHER);
                setDataUp();
            }
        }
    }

    private void setDataUp() {
        btSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "데이터", Toast.LENGTH_SHORT).show();
                btSPP.send(outgoingData, true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK) {
                btSPP.connect(data);
            }
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                btSPP.setupService();
                btSPP.startService(BluetoothState.DEVICE_OTHER);
                setDataUp();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth was not enabled", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setHorizontalBarChart() {
        // bluetooth!

        float alcoholSize = 200f;
        float width = (alcoholSize / SOJU_VOLUME) * 307f;
        Log.d("HORIZONTAL", String.valueOf(width));

        barchart_top.setLayoutParams(new FrameLayout.LayoutParams((int) width, ViewGroup.LayoutParams.MATCH_PARENT));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getData(String key) {
        SharedPreferences sf = getSharedPreferences("taeyoung", MODE_PRIVATE);
        return sf.getString(key, "NONE");
    }
}
