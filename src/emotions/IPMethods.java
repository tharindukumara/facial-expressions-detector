/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package emotions;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 *
 * @author Tharindu
 */
public class IPMethods {
    public static BufferedImage toGScale(BufferedImage meanImg) {

        int red, green, blue, alpha;
        int gScale;

        BufferedImage dupImg = new BufferedImage(meanImg.getWidth(), meanImg.getHeight(), meanImg.getType());

        for (int a = 0; a < meanImg.getWidth(); a++) {
            for (int b = 0; b < meanImg.getHeight(); b++) {

                red = new Color(meanImg.getRGB(a, b)).getRed();
                green = new Color(meanImg.getRGB(a, b)).getGreen();
                blue = new Color(meanImg.getRGB(a, b)).getBlue();
                alpha = new Color(meanImg.getRGB(a, b)).getAlpha();

                //gScale = (red+green+blue)/3;
                gScale = (int) ((red * 0.299) + (green * 0.587) + (blue * 0.114));
                gScale = create_rgb(alpha, gScale, gScale, gScale);
                dupImg.setRGB(a, b, gScale);

            }
        }
        return dupImg;
    }

    public static int create_rgb(int alpha, int r, int g, int b) {
        int rgb = (alpha << 24) + (r << 16) + (g << 8) + b;
        return rgb;
    }

    public static BufferedImage contrastStretching(BufferedImage in) {
        int width = in.getWidth();
        int height = in.getHeight();
        int min = 0;  //stretch min level
        int max = 255; //stretch max level
        //System.out.println("width=" + width + " height=" + height);
        try {
            int[] r = new int[width * height];
            int[] g = new int[width * height];
            int[] b = new int[width * height];
            int[] e = new int[width * height];
            int[] data = new int[width * height];
            in.getRGB(0, 0, width, height, data, 0, width);

            int rmin = 255;
            int bmin = 255;
            int gmin = 255;
            int rmax = 0;
            int bmax = 0;
            int gmax = 0;
            for (int i = 0; i < (height * width); i++) {
                r[i] = (int) ((data[i] >> 16) & 0xff);  //shift 3rd byte to first byte location
                g[i] = (int) ((data[i] >> 8) & 0xff);   //shift 2nd byte to first byte location
                b[i] = (int) (data[i] & 0xff);           //it is already at first byte location

                if (rmin > r[i]) {
                    rmin = r[i];
                }
                if (bmin > b[i]) {
                    bmin = b[i];
                }
                if (gmin > g[i]) {
                    gmin = g[i];
                }

                if (rmax < r[i]) {
                    rmax = r[i];
                }
                if (bmax < b[i]) {
                    bmax = b[i];
                }
                if (gmax < g[i]) {
                    gmax = g[i];
                }
            }
            //convert it back
            for (int i = 0; i < (height * width); i++) {
                // System.out.println(""+r[i]);
                r[i] = (r[i] - rmin) * ((max - min) / (rmax - rmin)) + min;
                b[i] = (b[i] - bmin) * ((max - min) / (bmax - bmin)) + min;
                g[i] = (g[i] - gmin) * ((max - min) / (gmax - gmin)) + min;
                //System.out.println(""+r[i]);
                e[i] = (r[i] << 16) | (g[i] << 8) | b[i];
            }
            //convert e back to say jpg
            in.setRGB(0, 0, width, height, e, 0, width);


        } catch (Exception e) {
            System.err.println("Error: " + e);
            Thread.dumpStack();

        }
        return in;
    }

    public static BufferedImage medianFilter(BufferedImage in) {

        int width = in.getWidth();
        int height = in.getHeight();
        int[][] medianVals = new int[height][width];
        int[][] r = new int[height][width];
        int[][] g = new int[height][width];
        int[][] b = new int[height][width];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                Color c = new Color(in.getRGB(i, j));
                r[j][i] = c.getRed();
                g[j][i] = c.getGreen();
                b[j][i] = c.getBlue();

            }
            // System.out.println();
        }
        int intArray[] = new int[9];
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {

                if (row == 0 || row == width - 1 || col == 0 || col == height - 1) {

                    medianVals[col][row] = in.getRGB(row, col);

                } else {

                    intArray[0] = in.getRGB(row - 1, col - 1);
                    intArray[1] = in.getRGB(row - 1, col);
                    intArray[2] = in.getRGB(row - 1, col + 1);
                    intArray[3] = in.getRGB(row, col - 1);
                    intArray[4] = in.getRGB(row, col);
                    intArray[5] = in.getRGB(row, col + 1);
                    intArray[6] = in.getRGB(row + 1, col - 1);
                    intArray[7] = in.getRGB(row + 1, col);
                    intArray[8] = in.getRGB(row + 1, col + 1);

                    Arrays.sort(intArray);

                    medianVals[col][row] = intArray[4];
                }
            }
        }
        
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {

               in.setRGB(row, col, medianVals[col][row]);
            }
        }



        return in;

    }
}
