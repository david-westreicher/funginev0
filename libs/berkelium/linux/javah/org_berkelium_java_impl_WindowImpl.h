/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_berkelium_java_impl_WindowImpl */

#ifndef _Included_org_berkelium_java_impl_WindowImpl
#define _Included_org_berkelium_java_impl_WindowImpl
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    _setDelegate
 * Signature: (Lorg/berkelium/java/api/WindowDelegate;)V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl__1setDelegate
  (JNIEnv *, jobject, jobject);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    getId
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_berkelium_java_impl_WindowImpl_getId
  (JNIEnv *, jobject);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    setTransparent
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_setTransparent
  (JNIEnv *, jobject, jboolean);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    focus
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_focus
  (JNIEnv *, jobject);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    unfocus
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_unfocus
  (JNIEnv *, jobject);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    mouseMoved
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_mouseMoved
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    mouseButton
 * Signature: (IZ)V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_mouseButton
  (JNIEnv *, jobject, jint, jboolean);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    mouseWheel
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_mouseWheel
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    textEvent
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_textEvent
  (JNIEnv *, jobject, jstring);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    keyEvent
 * Signature: (ZIII)V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_keyEvent
  (JNIEnv *, jobject, jboolean, jint, jint, jint);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    resize
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_resize
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    adjustZoom
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_adjustZoom
  (JNIEnv *, jobject, jint);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    executeJavascript
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_executeJavascript
  (JNIEnv *, jobject, jstring);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    insertCSS
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_insertCSS
  (JNIEnv *, jobject, jstring, jstring);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    navigateTo
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_berkelium_java_impl_WindowImpl_navigateTo
  (JNIEnv *, jobject, jstring);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    refresh
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_refresh
  (JNIEnv *, jobject);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    stop
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_stop
  (JNIEnv *, jobject);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    goBack
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_goBack
  (JNIEnv *, jobject);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    goForward
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_goForward
  (JNIEnv *, jobject);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    canGoBack
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_berkelium_java_impl_WindowImpl_canGoBack
  (JNIEnv *, jobject);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    canGoForward
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_org_berkelium_java_impl_WindowImpl_canGoForward
  (JNIEnv *, jobject);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    cut
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_cut
  (JNIEnv *, jobject);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    copy
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_copy
  (JNIEnv *, jobject);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    paste
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_paste
  (JNIEnv *, jobject);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    undo
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_undo
  (JNIEnv *, jobject);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    redo
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_redo
  (JNIEnv *, jobject);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    del
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_del
  (JNIEnv *, jobject);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    selectAll
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_selectAll
  (JNIEnv *, jobject);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    filesSelected
 * Signature: ([Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_filesSelected
  (JNIEnv *, jobject, jobjectArray);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    addEvalOnStartLoading
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_addEvalOnStartLoading
  (JNIEnv *, jobject, jstring);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    clearStartLoading
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl_clearStartLoading
  (JNIEnv *, jobject);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    _init
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_berkelium_java_impl_WindowImpl__1init
  (JNIEnv *, jobject, jlong);

/*
 * Class:     org_berkelium_java_impl_WindowImpl
 * Method:    _destroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_berkelium_java_impl_WindowImpl__1destroy
  (JNIEnv *, jobject, jlong);

#ifdef __cplusplus
}
#endif
#endif