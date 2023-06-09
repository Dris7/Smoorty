package me.driss.test1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CalculateService {
    @GET("/calculate")
    Call<CalculationResponse> calculateEquation(@Query("equation") String equation);
}

