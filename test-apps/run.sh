ARGS="1 + 2"

function run_app() {
  if [ -z "$2" ]; then
    NAME=$1
  else
    NAME=$2
  fi
  DIST_IMAGE="./test-apps/$1/build/dist-image"
  LAUNCHER=""$DIST_IMAGE/$NAME""
  echo ""
  echo "== $1 =="
  du -sh "$DIST_IMAGE" || exit 1
  if [ -f "$DIST_IMAGE/jvm/bin/java" ]; then
    otool -hv "$DIST_IMAGE/jvm/bin/java"
  else
    otool -hv "$LAUNCHER"
  fi
  time "$LAUNCHER" $ARGS || exit 1
}

run_app jvm-cli-app
run_app jvm-cli-app-embedded
run_app jvm-cli-app-customized app
run_app jvm-cli-app-native-binary
run_app native-cli-app
