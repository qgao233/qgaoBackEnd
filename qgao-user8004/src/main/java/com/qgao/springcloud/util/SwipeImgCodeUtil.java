package com.qgao.springcloud.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SwipeImgCodeUtil {

    private static int picWidth=300;
    private static int picHeight = 120;
    private static int otlSideLength = 40;//边长40px;
    public static int circleRadius = 10;//圆半径10px
    public static int otlX = 0;//轮廓起始位置
    public static int otlY = 0;
    private static int otlxEnd = 0;
    private static int otlyEnd = 0;


    public static BufferedImage[] cutImage(Resource img) throws IOException {
//        String path = url.getFile().replaceAll("%20"," ");
//        BufferedImage originImg = ImageIO.read(new FileInputStream(path));
        BufferedImage originImg = ImageIO.read(img.getInputStream());
        picHeight = originImg.getHeight();
        picWidth = originImg.getWidth();
        BufferedImage maskImg = new BufferedImage(otlSideLength+2*circleRadius,otlSideLength+2*circleRadius,BufferedImage.TYPE_INT_ARGB);
        BufferedImage targetImg = new BufferedImage(picWidth,picHeight,BufferedImage.TYPE_INT_ARGB);
        int[][] outline = getOutline();
        int maskX = otlX-circleRadius;
        int maskY = otlY-circleRadius;
        int maskxEnd = otlxEnd+circleRadius;
        int maskyEnd = otlyEnd+circleRadius;
        for(int i = 0;i<picHeight;i++) {
            for (int j = 0; j < picWidth; j++) {
                int rgb = outline[i][j];

                int rgb_origin = originImg.getRGB(j,i);
                //mask
                if(j>=maskX && i>=maskY && j<maskxEnd && i<maskyEnd){
                    if(rgb == 1){
                        maskImg.setRGB(j-maskX, i-maskY, rgb_origin);
                    }else if(rgb == 2){
                        maskImg.setRGB(j-maskX, i-maskY, 0xff00bfff);
                    }else if(rgb == 3){
                        maskImg.setRGB(j-maskX, i-maskY, 0xff00bfff);
                    }else{
                        //透明
                        int temp = rgb_origin;
                        rgb_origin &= 0x00ffffff;
                        maskImg.setRGB(j-maskX, i-maskY, rgb_origin);
                        rgb_origin = 0xffffffff & temp;
                    }
                }
                //bg
                if(rgb == 1){
                    int r = (0xff & rgb_origin);
                    int g = (0xff & (rgb_origin >> 8));
                    int b = (0xff & (rgb_origin >> 16));
                    rgb_origin = (0x00ffffff & rgb_origin) | (100 << 24);
                    //原图对应位置颜色变化
                    targetImg.setRGB(j, i, rgb_origin);
                }else{
                    targetImg.setRGB(j,i,rgb_origin);
                }
            }
        }
        return new BufferedImage[]{maskImg,targetImg};

    }

    public static int[][] getOutline(){
        //外0内1
        int[][]data = new int[picHeight][picWidth];
        //随机获得outline的起始位置,直角处
        otlX = (int) (Math.random()*(picWidth-otlSideLength-2*circleRadius)+circleRadius);
        otlY = (int) (Math.random()*(picHeight-otlSideLength-2*circleRadius)+circleRadius);
        otlxEnd = otlX+otlSideLength;
        otlyEnd = otlY+otlSideLength;
        //随机获得圆心的位置，圆心在边长上，此处设置4个圆
        int c1x = (int) (Math.random()*(otlSideLength-2*circleRadius)+(otlX+circleRadius));
        int c1y = otlY;
        int c2x = otlX+otlSideLength;
        int c2y = (int) (Math.random()*(otlSideLength-2*circleRadius)+(otlY+circleRadius));
        int c3x = (int) (Math.random()*(otlSideLength-2*circleRadius)+(otlX+circleRadius));
        int c3y = otlY+otlSideLength;
        int c4x = otlX;
        int c4y = (int) (Math.random()*(otlSideLength-2*circleRadius)+(otlY+circleRadius));

        int CR2 = circleRadius*circleRadius;//r^2

        for(int i = 0;i<picHeight;i++){
            for(int j = 0;j<picWidth;j++){
                int temp1_2 = (i-c1y)*(i-c1y)+(j-c1x)*(j-c1x);
                int temp2_2 = (i-c2y)*(i-c2y)+(j-c2x)*(j-c2x);
                int temp3_2 = (i-c3y)*(i-c3y)+(j-c3x)*(j-c3x);
                int temp4_2 = (i-c4y)*(i-c4y)+(j-c4x)*(j-c4x);

                if(i<otlY){
                    data[i][j] = temp1_2 <= CR2 ? 1 : 0;
                    if(temp1_2 == CR2-1)
                        data[i][j] = 2;
                    if(temp1_2 == CR2-4)
                        data[i][j] = 3;
                }else if(j>otlxEnd) {
                    data[i][j] = temp2_2 <= CR2 ? 1 : 0;
                }else if(i>otlyEnd) {
                    data[i][j] = temp3_2 <= CR2 ? 1 : 0;
                }else if(j<otlX){
                    data[i][j] = temp4_2 <= CR2 ? 1 : 0;
                }else {
                    data[i][j] = 1;
                }
            }
        }
//        for(int i = 0;i<picHeight;i++) {
//            for (int j = 0; j < picWidth; j++) {
//                System.out.print(data[i][j]);
//            }
//            System.out.println();
//        }

        return data;
    }

//    public static void main(String[] args) throws IOException {
////        PictureProcess p = new PictureProcess();
//////        p.getOutline();
////        p.cutImage();
//    }
}
