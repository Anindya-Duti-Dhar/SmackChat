package anindya.sample.smackchat.utils;


import anindya.sample.smackchat.model.UserList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;


public interface ApiInterface<T> {

    @GET("users")
    Call<UserList> getAllUser(
            @Header("Content-Type") String contentType,
            @Header("Authorization") String serverKey,
            @Header("Accept") String AcceptType);

}
