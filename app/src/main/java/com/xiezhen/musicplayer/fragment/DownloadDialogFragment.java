package com.xiezhen.musicplayer.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;

import com.xiezhen.musicplayer.activity.MainActivity;
import com.xiezhen.musicplayer.entity.Mp3Cloud;
import com.xiezhen.musicplayer.entity.SearchResult;
import com.xiezhen.musicplayer.utils.DownloadUtils;

/**
 * Created by xiezhen on 2015/12/26 0026.
 */
public class DownloadDialogFragment extends DialogFragment {
    private Mp3Cloud searchResult;
    private MainActivity mainActivity;

    public  static DownloadDialogFragment newInstance(Mp3Cloud searchResult) {
        DownloadDialogFragment downloadDialogFragment = new DownloadDialogFragment();
        downloadDialogFragment.searchResult = searchResult;
        return downloadDialogFragment;
    }

    private String[] items;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) getActivity();
        items = new String[]{"下载", "取消"};
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setCancelable(true);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        downloadMusic();
                        break;
                    case 1:
                        dialog.dismiss();
                        break;
                }
            }
        });
        return builder.show();
    }

    private void downloadMusic() {
        Toast.makeText(mainActivity, "正在下载: " + searchResult.getMusicName(), Toast.LENGTH_SHORT).show();
        DownloadUtils.getsInstance(mainActivity).setListener(new DownloadUtils.OnDownloadListener() {
            @Override
            public void onDownload(String mp3Url) {
                Toast.makeText(mainActivity, mp3Url, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(mainActivity, error, Toast.LENGTH_SHORT).show();
            }
        }).download(searchResult);
    }
}
