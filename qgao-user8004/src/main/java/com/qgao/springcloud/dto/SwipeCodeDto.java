package com.qgao.springcloud.dto;

import java.io.Serializable;
import java.util.Arrays;

public class SwipeCodeDto implements Serializable{

    private static final long serialVersionUID = -91489657244065578L;

    private int otlY;
    private ImageDetail[] imageDetails;

    public SwipeCodeDto() {
    }

    public int getOtlY() {
        return otlY;
    }

    public void setOtlY(int otlY) {
        this.otlY = otlY;
    }

    public ImageDetail[] getImageDetails() {
        return imageDetails;
    }

    public void setImageDetails(ImageDetail[] imageDetails) {
        this.imageDetails = imageDetails;
    }

    @Override
    public String toString() {
        return "ValidateCodeDto{" +
                "otlY=" + otlY +
                ", imageDetails=" + Arrays.toString(imageDetails) +
                '}';
    }

    public class ImageDetail{
        private String name;
        private String value;

        public ImageDetail(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }
}
