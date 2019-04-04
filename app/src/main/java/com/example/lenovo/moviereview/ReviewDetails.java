package com.example.lenovo.moviereview;

import java.util.ArrayList;

/**
 * Created by LENOVO on 6/22/2018.
 */

public class ReviewDetails {

    private ArrayList<String> authorNames = new ArrayList<>();
    private ArrayList<String> detailComments = new ArrayList<>();

    public ReviewDetails(ArrayList<String> names, ArrayList<String> comments){
        authorNames = names;
        detailComments = comments;
    }

    public ArrayList<String> getAuthorNames(){
        return authorNames;
    }

    public ArrayList<String> getDetailComments(){
        return detailComments;
    }
}
