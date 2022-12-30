ARGS="1 + 2"

function run_app() {
  if [ -z "$2" ]; then
    NAME=$1
  else
    NAME=$2
  fi
  echo ""
  echo "== $1 =="
  du -sh ./test-apps/$1/build/dist-image
  time ./test-apps/$1/build/dist-image/$NAME $ARGS
}

run_app jvm-cli-app
run_app jvm-cli-app-embedded
run_app jvm-cli-app-customized app
run_app jvm-cli-app-native-binary
run_app native-cli-app
