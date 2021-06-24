package com.xenori.pushbox.dlna;


import android.content.Context;

import com.android.cast.dlna.core.Utils;
import com.android.cast.dlna.dmr.IDLNARenderControl;
import com.android.cast.dlna.dmr.service.IRendererInterface;
import com.orhanobut.logger.Logger;
import com.xenori.pushbox.misc.SharedPref;

import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.avtransport.AVTransportErrorCode;
import org.fourthline.cling.support.avtransport.AVTransportException;
import org.fourthline.cling.support.model.DeviceCapabilities;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.SeekMode;
import org.fourthline.cling.support.model.StorageMedium;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportSettings;
import org.fourthline.cling.support.model.TransportState;

import java.net.ConnectException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AVPushTransportController implements IRendererInterface.IAVTransportControl {
    private static final TransportAction[] TRANSPORT_ACTION_STOPPED = new TransportAction[]{TransportAction.Play};
    private static final TransportAction[] TRANSPORT_ACTION_PLAYING = new TransportAction[]{TransportAction.Stop, TransportAction.Pause, TransportAction.Seek};
    private static final TransportAction[] TRANSPORT_ACTION_PAUSE_PLAYBACK = new TransportAction[]{TransportAction.Play, TransportAction.Seek, TransportAction.Stop};

    private final UnsignedIntegerFourBytes mInstanceId;
    private final Context mApplicationContext;
    private TransportState mCurrentTransportState = TransportState.NO_MEDIA_PRESENT;
    private final TransportInfo mTransportInfo = new TransportInfo();
    private final TransportInfo mPlayingTransportInfo = new TransportInfo(TransportState.PLAYING);
    private final TransportInfo mStopTransportInfo = new TransportInfo(TransportState.STOPPED);
    private final TransportInfo mPauseTransportInfo = new TransportInfo(TransportState.PAUSED_PLAYBACK);
    private final TransportInfo mTransitioningTransportInfo = new TransportInfo(TransportState.TRANSITIONING);
    private final TransportSettings mTransportSettings = new TransportSettings();
    private PositionInfo mOriginPositionInfo = new PositionInfo();
    private MediaInfo mMediaInfo = new MediaInfo();
    private final IDLNARenderControl mMediaControl;

    private final OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(1, TimeUnit.SECONDS).build();

    public AVPushTransportController(Context context, IDLNARenderControl control) {
        this(context, new UnsignedIntegerFourBytes(0), control);
    }

    public AVPushTransportController(Context context, UnsignedIntegerFourBytes instanceId, IDLNARenderControl control) {
        mApplicationContext = context.getApplicationContext();
        mInstanceId = instanceId;
        mMediaControl = control;
    }

    public UnsignedIntegerFourBytes getInstanceId() {
        return mInstanceId;
    }

    public synchronized TransportAction[] getCurrentTransportActions() {
        switch (mTransportInfo.getCurrentTransportState()) {
            case PLAYING:
                return TRANSPORT_ACTION_PLAYING;
            case PAUSED_PLAYBACK:
                return TRANSPORT_ACTION_PAUSE_PLAYBACK;
            default:
                return TRANSPORT_ACTION_STOPPED;
        }
    }

    @Override
    public DeviceCapabilities getDeviceCapabilities() {
        return new DeviceCapabilities(new StorageMedium[]{StorageMedium.NETWORK});
    }

    @Override
    public MediaInfo getMediaInfo() {
        return mMediaInfo;
    }

    @Override
    public PositionInfo getPositionInfo() {
        return new PositionInfo(mOriginPositionInfo, mMediaControl.getPosition() / 1000, mMediaControl.getDuration() / 1000);
    }

    @Override
    public TransportInfo getTransportInfo() {
        switch (mCurrentTransportState) {
            case TRANSITIONING:
                return mTransitioningTransportInfo;
            case PLAYING:
                return mPlayingTransportInfo;
            case  PAUSED_PLAYBACK:
                return mPauseTransportInfo;
            case STOPPED:
                return mStopTransportInfo;
            default:
                return mTransportInfo;
        }
    }

    @Override
    public TransportSettings getTransportSettings() {
        return mTransportSettings;
    }

    @Override
    public void setAVTransportURI(String currentURI, String currentURIMetaData) throws AVTransportException {
        Logger.i(">>> " + currentURI);
        try {
            new URI(currentURI);
        } catch (Exception ex) {
            throw new AVTransportException(ErrorCode.INVALID_ARGS, "CurrentURI can not be null or malformed");
        }
        mMediaInfo = new MediaInfo(currentURI, currentURIMetaData, new UnsignedIntegerFourBytes(1), "", StorageMedium.NETWORK);
        mOriginPositionInfo = new PositionInfo(1, currentURIMetaData, currentURI);
        mCurrentTransportState = TransportState.TRANSITIONING;
    }



    @Override
    public void setNextAVTransportURI(String nextURI, String nextURIMetaData) {
    }

    @Override
    public void play(String speed) {
        try {
            pushUrl(mMediaInfo.getCurrentURI());
        } catch (ConnectException e) {
            Logger.e(e.getMessage() + e.getStackTrace() );
            //throw new AVTransportException(ErrorCode.ACTION_FAILED, "连接失败");
        } catch (Exception e) {
            Logger.e(e.getMessage() + e.getStackTrace() );
            //throw new AVTransportException(ErrorCode.ACTION_FAILED, "推送失败");
        }

        mCurrentTransportState = TransportState.PLAYING;
        mMediaControl.play();
    }

    private void pushUrl(String url) throws Exception {
        String api = SharedPref.INSTANCE.getString("obox_url", "", SharedPref.Group.APP);
        if (api == null || "".equals(api)) {
            Logger.e("未配置Obxo" );
            return;
        }
        RequestBody body = new FormBody.Builder().add("value", url).add("play", "1").build();
        Request request = new Request.Builder()
                .url(api)
                .post(body)
                .build();

        Response resp =  client.newCall(request).execute();
        Logger.i(resp.body().string());
    }

    public void pause() {
        mCurrentTransportState = TransportState.PAUSED_PLAYBACK;
        mMediaControl.pause();
    }

    @Override
    public void seek(String unit, String target) throws AVTransportException {
        SeekMode seekMode = SeekMode.valueOrExceptionOf(unit);
        if (!seekMode.equals(SeekMode.REL_TIME)) {
            throw new AVTransportException(AVTransportErrorCode.SEEKMODE_NOT_SUPPORTED, "Unsupported seek mode: " + unit);
        }
        long position = Utils.getIntTime(target);
        mMediaControl.seek(position);
    }

    synchronized public void stop() {
        mCurrentTransportState = TransportState.STOPPED;
        mMediaControl.stop();
    }

    @Override
    public void previous() {
    }

    @Override
    public void next() {
    }

    @Override
    public void record() {
    }

    @Override
    public void setPlayMode(String newPlayMode) {
    }

    @Override
    public void setRecordQualityMode(String newRecordQualityMode) {
    }
}
