1.添加圆角

2.依赖android16 : android.car.view.MirroredSurfaceView

@SuppressLint({"UnflaggedApi"})
@Nullable
public IBinder getTokenForTaskId(int taskId){
if (mCarAM == null) {
Slogf.e(TAG, "Failed to mirrorSurface because CarService isn't ready yet");
return null;
}
IBinder taskMirroringToken = mCarAM.createTaskMirroringToken(taskId);
return taskMirroringToken;
}