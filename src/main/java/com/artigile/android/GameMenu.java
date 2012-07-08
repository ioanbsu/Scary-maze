package com.artigile.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.inject.Inject;
import roboguice.activity.RoboListActivity;

import javax.inject.Singleton;
import java.io.File;
import java.util.*;

/**
 * @author IoaN, 7/7/12 12:23 PM
 */
@Singleton
public class GameMenu extends RoboListActivity {


    @Inject
    private MazeDotState mazeDotState;

    @Inject
    private FileUtils fileUtils;


    private ArrayAdapter<File> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_menu);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getListView().setItemsCanFocus(false);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        List<File> files = new ArrayList<File>(Arrays.asList(fileUtils.getMagicMazeVideosDir().listFiles()));
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return (int) (file2.lastModified() - file1.lastModified());
            }
        });
        listAdapter = new ArrayAdapter<File>(this, android.R.layout.simple_list_item_1, files);
        setListAdapter(listAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        playVideo(listAdapter.getItem(position));
    }


    public void playAgain(View view) {
        mazeDotState.resetState();
        Intent intent = new Intent(this, MazeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade, R.anim.hold);
    }


    private void playVideo(File file) {
        Intent myIntent = new Intent(Intent.ACTION_VIEW);
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        myIntent.setDataAndType(Uri.fromFile(file), mimetype);
        startActivity(myIntent);
    }

}
