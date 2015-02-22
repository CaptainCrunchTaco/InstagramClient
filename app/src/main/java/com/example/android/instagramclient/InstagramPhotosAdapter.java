package com.example.android.instagramclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

/**
 * Created by Ariel on 2/17/15.
 */
public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {

    private static Transformation transformation;

    // View lookup cache
    private static class ViewHolder {
        TextView tvCaption;
        TextView tvUsername;
        TextView tvTimeStamp;
        TextView tvLikes;
        ImageView ivPhoto;
        ImageView ivProfile;
    }

    //What data do we need from the activity
    //Context, Data Source
    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }
    // What our item looks like
    // Use the template to display each photo

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        // Get the data item for this position
        InstagramPhoto photo = getItem(position);
        // Check if we are using a recycled view, if not we need to inflate
        //convertView is the old view that we want to reuse, if it has stuff in it
        if (convertView == null) {
//            screenWidth = DeviceDimensionsHelper.getDisplayWidth(getContext());
            viewHolder = new ViewHolder();
            // create a new view from template
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
            // Lookup the views for populating the data (image,caption)
            viewHolder.tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.tvTimeStamp = (TextView) convertView.findViewById(R.id.tvTimeStamp);
            viewHolder.tvLikes = (TextView) convertView.findViewById(R.id.tvLikes);
            viewHolder.ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
            viewHolder.ivProfile = (ImageView) convertView.findViewById(R.id.ivProfile);
            transformation = new RoundedTransformationBuilder()
                    .cornerRadiusDp(50)
                    .oval(false)
                    .build();
            convertView.setTag(viewHolder);
        } else {
            //Allows the view to "remember" the ViewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Insert the model data into eachh of the view items
        viewHolder.tvCaption.setText(photo.caption);
        // Clear out imageviews
        viewHolder.ivPhoto.setImageResource(0);
        viewHolder.ivProfile.setImageResource(0);
        // Insert Username
        viewHolder.tvUsername.setText(photo.username);
        //Insert Time Stamp
        viewHolder.tvTimeStamp.setText(photo.timeStamp);
        //Insert likes
        viewHolder.tvLikes.setText(Integer.toString(photo.likesCount) + " likes");
        // Insert image using picasso
        Picasso.with(getContext()).load(photo.imageUrl).into(viewHolder.ivPhoto);
        //Profile Picture
        Picasso.with(getContext()).load(photo.profilePicture).transform(transformation).into(viewHolder.ivProfile);
        // Return the created item as a view
        return convertView;
    }
}
