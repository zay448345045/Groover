package com.syfm.groover.presenters.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.syfm.groover.R;
import com.syfm.groover.business.usecases.PlayDataUseCase;
import com.syfm.groover.data.storage.databases.AverageScore;
import com.syfm.groover.data.storage.databases.PlayerData;
import com.syfm.groover.data.storage.databases.ShopSalesData;
import com.syfm.groover.data.storage.databases.StageData;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by lycoris on 2015/09/22.
 */
public class PlayDataFragment extends Fragment {

    @Bind(R.id.tv_player_data_name)
    TextView player_name;
    @Bind(R.id.tv_player_data_level)
    TextView player_level;
    @Bind(R.id.tv_player_data_avatar_title)
    TextView player_avatar_title;
    @Bind(R.id.tv_player_data_total_score)
    TextView player_total_score;
    @Bind(R.id.tv_player_data_rank)
    TextView player_rank;
    @Bind(R.id.tv_player_data_coin)
    TextView player_coin;
    @Bind(R.id.tv_player_data_trophy)
    TextView player_trophy;

    @Bind(R.id.tv_music_result_play_music)
    TextView music_play_music;
    @Bind(R.id.tv_music_result_play_music_per)
    TextView music_play_music_per;
    @Bind(R.id.tv_music_result_clear_stage)
    TextView music_clear_stage;
    @Bind(R.id.tv_music_result_clear_stage_per)
    TextView music_clear_stage_per;
    @Bind(R.id.tv_music_result_average_score)
    TextView music_average_score;
    @Bind(R.id.tv_music_result_average_score_per)
    TextView music_average_score_per;
    @Bind(R.id.tv_music_result_no_miss)
    TextView music_no_miss;
    @Bind(R.id.tv_music_result_no_miss_per)
    TextView music_no_miss_per;
    @Bind(R.id.tv_music_result_full_chain)
    TextView music_full_chain;
    @Bind(R.id.tv_music_result_full_chain_per)
    TextView music_full_chain_per;
    @Bind(R.id.tv_music_result_s)
    TextView music_s;
    @Bind(R.id.tv_music_result_s_per)
    TextView music_s_per;
    @Bind(R.id.tv_music_result_ss)
    TextView music_ss;
    @Bind(R.id.tv_music_result_ss_per)
    TextView music_ss_per;
    @Bind(R.id.tv_music_result_sss)
    TextView music_sss;
    @Bind(R.id.tv_music_result_sss_per)
    TextView music_sss_per;

    private Toolbar toolbar;
    private FragmentPagerAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int position = 0;
        String[] tab_name = getResources().getStringArray(R.array.tab_name);

        if (bundle != null) {
            position = bundle.getInt("position");
        }

        View view = inflater.inflate(R.layout.fragment_play_data, group, false);
        ButterKnife.bind(this, view);


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        new PlayDataUseCase().getPlayData();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEventMainThread(PlayDataUseCase.PlayDataEvent event) {
        if (event.playerData == null || event.salesData == null || event.averageScore == null || event.stageData == null) {
            return;
        }

        PlayerData pd     = event.playerData;
        ShopSalesData ssd = event.salesData;
        AverageScore as   = event.averageScore;
        StageData sd      = event.stageData;
        
        player_name.setText(pd.player_name);
        player_level.setText("Lv." + pd.level);
        player_avatar_title.setText(pd.avatar + "/" + pd.title);
        player_total_score.setText(String.valueOf(pd.total_score));
        player_rank.setText(String.valueOf(pd.rank));
        player_coin.setText(String.valueOf(ssd.current_coin));
        player_trophy.setText(String.valueOf(pd.total_trophy));

        music_play_music.setText(String.valueOf(pd.total_play_music) + "/" + String.valueOf(pd.total_music));
        music_play_music_per.setText(String.format("%.2f%%", (float) pd.total_play_music / pd.total_music * 100));
        music_clear_stage.setText(String.valueOf(sd.clear) + "/" + String.valueOf(sd.all));
        music_clear_stage_per.setText(String.format(("%.2f%%"), (float) sd.clear / sd.all * 100));
        music_average_score.setText(String.valueOf(as.average_score));
        music_average_score_per.setText(String.format("%.2f%%", (float) as.average_score / 1000000 * 100));
        music_no_miss.setText(String.valueOf(sd.nomiss));
        music_no_miss_per.setText(String.format("%.2f%%", (float) sd.nomiss / sd.all * 100));
        music_full_chain.setText(String.valueOf(sd.fullchain));
        music_full_chain_per.setText(String.format("%.2f%%", (float) sd.fullchain / sd.all * 100));
        music_s.setText(String.valueOf(sd.s));
        music_s_per.setText(String.format("%.2f%%", (float) sd.s / sd.all * 100));
        music_ss.setText(String.valueOf(sd.ss));
        music_ss_per.setText(String.format("%.2f%%", (float) sd.ss / sd.all * 100));
        music_sss.setText(String.valueOf(sd.sss));
        music_sss_per.setText(String.format("%.2f%%", (float) sd.sss / sd.all * 100));

    }

}
