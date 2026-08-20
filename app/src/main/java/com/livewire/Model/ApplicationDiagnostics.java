package com.livewire.Model;

public class ApplicationDiagnostics {

    private String appVersion;
    private int versionCode;
    private String androidVersion;
    private String deviceModel;

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

}
