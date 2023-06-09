package me.driss.test1;

import com.google.gson.annotations.SerializedName;

public class CalculationResponse {
    @SerializedName("Solution")
    private String result;

    public String getResult() {
        return result;
    }
}

