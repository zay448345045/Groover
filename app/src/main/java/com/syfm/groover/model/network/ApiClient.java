package com.syfm.groover.model.network;

import android.text.format.DateFormat;
import android.util.Log;

import com.syfm.groover.model.storage.Const;
import com.syfm.groover.model.storage.databases.AverageScore;
import com.syfm.groover.model.storage.databases.MusicData;
import com.syfm.groover.model.storage.databases.PlayerData;
import com.syfm.groover.model.storage.databases.ResultData;
import com.syfm.groover.model.storage.databases.ScoreRankData;
import com.syfm.groover.model.storage.databases.ShopSalesData;
import com.syfm.groover.model.storage.databases.StageData;
import com.syfm.groover.model.storage.databases.UserRank;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import io.realm.Realm;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by lycoris on 2015/09/27.
 */
public class ApiClient {
    private Realm realm = Realm.getInstance(AppController.getInstance());

    public void tryLogin(final String serial, final String pass) {

        final String url = "https://mypage.groovecoaster.jp/sp/login/auth_con.php";
        final String serialNoKey = "nesicaCardId";
        final String passwordKey = "password";

        RequestBody body = new FormBody.Builder()
                .add(serialNoKey, serial)
                .add(passwordKey, pass)
                .build();


        Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            Response response = AppController.getOkHttpClient().newCall(request).execute();

        } catch (IOException e) {
            Log.d("ktr", e.toString());

        }



    }

    public void fetchPlayerData() {
        Log.d("ktr", "player data");
        String url = "https://mypage.groovecoaster.jp/sp/json/player_data.php";
        String player_data = "player_data";

        Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            Response response = AppController.getOkHttpClient().newCall(request).execute();
            if(!response.isSuccessful()) {
                // TODO: エラー処理
                return;
            }

            JSONObject object = new JSONObject(response.body().string()).getJSONObject(player_data);
            if(object.length() <= 0) {
                return;
            }

            realm.beginTransaction();
            PlayerData playerData = realm.createObjectFromJson(PlayerData.class, object.toString());
            playerData.setDate(DateFormat.format("yyyy/MM/dd kk:mm:ss", Calendar.getInstance()).toString());
            realm.commitTransaction();

        } catch (IOException e) {
            Log.d("ktr", e.toString());

        } catch (JSONException e) {
            Log.d("ktr", e.toString());

        }
    }

    public void fetchShopSalesData() {
        Log.d("ktr", "shop data");
        String url = "https://mypage.groovecoaster.jp/sp/json/shop_sales_data.php";

        Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            Response response = AppController.getOkHttpClient().newCall(request).execute();
            if(!response.isSuccessful()) {
                // TODO: エラー処理
                return;
            }

            JSONObject object = new JSONObject(response.body().string());
            if(object.length() <= 0) {
                return;
            }

            realm.beginTransaction();
            realm.createObjectFromJson(ShopSalesData.class, object);
            realm.commitTransaction();

        } catch (IOException e) {
            Log.d("ktr", e.toString());

        } catch (JSONException e) {
            Log.d("ktr", e.toString());

        }
    }

    public void fetchAverageScore() {
        Log.d("ktr", "ave data");

        String url = "https://mypage.groovecoaster.jp/sp/json/average_score.php";
        String average_data = "average";

        Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            Response response = AppController.getOkHttpClient().newCall(request).execute();
            if(!response.isSuccessful()) {
                // TODO: エラー処理
                return;
            }

            JSONObject object = new JSONObject(response.body().string()).getJSONObject(average_data);
            if(object.length() <= 0) {
                return;
            }

            realm.beginTransaction();
            realm.createObjectFromJson(AverageScore.class, object.toString());
            realm.commitTransaction();

        } catch (IOException e) {
            Log.d("ktr", e.toString());

        } catch (JSONException e) {
            Log.d("ktr", e.toString());

        }
    }

    public void fetchStageData() {
        Log.d("ktr", "stage data");

        String url = "https://mypage.groovecoaster.jp/sp/json/stage_data.php";
        String stage_data = "stage";

        Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            Response response = AppController.getOkHttpClient().newCall(request).execute();
            if(!response.isSuccessful()) {
                // TODO: エラー処理
                return;
            }

            JSONObject object = new JSONObject(response.body().string()).getJSONObject(stage_data);
            if(object.length() <= 0) {
                return;
            }

            realm.beginTransaction();
            realm.createObjectFromJson(StageData.class, object.toString());
            realm.commitTransaction();

        } catch (IOException e) {
            Log.d("ktr", e.toString());

        } catch (JSONException e) {
            Log.d("ktr", e.toString());

        }
    }

    public void fetchMusicData() {
        String url = "https://mypage.groovecoaster.jp/sp/json/music_list.php";
        String music_list_data = "music_list";

        Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            Response response = AppController.getOkHttpClient().newCall(request).execute();
            if(!response.isSuccessful()) {
                // TODO: エラー処理
                return;
            }

            JSONArray array = new JSONObject(response.body().string()).getJSONArray(music_list_data);
            if(array.length() <= 0) {
                return;
            }

            for(int i=0;i<array.length();i++) {
                // For DEBUG
                if(i > 10) {
                    break;
                }

                fetchMusicDetail(array.getJSONObject(i), i, 10 - 1); //実際はlist.size() -1
            }

        } catch (IOException e) {
            Log.d("ktr", e.toString());

        } catch (JSONException e) {
            Log.d("ktr", e.toString());

        }
    }

    public void fetchMusicDetail(final JSONObject music, final int count, final int size) {
        final String url = "https://mypage.groovecoaster.jp/sp/json/music_detail.php?music_id=";
        String music_detail_data = "music_detail";
        String simple = "simple_result_data";
        String normal = "normal_result_data";
        String hard   = "hard_result_data";
        String extra  = "extra_result_data";
        String user_rank = "user_rank";

        // 遅延
        try {
            Thread.sleep(Const.TIME);
        } catch (InterruptedException e) {
            Log.d("ktr", e.toString());
        }

        Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            Response response = AppController.getOkHttpClient().newCall(request).execute();
            if(!response.isSuccessful()) {
                // TODO: エラー処理
                return;
            }

            JSONObject object = new JSONObject(response.body().string()).getJSONObject(music_detail_data);
            if(object.length() <= 0) {
                return;
            }

            resultDataJsonReplaceNull(object, simple);
            resultDataJsonReplaceNull(object, normal);
            resultDataJsonReplaceNull(object, hard);
            resultDataJsonReplaceNull(object, extra);

            userRankJsonReplaceNull(object.getJSONArray(user_rank));

            realm.beginTransaction();

            MusicData data = realm.createObjectFromJson(MusicData.class, object.toString());
            data.setLast_play_time(music.getString(Const.MUSIC_LIST_LAST_PLAY_TIME));
            data.setPlay_count(music.getInt(Const.MUSIC_LIST_LAST_PLAY_TIME));

            // 各難易度をMusicDataの子としてインサート
            // 要素にNULLがあると挙動がおかしくなるので気をつける

            ResultData resultSimple = realm.createObjectFromJson(ResultData.class, object.getJSONObject(simple).toString());
            data.getResult_data().add(resultSimple);

            ResultData resultNormal = realm.createObjectFromJson(ResultData.class, object.getJSONObject(normal).toString());
            data.getResult_data().add(resultNormal);

            ResultData resultHard = realm.createObjectFromJson(ResultData.class, object.getJSONObject(hard).toString());
            data.getResult_data().add(resultHard);

            ResultData resultExtra = realm.createObjectFromJson(ResultData.class, object.getJSONObject(extra).toString());
            data.getResult_data().add(resultExtra);

            // UserRankの整形
            JSONArray userRankRaw = object.getJSONArray(user_rank);

            for (int i = 0; i < userRankRaw.length(); i++) {
                UserRank userRank = realm.createObjectFromJson(UserRank.class, userRankRaw.get(i).toString());
                data.getUser_rank().add(userRank);
            }

            fetchMusicThumbnail(music.getInt(Const.MUSIC_LIST_MUSIC_ID), data, count, size);

        } catch (IOException e) {
            Log.d("ktr", e.toString());

        } catch (JSONException e) {
            Log.d("ktr", e.toString());

        }

    }

    public void fetchMusicThumbnail(int id, final MusicData musicData, final int count, final int maxSize) {
        String url = "https://mypage.groovecoaster.jp/sp/music/music_image.php?music_id=";

        Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            Response response = AppController.getOkHttpClient().newCall(request).execute();
            if(!response.isSuccessful()) {
                // TODO: エラー処理
                realm.cancelTransaction();
                return;
            }

            if(response.body().bytes().length <= 0) {
                realm.cancelTransaction();
                return;
            }

            musicData.setMusic_thumbnail(response.body().bytes());
            realm.commitTransaction();

        } catch (IOException e) {
            Log.d("ktr", e.toString());
            realm.cancelTransaction();

        }

    }

    public void fetchScoreRanking(final String id, final int diff) {
        final String url = "https://mypage.groovecoaster.jp/sp/json/score_ranking_bymusic_bydifficulty.php?music_id=" + id + "&difficulty=" + diff;

        String score_rank = "score_rank";

        Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            Response response = AppController.getOkHttpClient().newCall(request).execute();
            if(!response.isSuccessful()) {
                // TODO: エラー処理
                return;
            }

            JSONArray array = new JSONObject(response.body().string()).getJSONArray(score_rank);
            if(array.length() <= 0) {
                return;
            }

            realm.executeTransaction(realm -> {
                for (int i = 0; i < 5; i++) {
                    try {
                        ScoreRankData item = realm.createObjectFromJson(ScoreRankData.class, array.getJSONObject(i));
                        //item.setId(i + "_" + diff + id);
                        item.setDiff(String.valueOf(diff));
                    } catch (JSONException e) {
                        Log.d("JSONException", "at id:" + id + " diff:" + diff + " " + e.toString());
                    }
                }
            }, new Realm.Transaction.Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(Exception e) {
                    Log.d("ktr", e.toString());
                }
            });

        } catch (IOException e) {
            Log.d("ktr", e.toString());

        } catch (JSONException e) {
            Log.d("ktr", e.toString());

        }
    }

    // TODO: すごく汚いから治したい
    // nullで返ってくるデータをnull以外に整形する
    private void resultDataJsonReplaceNull(JSONObject obj, String key) {
        if (obj.isNull(key) && obj.has(key)) {
            JSONObject result = new JSONObject();
            try {
                result.put(Const.MUSIC_RESULT_ADLIB, 0);
                result.put(Const.MUSIC_RESULT_FULL_CHAIN, 0);
                result.put(Const.MUSIC_RESULT_IS_CLEAR_MARK, "false");
                result.put(Const.MUSIC_RESULT_IS_FAILED_MARK, "false");
                result.put(Const.MUSIC_RESULT_MAX_CHAIN, 0);
                result.put(Const.MUSIC_RESULT_LEVEL, "-");
                result.put(Const.MUSIC_RESULT_NO_MISS, 0);
                result.put(Const.MUSIC_LIST_PLAY_COUNT, 0);
                result.put(Const.MUSIC_RESULT_RATING, "-");
                result.put(Const.MUSIC_RESULT_SCORE, 0);
                obj.put(key, result);
            } catch (JSONException e) {
                Log.d("JSONException", e.toString());
            }

        }
    }

    // TODO: すごく汚いから治したい
    // nullで返ってくるデータをnull以外に整形する
    private void userRankJsonReplaceNull(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            if (array.isNull(i)) {
                JSONObject rank = new JSONObject();
                try {
                    rank.put(Const.MUSIC_USER_RANK, 0);
                    array.put(i, rank);
                } catch (JSONException e) {
                    Log.d("JSONException", e.toString());
                }

            }
        }
    }

}
