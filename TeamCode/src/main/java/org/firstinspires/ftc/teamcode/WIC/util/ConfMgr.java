package org.firstinspires.ftc.teamcode.WIC.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import org.firstinspires.ftc.teamcode.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class ConfMgr {
    static private ConfMgr instance = null;
    public static final String TEST_DEVICE_SERIAL = "8bd3b0c7306629af";
    public static final String CRAB_SERIAL = "4828169831438180";
    public static final String FLYING_FISH_SERIAL = "441bdb8edc88d384";

    static Map<String, String> robotName = new HashMap<>();

    static {
        robotName.put(TEST_DEVICE_SERIAL, "TestDevice");
        robotName.put(CRAB_SERIAL, "Crab");
        robotName.put(FLYING_FISH_SERIAL, "Flyingfish");
        robotName.put("0", "GlobalDefault");
    }
    private final Map<String, String> settings = new HashMap<>();

    private ConfMgr() {

        Context context = AppContextProvider.getAppContext();
        if(context == null)
            throw new IllegalStateException("AppContext must be set before any attempt to use ConfMgr. Maybe you forgot to write \"AppContextProvider.setAppContext(hardwareMap.appContext);\" in the beginning of your activity init() function");
        String serial = Build.SERIAL;
        int internalSettingsFileId = -1;
        System.out.println("deviceSerial = " + serial);
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File wicFolder = new File(externalStorageDirectory, "WICFolder");
        File confFile = new File(wicFolder, "conf"+robotName.get(serial)+".txt");

        if (!wicFolder.exists()){
            System.out.println("creating "+wicFolder);
            boolean made = wicFolder.mkdir();
            System.out.println("new folder was "+(made ? "" : "not ")+"created.");
        }
        if (!confFile.exists()){
            switch (serial){
                case TEST_DEVICE_SERIAL:
                    System.out.println("Device is TestDevice");
                    internalSettingsFileId = R.raw.conf8bd3b0c7306629af;
                    break;
                case CRAB_SERIAL:
                    System.out.println("Device is CRAB");
                    internalSettingsFileId = R.raw.conf0; //conf4828169831438180;
                    break;
                case FLYING_FISH_SERIAL:
                    System.out.println("Device is Flying Fish");
                    internalSettingsFileId = R.raw.conf0; //conf441bdb8edc88d384;
                    break;
                default:
                    System.out.println("UNKNOWN DEVICE! Loading GLOBAL defaults");
                    internalSettingsFileId = R.raw.conf0;
            }
            System.out.println("copying internal file ["+ internalSettingsFileId+"] into ["+ confFile+"]");
            createDefaultConfigFile(confFile, context, internalSettingsFileId);
        }

        try {
            Scanner scanner = new Scanner(confFile);
            fillSettings(scanner);
            scanner.close();
        } catch (Exception e) {
            System.out.println("could not create & write to config file because of exception");
            throw new RuntimeException(e);
        }
    }

    public static ConfMgr getInstance(){
        if (instance == null) {
            instance = new ConfMgr();
        }
        return instance;
    }

    private void createDefaultConfigFile(File externalConfFile, Context context, int resourceId){

        try {
            try (InputStream is = context.getResources().openRawResource(resourceId)) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(externalConfFile)) {
                    while (is.available() > 0) {
                        fileOutputStream.write(is.read());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillSettings(Scanner scanner){
        while (scanner.hasNext()){
            String line = scanner.nextLine();
            if(line == null)
                break;
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("#"))
                continue;
            String[] tokens = line.split("\\s*=\\s*", 2);
            settings.put(tokens[0], tokens[1]);
            System.out.println("["+tokens[0]+"], ["+tokens[1]+"]");
        }
        scanner.close();
    }

    public String get(String key){
        return settings.get(key);
    }
    public <T> String get(Class<T>cls, String key){
        return get(cls.getSimpleName()+"."+key);
    }
    public String get(Object o, String key){
        return get(o.getClass(), key);
    }
    public double getDouble(String key){
        String val = settings.get(key);
        if (val == null){
            System.out.println("Unknown Value for key: "+key);
            throw new RuntimeException("Unknown Value for key: "+key);
        }

        if (val.contains("/")){
            String[] operands = val.split("/");
            return Double.parseDouble(operands[0]) / Double.parseDouble(operands[1]);
        }
        return Double.parseDouble(val);
    }
    public <T> double getDouble(Class<T>cls, String key){
        return getDouble(cls.getSimpleName()+"."+key);
    }
    public double getDouble(Object o, String key){
        return getDouble(o.getClass(), key);
    }


}

//class AppContextProvider {
//    private static Context appContext;
//
//    public static Context getAppContext() {
//        return appContext;
//    }
//
//    public static void setAppContext(Context appContext) {
//        AppContextProvider.appContext = appContext;
//    }
//}