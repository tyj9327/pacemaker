package com.example.pacemaker;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.listeners.ColorListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    // Constants
    private static final float SOJU_VOLUME = 360f;
    private static final float BEER_VOLUME_DEFAULT = 500f;
    private static final String TAG = "taeyoung";
    private static final int COLOR_PICKER_REQUEST_CODE = 3000;

    // Views
    private Toolbar toolbar;
    private TextView titleToolbar;
    private TextView name;
    private TextView horizontalBarCapacity;
    private TextView alcoholPercent;
    private float alcoholBarSize;
//    private Button btConnectButton;
//    private Button btSendData;
//    private Button testSubmit;
//    private EditText testDataEditText;
    private ImageView colorPickerPopup;
    private FrameLayout bottomLayout;
    private ImageView sojuSelect;
    private ImageView beerSelect;
    private View cupColor;

    // Charts
    private CombinedChart combinedChart;
    private FrameLayout barchart_top;
    ArrayList<String> xAxisFormat = new ArrayList<>();
    ArrayList<Float> lineChartData = new ArrayList<>();
    ArrayList<Float> barChartData = new ArrayList<>();

    // ChartData
    private int count = 1;
    private float testData;
    private float stackedTestData;

    // Bluetooth
    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> mPairedDevices;
    List<String> mListPairedDevices;
    Handler mBluetoothHandler;
    ConnectedBluetoothThread mThreadConnectedBluetooth;
    BluetoothDevice mBluetoothDevice;
    BluetoothSocket mBluetoothSocket;
    Boolean isBluetoothConnected = false;

    // Bluetooth constants
    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");



    //Data
    public String receivedData;
    public int outGoingColorData = R.color.background_start;
    public String outGoingData = String.valueOf(outGoingColorData); // color
    private String userAlcoholCapacity;
    private int percentage = 0;
    private int[] outGoingColorDataIntArray;
    private int opacity = 0;

    //etc
    public String currentTime;
    public String currentAlcohol = "SOJU";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        settingToolbar();
        settingBlueTooth();
        String user_info = getData("USER_NAME") + " " + "님"; //+ getData("USER_GENDER");
        name.setText(user_info);

        // initial value for the chart ( test )
        updateCurrentTime();
        xAxisFormat.add(currentTime);
        testData = 0;
        stackedTestData = testData; 
        settingCombinedChart();
        setHorizontalBarChart(currentAlcohol);
        setColorPickerPopup();
        selectWhichAlcoholToDrink();

        // for Test ( change to Bluetooth incoming data afterward )
//        testSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                try{
//                    testData = Float.valueOf(testDataEditText.getText().toString());
//                    stackedTestData += testData;
//                    count++;
//                    updateCurrentTime();
//                    xAxisFormat.add(currentTime);
//                    settingCombinedChart();
//                    setHorizontalBarChart(currentAlcohol);
//                } catch (Exception e){
//                    Toast.makeText(getApplicationContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


        /**
         * Receiving Data from Arduino in real time, adding data to the variable
         */
        mBluetoothHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == BT_MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), readMessage, Toast.LENGTH_SHORT).show();
                    if (readMessage != null) {
                        receivedData = readMessage;
                        testData = Float.valueOf(receivedData);
                        stackedTestData += testData;
                        count++;
                        updateCurrentTime();
                        xAxisFormat.add(currentTime);
                        settingCombinedChart();
                        setHorizontalBarChart(currentAlcohol);
                    }
                }
            }
        };

    }

    private void findViews() {
        toolbar = findViewById(R.id.toolbar);
        titleToolbar = findViewById(R.id.title_toolbar);
        barchart_top = findViewById(R.id.barchart_top);
        name = findViewById(R.id.main_name);
        alcoholPercent = findViewById(R.id.main_bar_percent);
//        btConnectButton = findViewById(R.id.bluetooth_connect);
//        btSendData = findViewById(R.id.bt_send_data);
        combinedChart = findViewById(R.id.combined_chart);
        horizontalBarCapacity = findViewById(R.id.horizontal_chart_capacity);
        colorPickerPopup = findViewById(R.id.color_picker_popup);
        bottomLayout = findViewById(R.id.bottom_frame_layout);
        sojuSelect = findViewById(R.id.soju_select_img);
        beerSelect = findViewById(R.id.beer_select_img);
        cupColor = findViewById(R.id.cup_color_rectangle);

        // test
//        testSubmit = findViewById(R.id.submit_test);
//        testDataEditText = findViewById(R.id.for_test_edit_text);

    }

    private void settingBlueTooth() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
                startActivity(new Intent(MainActivity.this, AccountActivity.class));
            }
        });


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                listPairedDevices();
                return false;
            }
        });
    }


    private void settingCombinedChart() {

        Log.d("taeyoung", "settingCombinedChart");
        Log.d("taeyoung", "count: " + count);
        Log.d("taeyoung", "testData: " + testData);
        Log.d(TAG, "stackData: " + stackedTestData);

        combinedChart.getDescription().setEnabled(false);
        combinedChart.setBackgroundColor(Color.WHITE);
        combinedChart.setDrawGridBackground(false);
        combinedChart.setDrawBarShadow(false);
        combinedChart.setHighlightFullBarEnabled(false);
        combinedChart.enableScroll();

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
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                Log.d("taeyoung", "format value: " + value);
                return xAxisFormat.get((int) value);
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

        lineChartData.add(stackedTestData);
        for(int index = 0; index < count; index++) {
            entries.add(new Entry(index, lineChartData.get(index)));
        }

        LineDataSet set = new LineDataSet(entries, "누적 음주량");
        set.setColor(R.color.background_start);
        set.setLineWidth(4f);
        set.setCircleColor(R.color.colorPrimary);
        set.setCircleRadius(3f);
        set.setFillColor(R.color.colorAccent);
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setDrawValues(false);
        set.setValueTextSize(10f);
        set.setValueTextColor(R.color.colorAccent);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineData.addDataSet(set);
        return lineData;

    }

    private BarData generateBarData() {
        ArrayList<BarEntry> entries = new ArrayList<>();

        barChartData.add(testData);
        for(int index = 0; index < count; index++) {
            entries.add(new BarEntry(index, barChartData.get(index)));
        }

        BarDataSet set = new BarDataSet(entries, "개별 음주량");
        set.setColor(Color.rgb(255, 30, 77));
        set.setValueTextColor(Color.rgb(255, 30, 77));
        set.setValueTextSize(10f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        float barWidth = 0.45f;

        BarData d = new BarData(set);
        d.setBarWidth(barWidth);

        return d;
    }



    private void setColorPickerPopup() {
        colorPickerPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ColorPickerDialog.Builder colorPickerDialog = new ColorPickerDialog.Builder(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

                colorPickerDialog.setTitle("LED 색상 선택")
                        .setPreferenceName("MyColorPickerDialog")
                        .setPositiveButton(getString(R.string.confirm),
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
//                                        outGoingData = envelope.getHexCode();
                                        outGoingColorDataIntArray = envelope.getArgb();
                                        String firstData = "";
                                        for(int colorDataInt : outGoingColorDataIntArray) {
                                            firstData += colorDataInt + " ";
                                        }

                                        if(isBluetoothConnected) {
                                            mThreadConnectedBluetooth.write(firstData);
                                        }

                                        cupColor.setBackgroundColor(envelope.getColor());
                                        cupColor.setAlpha((float) percentage / 100f);
//                                        Toast.makeText(getApplicationContext(), "설정 직후 색: " + firstData, Toast.LENGTH_SHORT).show();

                                        try {
                                            Thread.sleep(1000);
                                        }catch (InterruptedException e){
                                            e.printStackTrace();
                                        }

                                        outGoingColorDataIntArray[0] = opacity;
                                        String secondData = "";
                                        for(int colorDataInt : outGoingColorDataIntArray) {
                                            secondData += colorDataInt + " ";
                                        }

                                        if(isBluetoothConnected) {
                                            mThreadConnectedBluetooth.write(secondData);
                                        }
                                        Toast.makeText(getApplicationContext(), "설정된 색: " + secondData, Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .setNegativeButton(getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                        .attachAlphaSlideBar(false) // default is true. If false, do not show the AlphaSlideBar.
                        .attachBrightnessSlideBar(true)  // default is true. If false, do not show the BrightnessSlideBar.
                        .show();
            }
        });
    }

    private void setHorizontalBarChart(String whichAlcohol) {
        // bluetooth!

        float volume = 0f;
        float userAlcoholCapacity = Float.valueOf(getData("USER_ALCOHOL_CAPACITY"));

        if (whichAlcohol.equals("SOJU")) {
            Log.d(TAG, "whichAlcohol: SOJU");
            volume = SOJU_VOLUME * userAlcoholCapacity;
            horizontalBarCapacity.setText(userAlcoholCapacity + "병\n" + volume + "ml");
        } else if(whichAlcohol.equals("BEER")) {
            Log.d(TAG, "whichAlcohol: BEER");
            volume = SOJU_VOLUME * userAlcoholCapacity * 0.16f / 0.05f;
            float beerCapacityBy500ML = (float) Math.floor(volume / 500f * 10) / 10;

            horizontalBarCapacity.setText(beerCapacityBy500ML + "잔\n" + volume + "ml");
        }

        // 1075 luna
        // 1025 s6
        // 512 a5
        float width = 1075f * (stackedTestData / volume);
        percentage = (int) (stackedTestData / volume * 100);
        Log.d(TAG, "user_alcohol_capacity: " + userAlcoholCapacity);
        Log.d(TAG, "volume: " + volume);
        Log.d(TAG, "width: " + width);
        Log.d(TAG, "percentage: " + percentage);




        alcoholPercent.setText(percentage + "%");
        if(percentage < 13) {
            alcoholPercent.setVisibility(View.INVISIBLE);
        }else{
            alcoholPercent.setVisibility(View.VISIBLE);
        }
        barchart_top.setLayoutParams(new FrameLayout.LayoutParams((int) width, ViewGroup.LayoutParams.MATCH_PARENT));

        cupColor.setAlpha((float) percentage / 100f);
        opacity = (int) (percentage / 100f) * 255;

        // opacity 수정해서 새로운 색 데이터 전송
//        if(btSPP != null) {
//            btSPP.send(outGoingData, true);
//        }

    }

    private void selectWhichAlcoholToDrink() {
        sojuSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sojuSelect.setImageDrawable(getDrawable(R.drawable.soju_2));
                beerSelect.setImageDrawable(getDrawable(R.drawable.beer_1));
                currentAlcohol = "SOJU";
                setHorizontalBarChart(currentAlcohol);
            }
        });

        beerSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sojuSelect.setImageDrawable(getDrawable(R.drawable.soju_1));
                beerSelect.setImageDrawable(getDrawable(R.drawable.beer_2));
                currentAlcohol = "BEER";
                setHorizontalBarChart(currentAlcohol);
            }
        });
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

    public void updateCurrentTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
        currentTime = sdfNow.format(date);
    }

    private String getData(String key) {
        SharedPreferences sf = getSharedPreferences("taeyoung", MODE_PRIVATE);
        return sf.getString(key, "NONE");
    }


    /**
     * Bluetooth Module Setting
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BT_REQUEST_ENABLE:
                if (resultCode == RESULT_OK) { // 블루투스 활성화를 확인을 클릭하였다면
                    Toast.makeText(getApplicationContext(), "블루투스 활성화", Toast.LENGTH_LONG).show();

                } else if (resultCode == RESULT_CANCELED) { // 블루투스 활성화를 취소를 클릭하였다면
                    Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_LONG).show();

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void listPairedDevices() {
        if (mBluetoothAdapter.isEnabled()) {
            mPairedDevices = mBluetoothAdapter.getBondedDevices();

            if (mPairedDevices.size() > 0) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                builder.setTitle("장치 선택");

                mListPairedDevices = new ArrayList<String>();
                for (BluetoothDevice device : mPairedDevices) {
                    mListPairedDevices.add(device.getName());
                    //mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }
                final CharSequence[] items = mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);
                mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        connectSelectedDevice(items[item].toString());
                        Toast.makeText(getApplicationContext(), "연결: " + items[item].toString(), Toast.LENGTH_SHORT).show();
                        isBluetoothConnected = true;
                    }
                });
                androidx.appcompat.app.AlertDialog alert = builder.create();
                alert.show();
            } else {
                isBluetoothConnected = false;
                Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            isBluetoothConnected = false;
            Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    void connectSelectedDevice(String selectedDeviceName) {
        for(BluetoothDevice tempDevice : mPairedDevices) {
            if (selectedDeviceName.equals(tempDevice.getName())) {
                mBluetoothDevice = tempDevice;
                break;
            }
        }

        Log.d("taeyoung", "connectSelectedDevice");

        try {
            Log.d("taeyoung", "inside try");
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID);
            mBluetoothSocket.connect();
            mThreadConnectedBluetooth = new ConnectedBluetoothThread(mBluetoothSocket);
            mThreadConnectedBluetooth.start();
            mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
            isBluetoothConnected = true;


        } catch (IOException e) {
            Log.d("taeyoung", "catch");
            isBluetoothConnected = false;
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
        }
    }

    private class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(String str) {
            byte[] bytes = str.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }



}


