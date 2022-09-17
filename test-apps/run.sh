ARGS="1 + 2"

echo ""
echo "== jvm-cli-app =="
time ./test-apps/jvm-cli-app/build/dist-image/jvm-cli-app $ARGS
du -sh ./test-apps/jvm-cli-app/build/dist-image

echo ""
echo "== customised =="
time ./test-apps/customised/build/dist-image/app $ARGS
du -sh ./test-apps/customised/build/dist-image

echo ""
echo "== embedded-jvm-cli-app"
time ./test-apps/embedded-jvm-cli-app/build/dist-image/embedded-jvm-cli-app $ARGS
du -sh ./test-apps/embedded-jvm-cli-app/build/dist-image

echo ""
echo "== native-binary-jvm-cli-app"
time ./test-apps/native-binary-jvm-cli-app/build/dist-image/native-binary-jvm-cli-app $ARGS
du -sh ./test-apps/native-binary-jvm-cli-app/build/dist-image

echo ""
echo "== native-cli-app"
time ./test-apps/native-cli-app/build/dist-image/native-cli-app $ARGS
du -sh ./test-apps/native-cli-app/build/dist-image
