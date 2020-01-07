package com.yibao.ml.cv2;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

/**
 * @author liuwenyi
 * @date 2019/12/26
 */
public class Picture {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        File imgFile = new File("/Users/liuwenyi/IdeaProjects/yibao-data/ml/data/20180609211149477.png");
        String dest = "/Users/liuwenyi/IdeaProjects/yibao-data/ml/data";
        Mat src = Imgcodecs.imread(imgFile.toString(), Imgcodecs.IMREAD_ANYCOLOR);

        Mat dst = new Mat();

        Imgproc.adaptiveThreshold(src, dst, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 13, 5);
        Imgcodecs.imwrite(dest + "/AdaptiveThreshold" + imgFile.getName(), dst);

    }
}
