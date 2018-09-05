package anindya.sample.smackchat.utils;

import android.util.Log;

import java.util.List;

import anindya.sample.smackchat.model.UserList;
import anindya.sample.smackchat.model.Users;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class ApiCalls {

    private onLoadUserListener onLoadUserListener = null;

    public interface onLoadUserListener {
        void onHttpResponse(List<Users> users);
    }

    public void setLoadUserListener(onLoadUserListener listener) {
        onLoadUserListener = listener;
    }

    public void getAllUsers(){
        Call<UserList> call = ApiClient.getClient().create(ApiInterface.class).getAllUser("application/json", "MP8KwYlwoCP1rUdY", "application/json");
        call.enqueue(new Callback<UserList>() {
            @Override
            public void onResponse(Call<UserList> call, Response<UserList> response) {
                Log.d("xmpp: ", "API onResponse" + response.toString());
                if (response.isSuccessful()) {
                    UserList userList = response.body();
                    List<Users> users = userList.getUsers();
                    doneAllUserApiCall(users);
                } else {
                    doneAllUserApiCall(null);
                }
            }

            @Override
            public void onFailure(Call<UserList> call, Throwable t) {
                Log.d("xmpp: ", "API onFailure: "+t.getMessage());
                doneAllUserApiCall(null);
            }
        });
    }

    public void doneAllUserApiCall(List<Users> users) {
        if (onLoadUserListener != null) {
            onLoadUserListener.onHttpResponse(users);
        }
    }

}
