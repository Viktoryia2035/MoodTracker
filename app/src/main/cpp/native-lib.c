#include <jni.h>
#include <string.h>
#include <stdlib.h>
#include <android/log.h>

#define LOG_TAG "NativeLib"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Структура для хранения пользователя
typedef struct {
    char *username;
    char *password;
} User;

static User *users = NULL;
static int user_count = 0;

// Функция для добавления пользователя
JNIEXPORT jboolean JNICALL
Java_com_mindhealth_EmoMind_NativeLib_addUser(JNIEnv *env, jobject obj, jstring username, jstring password) {
    const char *username_c = (*env)->GetStringUTFChars(env, username, 0);
    const char *password_c = (*env)->GetStringUTFChars(env, password, 0);

    // Проверка на существующих пользователей
    for (int i = 0; i < user_count; i++) {
        if (strcmp(users[i].username, username_c) == 0) {
            LOGE("Пользователь с именем %s уже существует.", username_c);
            (*env)->ReleaseStringUTFChars(env, username, username_c);
            (*env)->ReleaseStringUTFChars(env, password, password_c);
            return JNI_FALSE;
        }
    }

    // Увеличение массива пользователей
    users = realloc(users, sizeof(User) * (user_count + 1));
    users[user_count].username = strdup(username_c);
    users[user_count].password = strdup(password_c);
    user_count++;

    LOGI("Пользователь %s добавлен.", username_c);

    (*env)->ReleaseStringUTFChars(env, username, username_c);
    (*env)->ReleaseStringUTFChars(env, password, password_c);
    return JNI_TRUE;
}

// Функция для проверки пользователя
JNIEXPORT jboolean JNICALL
Java_com_mindhealth_EmoMind_NativeLib_isUserValid(JNIEnv *env, jobject obj, jstring username, jstring password) {
    const char *username_c = (*env)->GetStringUTFChars(env, username, 0);
    const char *password_c = (*env)->GetStringUTFChars(env, password, 0);

    for (int i = 0; i < user_count; i++) {
        if (strcmp(users[i].username, username_c) == 0 && strcmp(users[i].password, password_c) == 0) {
            LOGI("Пользователь %s проверен.", username_c);
            (*env)->ReleaseStringUTFChars(env, username, username_c);
            (*env)->ReleaseStringUTFChars(env, password, password_c);
            return JNI_TRUE;
        }
    }

    LOGE("Не удалось проверить пользователя %s.", username_c);
    (*env)->ReleaseStringUTFChars(env, username, username_c);
    (*env)->ReleaseStringUTFChars(env, password, password_c);
    return JNI_FALSE;
}

// Функция для освобождения памяти (можно вызывать при завершении приложения)
JNIEXPORT void JNICALL
Java_com_mindhealth_EmoMind_NativeLib_freeUsers(JNIEnv *env, jobject obj) {
for (int i = 0; i < user_count; i++) {
free(users[i].username);
free(users[i].password);
}
free(users);
users = NULL;
user_count = 0;
}
