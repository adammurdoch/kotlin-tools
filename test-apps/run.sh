ARGS="1 + 2"

echo "== jvm-cli-app =="
time ./test-apps/jvm-cli-app/build/dist-image/jvm-cli-app $ARGS

echo "== customised =="
time ./test-apps/customised/build/dist-image/app $ARGS

echo "== embedded-jvm-cli-app"
time ./test-apps/embedded-jvm-cli-app/build/dist-image/embedded-jvm-cli-app $ARGS

echo "== native-binary-jvm-cli-app"
time ./test-apps/native-binary-jvm-cli-app/build/dist-image/native-binary-jvm-cli-app $ARGS

echo "== native-cli-app"
time ./test-apps/native-cli-app/build/dist-image/native-cli-app $ARGS
