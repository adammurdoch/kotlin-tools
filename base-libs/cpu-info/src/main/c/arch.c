#include <stdlib.h>
#include <sys/types.h>
#include <sys/sysctl.h>
#include "net_rubygrapefruit_machine_cpu_Arch.h"

JNIEXPORT jstring
Java_net_rubygrapefruit_machine_cpu_Arch_arch(JNIEnv *env, jclass class) {
    int mib[5];
    size_t len = 5;
    size_t value_len;
    jstring result;
    char *value;

    if (sysctlnametomib("machdep.cpu.brand_string", mib, &len) != 0) {
        return NULL;
    }

    if (sysctl(mib, len, NULL, &value_len, NULL, 0) != 0) {
        return NULL;
    }
    value = malloc(value_len);
    if (sysctl(mib, len, value, &value_len, NULL, 0) != 0) {
        free(value);
        return NULL;
    }
    result = (*env)->NewStringUTF(env, value);
    free(value);
    return result;
}
