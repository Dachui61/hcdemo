package com.example.hcdemo;

import android.content.Context;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.example.hcdemo.Control.DevManageGuider;
import com.example.hcdemo.Control.SDKGuider;
import com.example.hcdemo.jna.HCNetSDKByJNA;
import com.example.hcdemo.jna.HCNetSDKJNAInstance;
import com.example.hcdemo.Model.DBDevice;
import com.example.hcdemo.View.MyActivityBase;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_IPPARACFG_V40;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.PlaybackCallBack;
import com.hikvision.netsdk.RealPlayCallBack;

import java.nio.charset.StandardCharsets;

public class DevManager {

    //配置摄像头ip 参数
    private String DevName = "我的大宝贝";
    private String m_szIp = "10.16.26.167";
    private String m_szPort = "8000";
    private String m_szUserName = "admin";
    private String m_szPassWorld = "abcd1234";
    private int lUserID;

    //用来处理视频录像的
    private int m_iPreviewHandle;

    public void addDev(Context context){
        if (!HCNetSDKJNAInstance.getInstance().NET_DVR_Init()) {
            Toast.makeText(context, "摄像机SDK初始化失败", Toast.LENGTH_SHORT).show();
            return;
        }

        HCNetSDKByJNA.NET_DVR_USER_LOGIN_INFO loginInfo = new HCNetSDKByJNA.NET_DVR_USER_LOGIN_INFO();
        System.arraycopy(m_szIp.getBytes(StandardCharsets.UTF_8), 0, loginInfo.sDeviceAddress, 0, m_szIp.length());
        System.arraycopy(m_szUserName.getBytes(StandardCharsets.UTF_8), 0, loginInfo.sUserName, 0, m_szUserName.length());
        System.arraycopy(m_szPassWorld.getBytes(StandardCharsets.UTF_8), 0, loginInfo.sPassword, 0, m_szPassWorld.length());
        loginInfo.wPort = (short)Integer.parseInt(m_szPort);
        HCNetSDKByJNA.NET_DVR_DEVICEINFO_V40 deviceInfo = new HCNetSDKByJNA.NET_DVR_DEVICEINFO_V40();
        loginInfo.write();

        lUserID = HCNetSDKJNAInstance.getInstance().NET_DVR_Login_V40(loginInfo.getPointer(), deviceInfo.getPointer());

        if (lUserID >= 0) {
            Toast.makeText(context, "摄像机初始化成功"+lUserID, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "无法连接到摄像头", Toast.LENGTH_SHORT).show();
        }
    }


    public void realPlay(SurfaceView surfaceView){
        NET_DVR_PREVIEWINFO struPlayInfo = new NET_DVR_PREVIEWINFO();
        struPlayInfo.lChannel = 1;
        struPlayInfo.dwStreamType = 0;
        struPlayInfo.bBlocked = 1;

        struPlayInfo.hHwnd = surfaceView.getHolder();
        m_iPreviewHandle = SDKGuider.g_sdkGuider.m_comPreviewGuider.RealPlay_V40_jni(lUserID, struPlayInfo, null);
        if (m_iPreviewHandle < 0)
        {
            Toast.makeText(surfaceView.getContext(),"NET_DVR_RealPlay_V40 fail, Err:"+ SDKGuider.g_sdkGuider.GetLastError_jni(),Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(surfaceView.getContext(),"NET_DVR_RealPlay_V40 Succ " ,Toast.LENGTH_SHORT).show();
    }

    public void record(Context context,String sPicFileName){
        if (m_iPreviewHandle < 0)
        {
            Toast.makeText(context,"please start preview first",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!SDKGuider.g_sdkGuider.m_comPreviewGuider.RealPlay_Record(m_iPreviewHandle,1,  sPicFileName))
        {
            Toast.makeText(context,"NET_DVR_SaveRealData_V30 fail, Err:"+ SDKGuider.g_sdkGuider.GetLastError_jni(),Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(context,"NET_DVR_SaveRealData_V30 Succ",Toast.LENGTH_SHORT).show();
    }

    //重载函数，默认路径：
    public void record(Context context){
        if (m_iPreviewHandle < 0)
        {
            Toast.makeText(context,"please start preview first",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!SDKGuider.g_sdkGuider.m_comPreviewGuider.RealPlay_Record(m_iPreviewHandle, 1, "/mnt/sdcard/123465.mp4"))
        {
            Toast.makeText(context,"NET_DVR_SaveRealData_V30 fail, Err:"+ SDKGuider.g_sdkGuider.GetLastError_jni(),Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(context,"NET_DVR_SaveRealData_V30 Succ",Toast.LENGTH_SHORT).show();
    }

    public void stop(Context context){
        if (!SDKGuider.g_sdkGuider.m_comPreviewGuider.RealPlay_Stop_jni(m_iPreviewHandle))
        {
            Toast.makeText(context,"NET_DVR_StopRealPlay m_iPreviewHandle：" + m_iPreviewHandle
                    + "  error:" + SDKGuider.g_sdkGuider.GetLastError_jni(),Toast.LENGTH_SHORT).show();
            return;
        }
        m_iPreviewHandle = -1;
        Toast.makeText(context,"NET_DVR_StopRealPlay Succ",Toast.LENGTH_SHORT).show();
    }

    public void snap(Context context){
        if (m_iPreviewHandle < 0)
        {
            Toast.makeText(context,"please start preview first",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!SDKGuider.g_sdkGuider.m_comPreviewGuider.RealPlay_Snap(m_iPreviewHandle, "/mnt/sdcard/test.bmp"))
        {
            Toast.makeText(context,"NET_DVR_CapturePicture fail, Err:"+ SDKGuider.g_sdkGuider.GetLastError_jni(),Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(context,"NET_DVR_CapturePicture Succ",Toast.LENGTH_SHORT).show();

    }



    public int getlUserID(){
        return lUserID;
    }

    public void setDevName(String devName) {
        DevName = devName;
    }

    public void setM_szIp(String m_szIp) {
        this.m_szIp = m_szIp;
    }

    public void setM_szPort(String m_szPort) {
        this.m_szPort = m_szPort;
    }

    public void setM_szUserName(String m_szUserName) {
        this.m_szUserName = m_szUserName;
    }

    public void setM_szPassWorld(String m_szPassWorld) {
        this.m_szPassWorld = m_szPassWorld;
    }

}
