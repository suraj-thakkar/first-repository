package com.pack1;

import org.monte.media.Format;
import org.monte.screenrecorder.ScreenRecorder;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

class SpecializedScreenRecorder extends ScreenRecorder {

    private final String name;

    public SpecializedScreenRecorder(GraphicsConfiguration cfg,
                                     Rectangle captureArea,
                                     Format fileFormat,
                                     Format screenFormat,
                                     Format mouseFormat,
                                     Format audioFormat,
                                     File movieFolder,
                                     String name)
            throws IOException, AWTException {

        super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
        this.name = name;
    }

    @Override
    protected File createMovieFile(Format fileFormat) throws IOException {
        return new File(
                movieFolder,
                name + "_" +
                        new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".avi"
        );
    }
}

