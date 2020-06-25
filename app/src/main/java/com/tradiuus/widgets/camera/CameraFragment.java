package com.tradiuus.widgets.camera;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TrackBox;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Mp4TrackImpl;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.tradiuus.R;
import com.tradiuus.activities.ContractorVideoRecordActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends CameraVideoFragment implements View.OnClickListener {

    private static final String TAG = "CameraFragment";
    private static final String VIDEO_DIRECTORY_NAME = "AndroidWave";
    private AutoFitTextureView mTextureView;
    private ImageView mRecordVideo;
    private VideoView mVideoView;
    private ImageView mPlayVideo;
    private String mOutputFilePath;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */


    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        mTextureView = (AutoFitTextureView) view.findViewById(R.id.mTextureView);
        mVideoView = (VideoView) view.findViewById(R.id.mVideoView);
        mPlayVideo = (ImageView) view.findViewById(R.id.mPlayVideo);
        mRecordVideo = (ImageView) view.findViewById(R.id.mRecordVideo);
        mPlayVideo.setColorFilter(Color.parseColor("#25B7D3"), PorterDuff.Mode.SRC_ATOP);
        mPlayVideo.setVisibility(View.INVISIBLE);
        mRecordVideo.setVisibility(View.INVISIBLE);
        mPlayVideo.setOnClickListener(this);
        return view;
    }

    @Override
    public int getTextureResource() {
        return R.id.mTextureView;
    }

    @Override
    protected void setUp(View view) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mRecordVideo:
                break;
            case R.id.mPlayVideo:
                playRecordedVideo();
                break;
        }
    }

    public void recordVideo() {
        try {
            if (mIsRecordingVideo) {
                try {
                    ((ContractorVideoRecordActivity)getActivity()).lockUI = true;
                    ((ContractorVideoRecordActivity)getActivity()).onShowPgDialog();
                    stopRecordingVideo();
                    prepareViews();
                } catch (Exception e) {
                    e.printStackTrace();
                    ((ContractorVideoRecordActivity)getActivity()).lockUI = false;
                    ((ContractorVideoRecordActivity)getActivity()).onHidePgDialog();
                }
            } else {
                startRecordingVideo();
                mRecordVideo.setImageResource(R.drawable.ic_stop);
                //Receive out put file here
                mOutputFilePath = getCurrentFile().getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ((ContractorVideoRecordActivity)getActivity()).lockUI = false;
            ((ContractorVideoRecordActivity)getActivity()).onHidePgDialog();
        }
    }

    public String getCurrentPath() {
        return mOutputFilePath;
    }

    public void playRecordedVideo() {
        try {
            mVideoView.start();
            mPlayVideo.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareViews() {
        if (mVideoView.getVisibility() == View.GONE) {
            mVideoView.setVisibility(View.VISIBLE);
            mPlayVideo.setVisibility(View.VISIBLE);
            mTextureView.setVisibility(View.GONE);
            setMediaForRecordVideo();
        }
    }

    private void setMediaForRecordVideo(){
        AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    mOutputFilePath = parseVideo(mOutputFilePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    // Set media controller
                    mVideoView.setMediaController(new MediaController(getActivity()));
                    mVideoView.requestFocus();
                    mVideoView.setVideoPath(mOutputFilePath);
                    mVideoView.seekTo(100);
                    mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            // Reset player
                            mVideoView.setVisibility(View.VISIBLE);
                            mPlayVideo.setVisibility(View.VISIBLE);
                            mTextureView.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((ContractorVideoRecordActivity)getActivity()).lockUI = false;
                ((ContractorVideoRecordActivity)getActivity()).onHidePgDialog();
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private String parseVideo(String mFilePath) throws IOException {
        DataSource channel = new FileDataSourceImpl(mFilePath);
        IsoFile isoFile = new IsoFile(channel);
        List<TrackBox> trackBoxes = isoFile.getMovieBox().getBoxes(TrackBox.class);
        boolean isError = false;
        for (TrackBox trackBox : trackBoxes) {
            TimeToSampleBox.Entry firstEntry = trackBox.getMediaBox().getMediaInformationBox().getSampleTableBox().getTimeToSampleBox().getEntries().get(0);
            // Detect if first sample is a problem and fix it in isoFile
            // This is a hack. The audio deltas are 1024 for my files, and video deltas about 3000
            // 10000 seems sufficient since for 30 fps the normal delta is about 3000
            if (firstEntry.getDelta() > 10000) {
                isError = true;
                firstEntry.setDelta(3000);
            }
        }
        File file = getOutputMediaFile();
        String filePath = file.getAbsolutePath();
        if (isError) {
            Movie movie = new Movie();
            for (TrackBox trackBox : trackBoxes) {
                movie.addTrack(new Mp4TrackImpl(channel.toString() + "[" + trackBox.getTrackHeaderBox().getTrackId() + "]", trackBox));
            }
            movie.setMatrix(isoFile.getMovieBox().getMovieHeaderBox().getMatrix());
            Container out = new DefaultMp4Builder().build(movie);

            //delete file first!
            FileChannel fc = new RandomAccessFile(filePath, "rw").getChannel();
            out.writeContainer(fc);
            fc.close();
            Log.d(TAG, "Finished correcting raw video");
            return filePath;
        }
        return mFilePath;
    }

    /**
     * Create directory and return file
     * returning video file
     */
    private File getOutputMediaFile() {
        // External sdcard file location
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),
                VIDEO_DIRECTORY_NAME);
        // Create storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + VIDEO_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "VID_" + timeStamp + ".mp4");
        return mediaFile;
    }
}