[![Gifft](art/banner.png)](#)

# Gifft 🎁

[![PLATFORM](https://img.shields.io/badge/platform-Android-lightgrey)](#) [![API](https://img.shields.io/badge/API-21%2B-brightgreen)](#)

Gifft is a **work-in-progress** app to exchange gifts in a digital way.
Simply enter a textual representation of your gift, sign it and share via sms, e-mail or your favorite messenger.
When a recepient opens your link, he or she will see a gift box. It will be opened with a festive confetti.

Supported gift types for now:
- [x] Text
- [ ] Photo
- [ ] Video

AR unwrap is under development!

## Development

The app is entirely written in [Kotlin](https://kotlinlang.org/).

Architecture: multimodule, MVVM (without android arch components)

Multimodule Navigation is provided via dagger multibindings and a special all-knowing fragment factory.

### Libraries and tools

 * Kotlin Coroutines
 * AndroidX
 * Material Components
 * Dagger2 + AssistedInject
 * RxJava 2
 * ObjectBox
 * Firebase Dynamic Links
 * Lottie
 * Lint: Android Lint, Detekt
 * Unit Tests: Junit 4, Mockito
 * Integration Tests: Robolectric 4
 * UI Tests: Espresso + Kakao

### Development setup

* Add debug and release keystores and `debugKeystore.properties`, `releaseKeystore.properties`
* appCenterUploadRelease task requires `APPCENTER_API_TOKEN` parameter or environment variable

### CI setup

1. Build docker images from [ANDROID-SDK dockerfile](/android_docker/Dockerfile) and [JENKINS dockerfile](/jenkins/Dockerfile) like this:
```bash
docker build -t sidovsky/android_sdk .
docker build -t sidovsky/jenkins .
```
2. Run jenkins container
```bash
docker run --name jenkins -it -p 8080:8080 -p 50000:50000 -v $HOME/jenkins:/var/jenkins_home --group-add 0 -v /var/run/docker.sock:/var/run/docker.sock -d sidovsky/jenkins
```
3. Add .jks and *Keystore.properties files under keystore and ksProperties variables
4. In jenkins job config specify target branch and an appropriate [pipeline](/jenkins)

## Contributions

If you've found an error, please file an issue.

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request. Since this project is still in its very early stages,
if your change is substantial, please raise an issue first to discuss it.

## License

This project is licensed under the Apache License, Version 2.0 - see the [LICENSE](LICENSE) file for details.
