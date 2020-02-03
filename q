[1mdiff --git a/app/src/main/res/layout/activity_add_poll.xml b/app/src/main/res/layout/activity_add_poll.xml[m
[1mindex bf258fa..fa9eefa 100644[m
[1m--- a/app/src/main/res/layout/activity_add_poll.xml[m
[1m+++ b/app/src/main/res/layout/activity_add_poll.xml[m
[36m@@ -2,11 +2,11 @@[m
 <androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"[m
     xmlns:app="http://schemas.android.com/apk/res-auto"[m
     xmlns:tools="http://schemas.android.com/tools"[m
[31m-    android:background="@android:color/transparent"[m
[31m-    android:orientation="vertical"[m
     android:layout_width="match_parent"[m
     android:layout_height="match_parent"[m
[31m-    android:gravity="center">[m
[32m+[m[32m    android:alpha="1.0"[m
[32m+[m[32m    android:background="@android:color/transparent"[m
[32m+[m[32m    android:orientation="vertical">[m
     tools:context=".AddPollActivity">[m
 [m
     <androidx.cardview.widget.CardView[m
[36m@@ -15,9 +15,11 @@[m
         android:layout_alignParentStart="true"[m
         android:layout_alignParentLeft="true"[m
         android:layout_alignParentTop="true"[m
[32m+[m[32m        android:layout_marginTop="32dp"[m
[32m+[m[32m        android:layout_marginHorizontal="50dp"[m
         app:cardBackgroundColor="@color/colorPrimaryDark"[m
         app:cardCornerRadius="15dp"[m
[31m-        app:layout_constraintBottom_toBottomOf="parent"[m
[32m+[m[32m        app:layout_constraintEnd_toEndOf="parent"[m
         app:layout_constraintStart_toStartOf="parent"[m
         app:layout_constraintTop_toTopOf="parent">[m
 [m
