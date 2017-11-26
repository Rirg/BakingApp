package com.example.ricardo.bakingapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ricardo.bakingapp.R;
import com.example.ricardo.bakingapp.models.Step;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import icepick.Icepick;
import icepick.State;


public class StepDetailFragment extends Fragment {

    /* Media variables */
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    @State long mPlayerPos = C.TIME_UNSET;
    private ImageView mThumbnailImageView;

    /* Variables to hold the current and all the steps in the recipe */
    @State Step mCurrentStep;
    @State ArrayList<Step> mSteps;

    private static final String TAG = "StepDetailFragment";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);

        mPlayerView = rootView.findViewById(R.id.detail_exo_player);
        mThumbnailImageView = rootView.findViewById(R.id.thumbnail_image_view);
        TextView descriptionTv = rootView.findViewById(R.id.step_description_tv);

        // Restore state using Icepick
        Icepick.restoreInstanceState(this, savedInstanceState);

        // Get all the steps and the current step based on the position if the mCurrentStep variable
        // is null
        if (mCurrentStep == null) {
            Bundle extras = getArguments();
            if (extras != null && extras.getParcelableArrayList("steps") != null) {
                mSteps = extras.getParcelableArrayList("steps");
                mCurrentStep = mSteps.get(extras.getInt("pos"));

            }
        }
        if (mCurrentStep != null){
            // Set the description text if the view isn't null
            if (descriptionTv != null) descriptionTv.setText(mCurrentStep.getDescription());
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
          if (mCurrentStep != null) {
              // If there is a video url available, hide ImageView and initialize the player
              if (mCurrentStep.getVideoUrl() != null && !mCurrentStep.getVideoUrl().isEmpty()) {
                  mThumbnailImageView.setVisibility(View.GONE);
                  initializeMediaSession();
                  initializePlayer(Uri.parse(mCurrentStep.getVideoUrl()));
              }
              // If there is an image url available, hide the SimpleExoPlayerView and load the image using Glide
              else if (mCurrentStep.getThumbnailURL() != null && !mCurrentStep.getThumbnailURL().isEmpty()){
                  mPlayerView.setVisibility(View.GONE);
                  Glide.with(this)
                          .load(mCurrentStep.getThumbnailURL())
                          .into(mThumbnailImageView);
              }
              // Else, there is nothing to show, hide ImageView and SimpleExoPlayerView
              else {
                  mPlayerView.setVisibility(View.GONE);
                  mThumbnailImageView.setVisibility(View.GONE);
              }
          }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            mPlayerPos = mExoPlayer.getCurrentPosition();
            releasePlayer();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }


    public void setCurrentStep(Step currentStep) {
        mCurrentStep = currentStep;
    }

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector(null);
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            // Check if there is a position saved
            if (mPlayerPos != C.TIME_UNSET) mExoPlayer.seekTo(mPlayerPos);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }


    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }
}