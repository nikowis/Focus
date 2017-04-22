package pl.nikowis.focus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikodem on 4/22/2017.
 */

public class FacebookFragment extends Fragment {

    private List<MediaItem> list;
    private RecyclerView recyclerView;
    private MediaFacebookAdapter facebookAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //tu pobieramy z API
        list = new ArrayList<>();
        list.add(new MediaItem("tytul", "opis opis opis"));
        list.add(new MediaItem("tytul2", "opis opis opis opis opis opis"));
        list.add(new MediaItem("tytul3", "opis opis opis opis"));
        list.add(new MediaItem("tytul4", "opis opis opis opis opis opis"));
        View mainFragment = inflater.inflate(R.layout.fragment_facebook, container, false);
        recyclerView = (RecyclerView) mainFragment.findViewById(R.id.shopping_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        facebookAdapter = new MediaFacebookAdapter(list, getActivity());

        recyclerView.setAdapter(facebookAdapter);
        return mainFragment;
    }

}
