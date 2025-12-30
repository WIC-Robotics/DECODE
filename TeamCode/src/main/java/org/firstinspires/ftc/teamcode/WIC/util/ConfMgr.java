package org.firstinspires.ftc.teamcode.WIC.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.teamcode.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


/**This class handles saving and loading configurations.<br>
 * It can tolerate white space and hash (#) incidents.
 * When the value is a double, it also handles 'PI', '*' and '/'.<br>
 * It is assumed that the distance unit used everywhere is the INCH. If you find anything else,
 * please change it and raise a flag.
 */
public class ConfMgr {
    static private ConfMgr instance = null;
    public static final String TEST_DEVICE_SERIAL = "8bd3b0c7306629af";
    public static final String CRAB_SERIAL = "4828169831438180";
    public static final String FLYING_FISH_SERIAL = "441bdb8edc88d384";
    public static final String GLOBAL_DEFAULT_SERIAL = "0";

    static Map<String, String> robotName = new HashMap<>();

    static {
        robotName.put(TEST_DEVICE_SERIAL, "TestDevice");
        robotName.put(CRAB_SERIAL, "Crab");
        robotName.put(FLYING_FISH_SERIAL, "Flyingfish");
        robotName.put(GLOBAL_DEFAULT_SERIAL, "GlobalDefault");
    }
    private final Map<String, String> settings = new HashMap<>();
    private static boolean testing = false;
    private static final String DEMO = "DEMO";
    private static Boolean demo = null;

    private ConfMgr() {

        Context context = AppContextProvider.getAppContext();
        if(context == null)
            throw new IllegalStateException("AppContext must be set before any attempt to use ConfMgr. Maybe you forgot to write \"AppContextProvider.setAppContext(hardwareMap.appContext);\" in the beginning of your activity init() function");
        String serial = Build.SERIAL; //TODO find a better way to get serial #
        int internalSettingsFileId;
        System.out.println("deviceSerial = " + serial);
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File wicFolder = new File(externalStorageDirectory, "WICFolder");
        File confFile = new File(wicFolder, "conf"+robotName.get(serial)+".txt");

        if (!wicFolder.exists()){
            System.out.println("creating "+wicFolder);
            boolean created = wicFolder.mkdir();
            System.out.println("new folder was "+(created ? "" : "not ") + "created.");
        }
        if (!confFile.exists()){
            //Pick the appropriate (or global default) settings file.
            switch (serial){
                case TEST_DEVICE_SERIAL:
                    System.out.println("Device is TestDevice");
                    internalSettingsFileId = R.raw.conf8bd3b0c7306629af;
                    break;
                case CRAB_SERIAL:
                    System.out.println("Device is CRAB");
                    internalSettingsFileId = R.raw.conf4828169831438180;
                    break;
                case FLYING_FISH_SERIAL:
                    System.out.println("Device is Flying Fish");
                    internalSettingsFileId = R.raw.conf0; //conf441bdb8edc88d384;
                    break;
                default:
                    System.out.println("UNKNOWN DEVICE! Loading GLOBAL defaults");
                    internalSettingsFileId = R.raw.conf0;
            }
            //and export it
            System.out.println("copying internal file ["+ internalSettingsFileId+"] into ["+ confFile+"]");
            createDefaultConfigFile(confFile, context, internalSettingsFileId);
        }
        testing = serial.equals(TEST_DEVICE_SERIAL);

        try (Scanner scanner = new Scanner(confFile)) {
            fillSettings(scanner);
        } catch (Exception e) {
            System.out.println("could not read config file because of exception");
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
            line = line.replaceAll("#.*", "");
            if (line.isEmpty()) {
                continue;
            }
//            if (line.startsWith("#"))
//                continue;
            String[] tokens = line.split("\\s*=\\s*", 2);
            settings.put(tokens[0], tokens[1]);
            System.out.println("["+tokens[0]+"], ["+tokens[1]+"]");
        }
        scanner.close();
    }

    public String get(String key){
        return settings.get(key);
    }
    public <T> String get(@NonNull Class<T>cls, String key){
        return get(cls.getSimpleName()+"."+key);
    }
    public String get(@NonNull Object o, String key){
        return get(o.getClass(), key);
    }
    public double getDouble(String key){
        String val = get(key);
        if (val == null){
            System.out.println("Unknown Value for key: "+key);
            throw new RuntimeException("Unknown Value for key: "+key);
        }
        val = val.replaceAll("\\s+", "");
        return parseStringToDouble(val);
    }
    public <T> double getDouble(@NonNull Class<T>cls, String key){
        return getDouble(cls.getSimpleName()+"."+key);
    }
    public double getDouble(@NonNull Object o, String key){
        return getDouble(o.getClass(), key);
    }

    public int getInt(String key){
        String val = get(key);
        if (val == null){
            System.out.println("Unknown Value for key: "+key);
            throw new RuntimeException("Unknown Value for key: "+key);
        }
        val = val.replaceAll("\\s+", "");
        return Integer.parseInt(val);
    }
    public <T> int getInt(@NonNull Class<T>cls, String key){
        return getInt(cls.getSimpleName()+"."+key);
    }
    public int getInt(@NonNull Object o, String key){
        return getInt(o.getClass(), key);
    }


    public boolean getBoolean(String key) throws IllegalArgumentException {
        String val = get(key);
        val = val.toLowerCase();
        if(val.matches("[0-9-]+"))
            return ! val.equals("0");
        else if (val.equals("yes") || val.equals("true" ))
            return true;
        else if (val.equals("no" ) || val.equals("false"))
            return false;
        throw new IllegalArgumentException("Invalid boolean value");
    }

    public <T> boolean getBoolean(@NonNull Class<T>cls, String key){
        return getBoolean(cls.getSimpleName()+"."+key);
    }
    public boolean getBoolean(@NonNull Object o, String key){
        return getBoolean(o.getClass(), key);
    }

    private double parseStringToDouble(String string){
        if (string.contains("*")){
            String[] operands = string.split("\\*", 2);
            return parseStringToDouble(operands[0]) * parseStringToDouble(operands[1]);
        }
        if (string.contains("/")){
            String[] operands = string.split("/", 2);
            return parseStringToDouble(operands[0]) / parseStringToDouble(operands[1]);
        }
        if (string.equals("PI"))
            return Math.PI;
        return Double.parseDouble(string);
    }


    public static boolean isTestingDevice() {
        if (instance == null)
            getInstance();
//        System.out.println("testing is " + testing);
        return testing;
    }

    public static boolean isDemo() {
        if (demo == null) {
            ConfMgr mgr = getInstance();
            demo = mgr.settings.containsKey(DEMO) && mgr.getBoolean(DEMO);
        }
        return demo;
    }
}