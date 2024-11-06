function getCtx() {
    let currentApplication = Java.use('android.app.ActivityThread').currentApplication();
    let context = currentApplication.getApplicationContext();
    return context;
}

export { getCtx }