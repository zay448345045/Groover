package com.syfm.groover.presenters.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.syfm.groover.R;
import com.syfm.groover.business.usecases.MusicDataUseCase;
import com.syfm.groover.data.storage.Const;
import com.syfm.groover.data.storage.databases.MusicData;
import com.syfm.groover.data.storage.databases.ScoreRankData;
import com.syfm.groover.presenters.adapter.MusicScoreRankingAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by lycoris on 2016/01/10.
 */
public class MusicDetailRankingFragment extends Fragment {

    @Bind(R.id.lv_music_detail_ranking)
    ListView listView;

    private Realm realm;
    private int id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getActivity().getIntent();
        this.id = i.getIntExtra(Const.INTENT_MUSIC_ID, 0);

        if (id == 0) {
            getActivity().finish();
        }

        MusicDataUseCase useCase = new MusicDataUseCase();
        useCase.getScoreRanking(String.valueOf(id));
        Log.d("Unko", "Start");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_detail_ranking, group, false);
        ButterKnife.bind(this, view);

        realm = Realm.getInstance(getActivity());
        MusicData item = realm.where(MusicData.class).equalTo(Const.MUSIC_LIST_MUSIC_ID, id).findFirst();
        // TODO: music_idをMusicScoreRankingに追加する
        //realm.where(ScoreRankData.class).equalTo()
        //MusicScoreRankingAdapter adapter = new MusicScoreRankingAdapter(getActivity(), 0, , true);
        //listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        new MusicDataUseCase().getMusicData();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
    }

    public void onEventMainThread(Boolean ready) {
        Log.d("Unko", "Fragment coming");
        RealmResults<ScoreRankData> ranks = realm.where(ScoreRankData.class).contains(Const.MUSIC_SCORE_DATA_DIFF, "0").findAll();
        MusicScoreRankingAdapter adapter = new MusicScoreRankingAdapter(getActivity(), 0, ranks, true);
        listView.setAdapter(adapter);
    }
}
