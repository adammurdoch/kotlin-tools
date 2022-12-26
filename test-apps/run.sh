ARGS="1 + 2"

function run_app() {
  echo ""
  echo "== $1 =="
  du -sh ./test-apps/$1/build/dist-image
  time ./test-apps/$1/build/dist-image/$1 $ARGS
}

run_app jvm-cli-app
run_app jvm-cli-app-embedded
run_app jvm-cli-app-customized
run_app jvm-cli-app-native-binary
run_app native-cli-app
