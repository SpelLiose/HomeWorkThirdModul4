package com.spelloise.homeworkthirdmodul4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements Runnable {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private SeekBar seekBar;
    private boolean wasPlaying = false;
    private FloatingActionButton fabPlayPause;
    private FloatingActionButton fabBack;
    private FloatingActionButton fabRepeat;
    private FloatingActionButton fabForward;
    private FloatingActionButton fabNext;
    private TextView timeDuration;
    private TextView metaDataAudio;
    private String metaData;
    private boolean isRepeat = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabPlayPause = findViewById(R.id.fabPlayPause);
        fabBack = findViewById(R.id.fabBack);
        fabRepeat = findViewById(R.id.fabRepeat);
        fabForward = findViewById(R.id.fabForward);
        fabNext = findViewById(R.id.fabNext);
        timeDuration = findViewById(R.id.timeDuration);
        seekBar = findViewById(R.id.seekBar);
        metaDataAudio = findViewById(R.id.metaDataAudio);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timeDuration.setVisibility(View.VISIBLE);
                int timeTrack = (int) Math.ceil(progress / 1000f);
                int minute = timeTrack / 60;
                timeDuration.setText(String.format("%02d", minute) + ":" + String.format("%02d", (timeTrack % 60)));


                double percentTrack = progress / (double) seekBar.getMax();
                timeDuration.setX(seekBar.getX() + Math.round(seekBar.getWidth() * percentTrack * 0.92));

                if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    clearMediaPlayer();
                    MainActivity.this.seekBar.setProgress(0);
                    fabPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_play));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                timeDuration.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });


        fabPlayPause.setOnClickListener(listener);
        fabBack.setOnClickListener(listener);
        fabForward.setOnClickListener(listener);
        fabRepeat.setOnClickListener(listener);
        fabNext.setOnClickListener(listener);

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fabPlayPause:
                    playSong();
                    break;
                case R.id.fabBack:
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
                    break;
                case  R.id.fabForward:
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
                    break;
                case R.id.fabRepeat:
                    if (!isRepeat && mediaPlayer != null) {
                        mediaPlayer.setLooping(true);
                        fabRepeat.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.repeat));
                        isRepeat = true;
                    } else if (isRepeat && mediaPlayer != null) {
                        mediaPlayer.setLooping(false);
                        fabRepeat.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.repeat_off));
                        isRepeat = false;
                    }
                    break;
                case R.id.fabNext:

                    break;
            }

        }
    };

    public void playSong() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                clearMediaPlayer();
                wasPlaying = true;

                fabPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,android.R.drawable.ic_media_play));
            }

            if (!wasPlaying) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }

                fabPlayPause.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, android.R.drawable.ic_media_pause));
                AssetFileDescriptor descriptor = getAssets().openFd("DJ spelL - love is more depressing than depression.mp3");
                mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());


                MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
                mediaMetadata.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());

                metaData =  mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                metaData += "\n" + mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

                mediaMetadata.release();

                metaDataAudio.setText(metaData);

                descriptor.close();

                mediaPlayer.prepare();
                mediaPlayer.setVolume(0.7f, 0.7f);
                mediaPlayer.setLooping(false);
                seekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.seekTo(seekBar.getProgress());

                mediaPlayer.start();
                new Thread(this).start();
            }

            wasPlaying = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMediaPlayer();
    }


    private void clearMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }


    @Override
    public void run() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int total = mediaPlayer.getDuration();


        while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
            try {
                Thread.sleep(1000); //
                currentPosition = mediaPlayer.getCurrentPosition(); //
            } catch (InterruptedException e) { //
                e.printStackTrace();
                return; //
            } catch (Exception e) {
                return;
            }
            seekBar.setProgress(currentPosition); //
        }
    }
}