package org.firstinspires.ftc.teamcode.WIC.util;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.io.File;
import java.io.FilenameFilter;

@TeleOp(name = "Reset Config Files", group = "Utils")
public class ResetConfigFile extends OpMode {
    @Override
    public void init() {
        AppContextProvider.setAppContext(hardwareMap.appContext);

        File wicFolder = ConfMgr.getWicFolder();
        String[] fileNames = wicFolder.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.matches("conf.+\\.txt");
            }
        });
        if (fileNames == null) {
            return;
        }
        for (String fileName : fileNames) {
            File file = new File(wicFolder, fileName);
            boolean deleted = file.delete();
            telemetry.addLine("file "+fileName +
                    (deleted ? " deleted successfully." : " NOT deleted."));
        }
        telemetry.update();
    }

    @Override
    public void loop() {

    }
}
