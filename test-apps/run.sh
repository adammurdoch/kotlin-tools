if [ ! -d /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app/build/dist-image ]; then
   echo 'jvm-cli-app distribution "/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app/build/dist-image" not found'
   exit 1
fi
if [ ! -f /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app/build/dist-image/jvm-cli-app ]; then
   echo 'jvm-cli-app launcher "/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app/build/dist-image/jvm-cli-app" not found'
   exit 1
fi
if [ ! -d /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-customized/build/dist-image ]; then
   echo 'jvm-cli-app-customized distribution "/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-customized/build/dist-image" not found'
   exit 1
fi
if [ ! -f /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-customized/build/dist-image/app ]; then
   echo 'jvm-cli-app-customized launcher "/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-customized/build/dist-image/app" not found'
   exit 1
fi
if [ ! -d /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-embedded/build/dist-image ]; then
   echo 'jvm-cli-app-embedded distribution "/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-embedded/build/dist-image" not found'
   exit 1
fi
if [ ! -f /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-embedded/build/dist-image/jvm-cli-app-embedded ]; then
   echo 'jvm-cli-app-embedded launcher "/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-embedded/build/dist-image/jvm-cli-app-embedded" not found'
   exit 1
fi
if [ ! -f /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-embedded/build/dist-image/jvm/bin/java ]; then
   echo 'jvm-cli-app-embedded binary "/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-embedded/build/dist-image/jvm/bin/java" not found'
   exit 1
fi
if [ ! -d /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-embedded-customized/build/dist-image ]; then
   echo 'jvm-cli-app-embedded-customized distribution "/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-embedded-customized/build/dist-image" not found'
   exit 1
fi
if [ ! -f /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-embedded-customized/build/dist-image/app ]; then
   echo 'jvm-cli-app-embedded-customized launcher "/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-embedded-customized/build/dist-image/app" not found'
   exit 1
fi
if [ ! -f /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-embedded-customized/build/dist-image/jvm/bin/java ]; then
   echo 'jvm-cli-app-embedded-customized binary "/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-embedded-customized/build/dist-image/jvm/bin/java" not found'
   exit 1
fi
if [ ! -d /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-native-binary/build/dist-image ]; then
   echo 'jvm-cli-app-native-binary distribution "/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-native-binary/build/dist-image" not found'
   exit 1
fi
if [ ! -f /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-native-binary/build/dist-image/jvm-cli-app-native-binary ]; then
   echo 'jvm-cli-app-native-binary launcher "/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-native-binary/build/dist-image/jvm-cli-app-native-binary" not found'
   exit 1
fi
if [ ! -d /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-native-binary-customized/build/dist-image ]; then
   echo 'jvm-cli-app-native-binary-customized distribution "/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-native-binary-customized/build/dist-image" not found'
   exit 1
fi
if [ ! -f /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-native-binary-customized/build/dist-image/app ]; then
   echo 'jvm-cli-app-native-binary-customized launcher "/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-native-binary-customized/build/dist-image/app" not found'
   exit 1
fi
if [ ! -d /Users/adam/Documents/kotlin-tools/test-apps/jvm-ui-app/build/debug/Jvm-ui-app.app ]; then
   echo 'jvm-ui-app distribution "/Users/adam/Documents/kotlin-tools/test-apps/jvm-ui-app/build/debug/Jvm-ui-app.app" not found'
   exit 1
fi
if [ ! -f /Users/adam/Documents/kotlin-tools/test-apps/jvm-ui-app/build/debug/Jvm-ui-app.app/Contents/MacOS/Jvm-ui-app ]; then
   echo 'jvm-ui-app binary "/Users/adam/Documents/kotlin-tools/test-apps/jvm-ui-app/build/debug/Jvm-ui-app.app/Contents/MacOS/Jvm-ui-app" not found'
   exit 1
fi
if [ ! -d /Users/adam/Documents/kotlin-tools/test-apps/jvm-ui-app-customized/build/debug/App.app ]; then
   echo 'jvm-ui-app-customized distribution "/Users/adam/Documents/kotlin-tools/test-apps/jvm-ui-app-customized/build/debug/App.app" not found'
   exit 1
fi
if [ ! -f /Users/adam/Documents/kotlin-tools/test-apps/jvm-ui-app-customized/build/debug/App.app/Contents/MacOS/App ]; then
   echo 'jvm-ui-app-customized binary "/Users/adam/Documents/kotlin-tools/test-apps/jvm-ui-app-customized/build/debug/App.app/Contents/MacOS/App" not found'
   exit 1
fi
if [ ! -d /Users/adam/Documents/kotlin-tools/test-apps/native-cli-app/build/dist-image ]; then
   echo 'native-cli-app distribution "/Users/adam/Documents/kotlin-tools/test-apps/native-cli-app/build/dist-image" not found'
   exit 1
fi
if [ ! -f /Users/adam/Documents/kotlin-tools/test-apps/native-cli-app/build/dist-image/native-cli-app ]; then
   echo 'native-cli-app launcher "/Users/adam/Documents/kotlin-tools/test-apps/native-cli-app/build/dist-image/native-cli-app" not found'
   exit 1
fi
if [ ! -d /Users/adam/Documents/kotlin-tools/test-apps/native-cli-app-customized/build/dist-image ]; then
   echo 'native-cli-app-customized distribution "/Users/adam/Documents/kotlin-tools/test-apps/native-cli-app-customized/build/dist-image" not found'
   exit 1
fi
if [ ! -f /Users/adam/Documents/kotlin-tools/test-apps/native-cli-app-customized/build/dist-image/app ]; then
   echo 'native-cli-app-customized launcher "/Users/adam/Documents/kotlin-tools/test-apps/native-cli-app-customized/build/dist-image/app" not found'
   exit 1
fi
if [ ! -d /Users/adam/Documents/kotlin-tools/test-apps/native-ui-app/build/debug/Native-ui-app.app ]; then
   echo 'native-ui-app distribution "/Users/adam/Documents/kotlin-tools/test-apps/native-ui-app/build/debug/Native-ui-app.app" not found'
   exit 1
fi
if [ ! -f /Users/adam/Documents/kotlin-tools/test-apps/native-ui-app/build/debug/Native-ui-app.app/Contents/MacOS/Native-ui-app ]; then
   echo 'native-ui-app binary "/Users/adam/Documents/kotlin-tools/test-apps/native-ui-app/build/debug/Native-ui-app.app/Contents/MacOS/Native-ui-app" not found'
   exit 1
fi
if [ ! -d /Users/adam/Documents/kotlin-tools/test-apps/native-ui-app-customized/build/debug/App.app ]; then
   echo 'native-ui-app-customized distribution "/Users/adam/Documents/kotlin-tools/test-apps/native-ui-app-customized/build/debug/App.app" not found'
   exit 1
fi
if [ ! -f /Users/adam/Documents/kotlin-tools/test-apps/native-ui-app-customized/build/debug/App.app/Contents/MacOS/App ]; then
   echo 'native-ui-app-customized binary "/Users/adam/Documents/kotlin-tools/test-apps/native-ui-app-customized/build/debug/App.app/Contents/MacOS/App" not found'
   exit 1
fi

echo '==== jvm-cli-app ===='
DU_OUT=`du -sh /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app/build/dist-image | xargs | cut -f 1 -w`
echo "dist size: ${DU_OUT}"
echo
/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app/build/dist-image/jvm-cli-app 1 + 2
if [ $? != 0 ]; then
  exit 1
fi
echo

echo '==== jvm-cli-app-customized ===='
DU_OUT=`du -sh /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-customized/build/dist-image | xargs | cut -f 1 -w`
echo "dist size: ${DU_OUT}"
echo
/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-customized/build/dist-image/app 1 + 2
if [ $? != 0 ]; then
  exit 1
fi
echo

echo '==== jvm-cli-app-embedded ===='
DU_OUT=`du -sh /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-embedded/build/dist-image | xargs | cut -f 1 -w`
echo "dist size: ${DU_OUT}"
OTOOL_OUT=`otool -hv /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-embedded/build/dist-image/jvm/bin/java | sed -n '4p' | cut -f 2 -w`
echo "arch: ${OTOOL_OUT}"
echo
/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-embedded/build/dist-image/jvm-cli-app-embedded 1 + 2
if [ $? != 0 ]; then
  exit 1
fi
echo

echo '==== jvm-cli-app-embedded-customized ===='
DU_OUT=`du -sh /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-embedded-customized/build/dist-image | xargs | cut -f 1 -w`
echo "dist size: ${DU_OUT}"
OTOOL_OUT=`otool -hv /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-embedded-customized/build/dist-image/jvm/bin/java | sed -n '4p' | cut -f 2 -w`
echo "arch: ${OTOOL_OUT}"
echo
/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-embedded-customized/build/dist-image/app 1 + 2
if [ $? != 0 ]; then
  exit 1
fi
echo

echo '==== jvm-cli-app-native-binary ===='
DU_OUT=`du -sh /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-native-binary/build/dist-image | xargs | cut -f 1 -w`
echo "dist size: ${DU_OUT}"
OTOOL_OUT=`otool -hv /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-native-binary/build/dist-image/jvm-cli-app-native-binary | sed -n '4p' | cut -f 2 -w`
echo "arch: ${OTOOL_OUT}"
echo
/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-native-binary/build/dist-image/jvm-cli-app-native-binary 1 + 2
if [ $? != 0 ]; then
  exit 1
fi
echo

echo '==== jvm-cli-app-native-binary-customized ===='
DU_OUT=`du -sh /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-native-binary-customized/build/dist-image | xargs | cut -f 1 -w`
echo "dist size: ${DU_OUT}"
OTOOL_OUT=`otool -hv /Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-native-binary-customized/build/dist-image/app | sed -n '4p' | cut -f 2 -w`
echo "arch: ${OTOOL_OUT}"
echo
/Users/adam/Documents/kotlin-tools/test-apps/jvm-cli-app-native-binary-customized/build/dist-image/app 1 + 2
if [ $? != 0 ]; then
  exit 1
fi
echo

echo '==== jvm-ui-app ===='
DU_OUT=`du -sh /Users/adam/Documents/kotlin-tools/test-apps/jvm-ui-app/build/debug/Jvm-ui-app.app | xargs | cut -f 1 -w`
echo "dist size: ${DU_OUT}"
OTOOL_OUT=`otool -hv /Users/adam/Documents/kotlin-tools/test-apps/jvm-ui-app/build/debug/Jvm-ui-app.app/Contents/MacOS/Jvm-ui-app | sed -n '4p' | cut -f 2 -w`
echo "arch: ${OTOOL_OUT}"
echo
echo '(UI app)'
echo

echo '==== jvm-ui-app-customized ===='
DU_OUT=`du -sh /Users/adam/Documents/kotlin-tools/test-apps/jvm-ui-app-customized/build/debug/App.app | xargs | cut -f 1 -w`
echo "dist size: ${DU_OUT}"
OTOOL_OUT=`otool -hv /Users/adam/Documents/kotlin-tools/test-apps/jvm-ui-app-customized/build/debug/App.app/Contents/MacOS/App | sed -n '4p' | cut -f 2 -w`
echo "arch: ${OTOOL_OUT}"
echo
echo '(UI app)'
echo

echo '==== native-cli-app ===='
DU_OUT=`du -sh /Users/adam/Documents/kotlin-tools/test-apps/native-cli-app/build/dist-image | xargs | cut -f 1 -w`
echo "dist size: ${DU_OUT}"
OTOOL_OUT=`otool -hv /Users/adam/Documents/kotlin-tools/test-apps/native-cli-app/build/dist-image/native-cli-app | sed -n '4p' | cut -f 2 -w`
echo "arch: ${OTOOL_OUT}"
echo
/Users/adam/Documents/kotlin-tools/test-apps/native-cli-app/build/dist-image/native-cli-app 1 + 2
if [ $? != 0 ]; then
  exit 1
fi
echo

echo '==== native-cli-app-customized ===='
DU_OUT=`du -sh /Users/adam/Documents/kotlin-tools/test-apps/native-cli-app-customized/build/dist-image | xargs | cut -f 1 -w`
echo "dist size: ${DU_OUT}"
OTOOL_OUT=`otool -hv /Users/adam/Documents/kotlin-tools/test-apps/native-cli-app-customized/build/dist-image/app | sed -n '4p' | cut -f 2 -w`
echo "arch: ${OTOOL_OUT}"
echo
/Users/adam/Documents/kotlin-tools/test-apps/native-cli-app-customized/build/dist-image/app 1 + 2
if [ $? != 0 ]; then
  exit 1
fi
echo

echo '==== native-ui-app ===='
DU_OUT=`du -sh /Users/adam/Documents/kotlin-tools/test-apps/native-ui-app/build/debug/Native-ui-app.app | xargs | cut -f 1 -w`
echo "dist size: ${DU_OUT}"
OTOOL_OUT=`otool -hv /Users/adam/Documents/kotlin-tools/test-apps/native-ui-app/build/debug/Native-ui-app.app/Contents/MacOS/Native-ui-app | sed -n '4p' | cut -f 2 -w`
echo "arch: ${OTOOL_OUT}"
echo
echo '(UI app)'
echo

echo '==== native-ui-app-customized ===='
DU_OUT=`du -sh /Users/adam/Documents/kotlin-tools/test-apps/native-ui-app-customized/build/debug/App.app | xargs | cut -f 1 -w`
echo "dist size: ${DU_OUT}"
OTOOL_OUT=`otool -hv /Users/adam/Documents/kotlin-tools/test-apps/native-ui-app-customized/build/debug/App.app/Contents/MacOS/App | sed -n '4p' | cut -f 2 -w`
echo "arch: ${OTOOL_OUT}"
echo
echo '(UI app)'
echo
