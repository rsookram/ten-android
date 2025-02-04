# ten

A game for Android where you swipe on numbers that add up to 10.

## Features

- Requires no permissions
- Small app (< 32 KB)
- No third-party runtime dependencies

## Build

Run the following command from the root of the repository to make a debug
build:

```shell
./gradlew assembleDebug
```

Making a release build is similar, but requires environment variables to be set
to indicate how to sign the APK:

```shell
STORE_FILE='...' STORE_PASSWORD='...' KEY_ALIAS='...' KEY_PASSWORD='...' ./gradlew assembleRelease
```

## License

[MIT](LICENSE)
