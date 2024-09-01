#include <stdlib.h>
#include <sys/types.h>
#include <sys/sysctl.h>
#include "net_rubygrapefruit_machine_cpu_Arch.h"

JNIEXPORT jstring
Java_net_rubygrapefruit_machine_cpu_Arch_arch(JNIEnv *env, jclass class) {
    int mib[2];
    mib[0] = CTL_HW;
    mib[1] = HW_MACHINE;
    size_t value_len;
    jstring result;
    char *value;
    if (sysctl(mib, 2, NULL, &value_len, NULL, 0) != 0) {
        return NULL;
    }
    value = malloc(value_len);
    if (sysctl(mib, 2, value, &value_len, NULL, 0) != 0) {
        free(value);
        return NULL;
    }
    result = (*env)->NewStringUTF(env, value);
    free(value);
    return result;
}
