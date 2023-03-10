
#StartAdb

<font color="red">**Root Required**</font>

An app starts the adbd on the device, then you can connect the adb over Wi-Fi.


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startAdb();
        finish();
    }

    private void startAdb() {
        if (isAdbStarted()) {
            String msg = "adbd is on 5555 already";
            Log.i(TAG, msg);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream out = new DataOutputStream(process.getOutputStream());
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            out.writeBytes("setprop service.adb.tcp.port 5555\n");
            out.writeBytes("stop adbd\n");
            out.writeBytes("start adbd\n");
            out.writeBytes("exit\n");
            out.flush();
            String result;
            do {
                result = input.readLine();
                if (result != null) {
                    Log.i(TAG, result);
                }
            } while (result != null);
            out.close();
            input.close();
            Log.i(TAG, "Succeeded to start adbd on 5555");
            Toast.makeText(this, "Succeeded to start adbd on 5555", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Failed to start adb.");
            e.printStackTrace();
        }
    }

    private boolean isAdbStarted() {
        boolean started = false;
        try {
            Process process = Runtime.getRuntime().exec("getprop service.adb.tcp.port");
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String result = input.readLine();
            if (result != null && result.equals("5555")) {
                started = true;
            }
            input.close();
        } catch (Exception e) {
            Log.e(TAG, "Failed to check isAdbStarted.");
            e.printStackTrace();
        }
        return started;
    }

