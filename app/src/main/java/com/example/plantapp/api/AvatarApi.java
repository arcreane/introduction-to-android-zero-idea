// AvatarApi.java (Interface)
package com.example.plantapp.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AvatarApi {
    @GET("{avatarId}.png")
    Call<ResponseBody> getAvatar(@Path("avatarId") String avatarId);
}
